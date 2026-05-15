package billeteradigitalback.billeteradigitalback.Service;

import billeteradigitalback.billeteradigitalback.Enums.EstadoOperacionProgramada;
import billeteradigitalback.billeteradigitalback.Enums.TipoAlerta;
import billeteradigitalback.billeteradigitalback.Enums.TipoTransaccion;
import billeteradigitalback.billeteradigitalback.Model.Billetera;
import billeteradigitalback.billeteradigitalback.Model.OperacionProgramada;
import billeteradigitalback.billeteradigitalback.Model.Usuario;
import billeteradigitalback.billeteradigitalback.Repository.OperacionProgramadaRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class OperacionProgramadaService {

    // =========================================================
    // DEPENDENCIAS
    // =========================================================

    private final OperacionProgramadaRepository operacionRepository;
    private final TransaccionService transaccionService;
    private final BilleteraService billeteraService;
    private final AlertaService alertaService;
    private final UsuarioService usuarioService;

    public OperacionProgramadaService(
            OperacionProgramadaRepository operacionRepository,
            @Lazy TransaccionService transaccionService,
            BilleteraService billeteraService,
            AlertaService alertaService,
            UsuarioService usuarioService) {
        this.operacionRepository = operacionRepository;
        this.transaccionService = transaccionService;
        this.billeteraService = billeteraService;
        this.alertaService = alertaService;
        this.usuarioService = usuarioService;
    }

    // =========================================================
    // ESTRUCTURAS DE DATOS EN MEMORIA
    // =========================================================

    /**
     * COLA DE PRIORIDAD — PriorityQueue<OperacionProgramada>
     * Ordena las operaciones por fechaFutura ascendente (min-heap).
     * La operación más próxima siempre está en el tope.
     *
     * Por qué no una lista simple: con una lista habría que recorrerla
     * toda para encontrar la más próxima — O(n). Con la PriorityQueue
     * el peek() es O(1) y el poll() es O(log n).
     */
    private final PriorityQueue<OperacionProgramada> colaPrioridad =
            new PriorityQueue<>(Comparator.comparing(OperacionProgramada::getFechaFutura));

    /**
     * COLA FIFO — LinkedList como Queue
     * Operaciones que fallaron y esperan reintento.
     * Se reintenta en el siguiente ciclo del scheduler.
     * O(1) enqueue/dequeue.
     */
    private final Queue<OperacionProgramada> colaReintentos = new LinkedList<>();

    // =========================================================
    // INICIALIZACIÓN — cargar pendientes en la cola
    // =========================================================

    @PostConstruct
    public void inicializarCola() {
        List<OperacionProgramada> pendientes = operacionRepository
                .findByEstadoOrderByFechaFuturaAsc(EstadoOperacionProgramada.PENDIENTE);

        pendientes.forEach(colaPrioridad::offer);

        System.out.println("[OperacionProgramadaService] Cola inicializada con "
                + pendientes.size() + " operaciones pendientes.");
    }

    // =========================================================
    // PROGRAMAR OPERACIONES
    // =========================================================

    public OperacionProgramada programar(Long billeteraOrigenId,
                                         Long billeteraDestinoId,
                                         BigDecimal monto,
                                         LocalDateTime fechaFutura,
                                         String tipoTransaccion,
                                         String descripcion,
                                         boolean recurrente,
                                         int diasRecurrencia) {
        Billetera origen = billeteraService.buscarPorId(billeteraOrigenId);
        Billetera destino = billeteraDestinoId != null
                ? billeteraService.buscarPorId(billeteraDestinoId)
                : null;

        OperacionProgramada op = new OperacionProgramada();
        op.setBilleteraOrigen(origen);
        op.setBilleteraDestino(destino);
        op.setMonto(monto);
        op.setFechaFutura(fechaFutura);
        op.setTipoTransaccion(
                billeteradigitalback.billeteradigitalback.Enums.TipoTransaccion
                        .valueOf(tipoTransaccion));
        op.setDescripcion(descripcion);
        op.setRecurrente(recurrente);
        op.setDiasRecurrencia(diasRecurrencia);
        op.setEstado(EstadoOperacionProgramada.PENDIENTE);
        op.setPrioridad(calcularPrioridad(fechaFutura));

        OperacionProgramada guardada = operacionRepository.save(op);

        // Insertar en cola de prioridad — O(log n)
        colaPrioridad.offer(guardada);

        // Alerta si es en las próximas 24 horas
        if (fechaFutura.isBefore(LocalDateTime.now().plusHours(24))) {
            alertaService.crearAlertaOperacionProgramadaProxima(
                    origen.getUsuario(), descripcion);
        }

        return guardada;
    }

    public void cancelar(Long operacionId) {
        OperacionProgramada op = operacionRepository.findById(operacionId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Operación no encontrada: " + operacionId));

        if (op.getEstado() != EstadoOperacionProgramada.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se pueden cancelar operaciones PENDIENTES.");
        }

        op.setEstado(EstadoOperacionProgramada.CANCELADA);
        operacionRepository.save(op);

        // Remover de la cola — O(n) aceptable para cancelaciones
        colaPrioridad.remove(op);
    }

    // =========================================================
    // PROCESAMIENTO AUTOMÁTICO — @Scheduled
    // =========================================================

    /**
     * Se ejecuta cada 60 segundos automáticamente.
     * Lee la cola de prioridad y ejecuta las operaciones cuya fecha ya llegó.
     * peek() verifica el tope sin remover — O(1).
     * poll() remueve el tope — O(log n).
     */
    @Scheduled(fixedDelay = 60_000)
    public void procesarCola() {
        LocalDateTime ahora = LocalDateTime.now();
        int ejecutadas = 0;

        // Mientras haya operaciones y la más próxima ya venció
        while (!colaPrioridad.isEmpty()
                && colaPrioridad.peek().getFechaFutura().isBefore(ahora)) {

            OperacionProgramada op = colaPrioridad.poll(); // O(log n)
            ejecutarOperacion(op);
            ejecutadas++;
        }

        // Procesar reintentos
        procesarReintentos();

        if (ejecutadas > 0) {
            System.out.println("[Scheduler] " + ejecutadas
                    + " operaciones ejecutadas a las " + ahora);
        }
    }

    /**
     * Alerta de operaciones próximas — cada hora.
     */
    @Scheduled(fixedDelay = 3_600_000)
    public void alertarProximas() {
        LocalDateTime en2Horas = LocalDateTime.now().plusHours(2);

        colaPrioridad.stream()
                .filter(op -> op.getFechaFutura().isBefore(en2Horas))
                .forEach(op -> alertaService.crearAlertaOperacionProgramadaProxima(
                        op.getBilleteraOrigen().getUsuario(),
                        op.getDescripcion() + " — $" + op.getMonto()));
    }

    // =========================================================
    // CONSULTAS
    // =========================================================

    public List<OperacionProgramada> listarPendientes() {
        // Devuelve snapshot de la cola (no la modifica)
        return new ArrayList<>(colaPrioridad);
    }

    public List<OperacionProgramada> listarPorUsuario(Long usuarioId) {
        return operacionRepository.findByUsuarioId(usuarioId);
    }

    public int cantidadPendientes() {
        return colaPrioridad.size();
    }

    // =========================================================
    // HELPERS PRIVADOS
    // =========================================================

    private void ejecutarOperacion(OperacionProgramada op) {
        try {
            switch (op.getTipoTransaccion()) {
                case TRANSFERENCIA -> transaccionService.transferir(
                        op.getBilleteraOrigen().getId(),
                        op.getBilleteraDestino().getId(),
                        op.getMonto(),
                        op.getDescripcion());

                case RECARGA -> transaccionService.recargar(
                        op.getBilleteraOrigen().getId(),
                        op.getMonto(),
                        op.getDescripcion());

                case RETIRO -> transaccionService.retirar(
                        op.getBilleteraOrigen().getId(),
                        op.getMonto(),
                        op.getDescripcion());
            }

            op.setEstado(EstadoOperacionProgramada.EJECUTADA);
            operacionRepository.save(op);

            // Bono de puntos por operación programada exitosa
            usuarioService.agregarPuntos(
                    op.getBilleteraOrigen().getUsuario().getId(), 50);

            // Si es recurrente, reagendar automáticamente
            if (op.isRecurrente() && op.getDiasRecurrencia() > 0) {
                reagendar(op);
            }

        } catch (Exception e) {
            op.setEstado(EstadoOperacionProgramada.FALLIDA);
            operacionRepository.save(op);

            // Encolar para reintento — O(1)
            colaReintentos.offer(op);

            alertaService.crearAlertaOperacionRechazada(
                    op.getBilleteraOrigen().getUsuario(),
                    e.getMessage());
        }
    }

    private void procesarReintentos() {
        int cantidad = colaReintentos.size();
        for (int i = 0; i < cantidad; i++) {
            OperacionProgramada op = colaReintentos.poll();
            if (op != null) {
                // Reagendar para 5 minutos después
                op.setFechaFutura(LocalDateTime.now().plusMinutes(5));
                op.setEstado(EstadoOperacionProgramada.PENDIENTE);
                colaPrioridad.offer(operacionRepository.save(op));
            }
        }
    }

    private void reagendar(OperacionProgramada original) {
        OperacionProgramada siguiente = new OperacionProgramada(
                original.getFechaFutura().plusDays(original.getDiasRecurrencia()),
                original.getTipoTransaccion(),
                original.getMonto(),
                calcularPrioridad(original.getFechaFutura()
                        .plusDays(original.getDiasRecurrencia())),
                original.getDescripcion(),
                original.isRecurrente(),
                original.getDiasRecurrencia(),
                EstadoOperacionProgramada.PENDIENTE,
                original.getUsuario(),
                original.getBilleteraOrigen(),
                original.getBilleteraDestino()
        );

        OperacionProgramada guardada = operacionRepository.save(siguiente);
        colaPrioridad.offer(guardada);
    }

    /**
     * Prioridad numérica: mientras más próxima la fecha, mayor prioridad.
     * 1 = más urgente, 5 = menos urgente.
     */
    private int calcularPrioridad(LocalDateTime fecha) {
        long horas = java.time.Duration.between(LocalDateTime.now(), fecha).toHours();
        if (horas <= 1)   return 1;
        if (horas <= 6)   return 2;
        if (horas <= 24)  return 3;
        if (horas <= 72)  return 4;
        return 5;
    }
}