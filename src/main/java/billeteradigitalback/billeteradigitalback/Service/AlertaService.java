package billeteradigitalback.billeteradigitalback.Service;

import billeteradigitalback.billeteradigitalback.Enums.TipoAlerta;
import billeteradigitalback.billeteradigitalback.Model.Alerta;
import billeteradigitalback.billeteradigitalback.Model.Usuario;
import billeteradigitalback.billeteradigitalback.Repository.AlertaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class AlertaService {

    private final AlertaRepository alertaRepository;

    public AlertaService(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    // =========================================================
    // ESTRUCTURAS DE DATOS EN MEMORIA
    // =========================================================

    /**
     * COLA FIFO — LinkedList como Queue<Alerta>
     * Alertas generadas que aún no han sido "despachadas" (enviadas al cliente).
     * Se procesan en el orden en que llegaron — primero en entrar, primero en salir.
     * O(1) para encolar (offer) y desencolar (poll).
     */
    private final Queue<Alerta> colaPendientes = new LinkedList<>();

    /**
     * DEQUE por usuario — HashMap<Long, ArrayDeque<Alerta>>
     * Cada usuario tiene su propio deque con sus últimas MAX_RECIENTES alertas.
     * Cuando llega una nueva y ya se llegó al límite, se elimina la más antigua
     * con pollFirst() — O(1).
     * Permite consultar el historial reciente sin ir a la BD.
     */
    private final Map<Long, Deque<Alerta>> historialReciente = new HashMap<>();
    private static final int MAX_RECIENTES = 20;

    // =========================================================
    // CREAR ALERTAS
    // =========================================================

    /**
     * Método central para crear cualquier alerta.
     * Guarda en BD, encola en FIFO y agrega al deque del usuario.
     */
    public Alerta crearAlerta(Usuario usuario, String mensaje, TipoAlerta tipo) {
        Alerta alerta = new Alerta(mensaje, tipo, LocalDateTime.now(), false, usuario);

        // 1. Persistir en BD
        Alerta guardada = alertaRepository.save(alerta);

        // 2. Encolar en FIFO — O(1)
        colaPendientes.offer(guardada);

        // 3. Agregar al deque del usuario — O(1)
        Deque<Alerta> deque = historialReciente
                .computeIfAbsent(usuario.getId(), k -> new ArrayDeque<>());
        deque.addLast(guardada);

        // Si supera el límite, elimina la más antigua
        if (deque.size() > MAX_RECIENTES) {
            deque.pollFirst();
        }

        return guardada;
    }

    // Métodos de conveniencia para los tipos más usados
    public void crearAlertaSaldoBajo(Usuario usuario, String nombreBilletera) {
        crearAlerta(usuario,
                "Saldo bajo en billetera '" + nombreBilletera + "'",
                TipoAlerta.SALDO_BAJO);
    }

    public void crearAlertaOperacionRechazada(Usuario usuario, String motivo) {
        crearAlerta(usuario,
                "Operación rechazada: " + motivo,
                TipoAlerta.OPERACION_RECHAZADA);
    }

    public void crearAlertaSeguridad(Usuario usuario, String detalle) {
        crearAlerta(usuario,
                "⚠ Alerta de seguridad: " + detalle,
                TipoAlerta.ALERTA_SEGURIDAD);
    }

    public void crearAlertaOperacionProgramadaProxima(Usuario usuario, String descripcion) {
        crearAlerta(usuario,
                "Operación programada próxima: " + descripcion,
                TipoAlerta.OPERACION_PROGRAMADA_PROXIMA);
    }

    // =========================================================
    // DESPACHAR COLA FIFO
    // =========================================================

    /**
     * Procesa y marca como despachadas las alertas pendientes.
     * poll() saca del frente de la cola — O(1).
     * En producción aquí iría el envío por WebSocket, email o push.
     */
    public List<Alerta> despacharPendientes() {
        List<Alerta> despachadas = new ArrayList<>();
        while (!colaPendientes.isEmpty()) {
            Alerta a = colaPendientes.poll();
            despachadas.add(a);
        }
        return despachadas;
    }

    // =========================================================
    // CONSULTAS
    // =========================================================

    /**
     * Historial reciente desde el deque en memoria — O(1).
     * Si el usuario no tiene deque en memoria, va a BD.
     */
    public List<Alerta> obtenerRecientes(Long usuarioId) {
        Deque<Alerta> deque = historialReciente.get(usuarioId);
        if (deque != null && !deque.isEmpty()) {
            return new ArrayList<>(deque);
        }
        return alertaRepository.findTop20ByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    public List<Alerta> obtenerTodas(Long usuarioId) {
        return alertaRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    public List<Alerta> obtenerNoLeidas(Long usuarioId) {
        return alertaRepository.findByUsuarioIdAndLeidaFalseOrderByFechaDesc(usuarioId);
    }

    public long contarNoLeidas(Long usuarioId) {
        return alertaRepository.countByUsuarioIdAndLeidaFalse(usuarioId);
    }

    public void marcarComoLeida(Long alertaId) {
        Alerta alerta = alertaRepository.findById(alertaId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Alerta no encontrada: " + alertaId));
        alerta.setLeida(true);
        alertaRepository.save(alerta);
    }

    public void marcarTodasComoLeidas(Long usuarioId) {
        alertaRepository.findByUsuarioIdAndLeidaFalseOrderByFechaDesc(usuarioId)
                .forEach(a -> {
                    a.setLeida(true);
                    alertaRepository.save(a);
                });
    }

    public int contarPendientes() {
        return colaPendientes.size();
    }
}