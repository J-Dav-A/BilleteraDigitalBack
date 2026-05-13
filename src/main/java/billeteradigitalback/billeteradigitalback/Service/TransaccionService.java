package billeteradigitalback.billeteradigitalback.Service;

import billeteradigitalback.billeteradigitalback.Enums.*;
import billeteradigitalback.billeteradigitalback.Model.Billetera;
import billeteradigitalback.billeteradigitalback.Model.Transaccion;
import billeteradigitalback.billeteradigitalback.Model.Usuario;
import billeteradigitalback.billeteradigitalback.Repository.TransaccionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class TransaccionService {

    // =========================================================
    // DEPENDENCIAS
    // =========================================================

    private final TransaccionRepository transaccionRepository;
    private final BilleteraService billeteraService;
    private final UsuarioService usuarioService;
    private final AlertaService alertaService;

    // @Lazy para evitar dependencia circular con OperacionProgramadaService
    public TransaccionService(TransaccionRepository transaccionRepository,
                              BilleteraService billeteraService,
                              UsuarioService usuarioService,
                              @Lazy AlertaService alertaService) {
        this.transaccionRepository = transaccionRepository;
        this.billeteraService = billeteraService;
        this.usuarioService = usuarioService;
        this.alertaService = alertaService;
    }

    // =========================================================
    // ESTRUCTURAS DE DATOS EN MEMORIA
    // =========================================================

    /**
     * PILA — ArrayDeque<Transaccion> usada como stack (LIFO)
     * Una pila por usuario: clave = usuarioId, valor = su pila.
     *
     * Cada transacción ejecutada se apila con push().
     * Al revertir, se hace pop() para obtener la última — O(1).
     * Solo se apilan transacciones REVERSIBLES (no retiros de seguridad, etc.)
     */
    private final Map<Long, Deque<Transaccion>> pilasReversion = new HashMap<>();

    /**
     * GRAFO DIRIGIDO — HashMap<Long, Map<Long, int[]>>
     * Representa transferencias entre usuarios.
     * Clave externa = usuarioId origen
     * Clave interna = usuarioId destino
     * int[] = {cantidadTransferencias, montoTotal (como long de centavos)}
     *
     * Permite:
     * - Detectar rutas frecuentes de dinero
     * - Encontrar ciclos (posible fraude)
     * - Analizar patrones de interacción
     */
    private final Map<Long, Map<Long, int[]>> grafoTransferencias = new HashMap<>();

    // =========================================================
    // INICIALIZACIÓN — reconstruir pila y grafo desde BD
    // =========================================================

    @PostConstruct
    public void inicializarEstructuras() {
        // Reconstruir grafo desde historial de transferencias
        List<Transaccion> transferencias = transaccionRepository.findTodasLasTransferencias();
        for (Transaccion t : transferencias) {
            if (t.getBilleteraOrigen() != null && t.getBilleteraDestino() != null) {
                Long origen  = t.getBilleteraOrigen().getUsuario().getId();
                Long destino = t.getBilleteraDestino().getUsuario().getId();
                agregarAristaGrafo(origen, destino, t.getValor());
            }
        }
        System.out.println("[TransaccionService] Grafo reconstruido con "
                + transferencias.size() + " transferencias.");
    }

    // =========================================================
    // OPERACIONES FINANCIERAS
    // =========================================================

    /**
     * RECARGA — aumenta saldo de la billetera destino.
     * billeteraOrigen = null (el dinero viene de afuera)
     */
    public Transaccion recargar(Long billeteraDestinoId, BigDecimal monto,
                                String descripcion) {
        Billetera destino = billeteraService.buscarPorId(billeteraDestinoId);

        billeteraService.acreditar(billeteraDestinoId, monto);

        Transaccion tx = construirTransaccion(
                TipoTransaccion.RECARGA, monto,
                null, destino, descripcion);
        Transaccion guardada = transaccionRepository.save(tx);

        // Calcular y sumar puntos: 1 punto por cada 100
        int puntos = calcularPuntos(TipoTransaccion.RECARGA, monto);
        usuarioService.agregarPuntos(destino.getUsuario().getId(), puntos);
        guardada.setPuntosGenerados(puntos);
        transaccionRepository.save(guardada);

        // Apilar en la pila del usuario — O(1)
        apilar(destino.getUsuario().getId(), guardada);

        alertaService.crearAlerta(
                destino.getUsuario(),
                "Recarga de $" + monto + " en '" + destino.getNombre() + "' exitosa.",
                TipoAlerta.OPERACION_EXITOSA);

        return guardada;
    }

    /**
     * RETIRO — disminuye saldo de la billetera origen.
     * billeteraDestino = null (el dinero sale a afuera)
     */
    public Transaccion retirar(Long billeteraOrigenId, BigDecimal monto,
                               String descripcion) {
        Billetera origen = billeteraService.buscarPorId(billeteraOrigenId);

        billeteraService.debitar(billeteraOrigenId, monto);

        Transaccion tx = construirTransaccion(
                TipoTransaccion.RETIRO, monto,
                origen, null, descripcion);
        Transaccion guardada = transaccionRepository.save(tx);

        int puntos = calcularPuntos(TipoTransaccion.RETIRO, monto);
        usuarioService.agregarPuntos(origen.getUsuario().getId(), puntos);
        guardada.setPuntosGenerados(puntos);
        transaccionRepository.save(guardada);

        apilar(origen.getUsuario().getId(), guardada);

        return guardada;
    }

    /**
     * TRANSFERENCIA — débita origen y acredita destino.
     * Actualiza el grafo con la nueva arista.
     */
    public Transaccion transferir(Long billeteraOrigenId, Long billeteraDestinoId,
                                  BigDecimal monto, String descripcion) {
        Billetera origen  = billeteraService.buscarPorId(billeteraOrigenId);
        Billetera destino = billeteraService.buscarPorId(billeteraDestinoId);

        // Validar saldo antes de operar
        billeteraService.validarSaldoSuficiente(billeteraOrigenId, monto);

        // Analizar riesgo ANTES de ejecutar
        NivelRiesgo riesgo = analizarRiesgo(
                origen.getUsuario().getId(), billeteraOrigenId, monto);

        // Ejecutar movimiento
        billeteraService.debitar(billeteraOrigenId, monto);
        billeteraService.acreditar(billeteraDestinoId, monto);

        Transaccion tx = construirTransaccion(
                TipoTransaccion.TRANSFERENCIA, monto,
                origen, destino, descripcion);
        tx.setNivelRiesgo(riesgo);
        Transaccion guardada = transaccionRepository.save(tx);

        // Actualizar grafo de transferencias
        agregarAristaGrafo(
                origen.getUsuario().getId(),
                destino.getUsuario().getId(),
                monto);

        int puntos = calcularPuntos(TipoTransaccion.TRANSFERENCIA, monto);
        usuarioService.agregarPuntos(origen.getUsuario().getId(), puntos);
        guardada.setPuntosGenerados(puntos);
        transaccionRepository.save(guardada);

        apilar(origen.getUsuario().getId(), guardada);

        // Alerta si fue marcada como riesgo alto
        if (riesgo == NivelRiesgo.ALTO) {
            alertaService.crearAlertaSeguridad(
                    origen.getUsuario(),
                    "Transferencia de $" + monto + " marcada como riesgo alto.");
        }

        return guardada;
    }

    // =========================================================
    // REVERSIÓN — usa la Pila (LIFO)
    // =========================================================

    /**
     * Revierte la ÚLTIMA transacción del usuario.
     * pop() de la pila → deshace el movimiento → descuenta puntos.
     */
    public Transaccion revertirUltima(Long usuarioId) {
        Deque<Transaccion> pila = pilasReversion.get(usuarioId);

        if (pila == null || pila.isEmpty()) {
            throw new IllegalStateException(
                    "No hay transacciones reversibles para el usuario: " + usuarioId);
        }

        // Pop de la pila — O(1)
        Transaccion tx = pila.pop();
        return revertirTransaccion(tx.getPid());
    }

    /**
     * Revierte una transacción específica por ID.
     */
    public Transaccion revertirTransaccion(Long transaccionId) {
        Transaccion tx = transaccionRepository.findById(transaccionId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Transacción no encontrada: " + transaccionId));

        if (tx.getEstado() == EstadoTransaccion.REVERTIDA) {
            throw new IllegalArgumentException(
                    "La transacción ya fue revertida: " + transaccionId);
        }

        // Deshacer el movimiento de saldo según el tipo
        switch (tx.getTipoTransaccion()) {
            case RECARGA -> {
                // Recarga: se acreditó destino → ahora se debita
                billeteraService.debitar(tx.getBilleteraDestino().getId(), tx.getValor());
            }
            case RETIRO -> {
                // Retiro: se debitó origen → ahora se acredita
                billeteraService.acreditar(tx.getBilleteraOrigen().getId(), tx.getValor());
            }
            case TRANSFERENCIA -> {
                // Transferencia: se debitó origen y acreditó destino → invertir
                billeteraService.acreditar(tx.getBilleteraOrigen().getId(), tx.getValor());
                billeteraService.debitar(tx.getBilleteraDestino().getId(), tx.getValor());
            }
        }

        // Actualizar estado de la transacción
        tx.setEstado(EstadoTransaccion.REVERTIDA);
        Transaccion guardada = transaccionRepository.save(tx);

        // Descontar los puntos que se ganaron con esta transacción
        if (tx.getPuntosGenerados() > 0) {
            Long propietarioId = tx.getBilleteraOrigen() != null
                    ? tx.getBilleteraOrigen().getUsuario().getId()
                    : tx.getBilleteraDestino().getUsuario().getId();
            usuarioService.canjearPuntos(propietarioId, tx.getPuntosGenerados());
        }

        // Notificar
        Usuario usuario = tx.getBilleteraOrigen() != null
                ? tx.getBilleteraOrigen().getUsuario()
                : tx.getBilleteraDestino().getUsuario();
        alertaService.crearAlerta(
                usuario,
                "Transacción #" + transaccionId + " revertida exitosamente.",
                TipoAlerta.INFO);

        return guardada;
    }

    // =========================================================
    // HISTORIAL (Listas)
    // =========================================================

    public List<Transaccion> historialPorBilletera(Long billeteraId) {
        return transaccionRepository.findByBilleteraId(billeteraId);
    }

    public List<Transaccion> historialPorUsuario(Long usuarioId) {
        return transaccionRepository.findByUsuarioId(usuarioId);
    }

    public List<Transaccion> historialEntreFechas(Long usuarioId,
                                                  LocalDateTime desde,
                                                  LocalDateTime hasta) {
        return transaccionRepository.findByUsuarioIdEntreFechas(usuarioId, desde, hasta);
    }

    public List<Transaccion> topPorValor(int n) {
        return transaccionRepository.findTopPorValor(PageRequest.of(0, n));
    }

    public Transaccion buscarPorId(Long id) {
        return transaccionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Transacción no encontrada: " + id));
    }

    // =========================================================
    // GRAFO DE TRANSFERENCIAS
    // =========================================================

    /**
     * Usuarios alcanzables desde un usuario usando BFS.
     * Recorre el grafo en anchura — O(V + E).
     */
    public List<Long> bfsAlcanzables(Long usuarioOrigenId) {
        List<Long> visitados = new ArrayList<>();
        Queue<Long> cola = new LinkedList<>();
        Set<Long> vistos = new HashSet<>();

        cola.offer(usuarioOrigenId);
        vistos.add(usuarioOrigenId);

        while (!cola.isEmpty()) {
            Long actual = cola.poll();
            visitados.add(actual);

            Map<Long, int[]> vecinos = grafoTransferencias
                    .getOrDefault(actual, Collections.emptyMap());
            for (Long vecino : vecinos.keySet()) {
                if (vistos.add(vecino)) {
                    cola.offer(vecino);
                }
            }
        }
        return visitados;
    }

    /**
     * Detecta si hay ciclos en el grafo usando DFS.
     * Un ciclo puede indicar "carrusel" de dinero (posible fraude).
     */
    public boolean hayciClos() {
        Set<Long> visitados = new HashSet<>();
        Set<Long> enPila = new HashSet<>();

        for (Long nodo : grafoTransferencias.keySet()) {
            if (!visitados.contains(nodo)) {
                if (dfsBuscarCiclo(nodo, visitados, enPila)) return true;
            }
        }
        return false;
    }

    /** Relaciones más frecuentes entre usuarios */
    public List<String> relacionesFrecuentes(int top) {
        record Relacion(Long de, Long a, int veces) {}
        List<Relacion> lista = new ArrayList<>();

        grafoTransferencias.forEach((de, destinos) ->
                destinos.forEach((a, datos) ->
                        lista.add(new Relacion(de, a, datos[0]))));

        return lista.stream()
                .sorted(Comparator.comparingInt(Relacion::veces).reversed())
                .limit(top)
                .map(r -> "Usuario " + r.de() + " → Usuario " + r.a()
                        + " (" + r.veces() + " veces)")
                .toList();
    }

    public Map<Long, Map<Long, int[]>> obtenerGrafo() {
        return Collections.unmodifiableMap(grafoTransferencias);
    }

    // =========================================================
    // ANTIFRAUDE — análisis de riesgo
    // =========================================================

    /**
     * Analiza el riesgo de una transacción antes de ejecutarla.
     * Detecta ráfagas de transacciones y montos inusualmente altos.
     */
    private NivelRiesgo analizarRiesgo(Long usuarioId, Long billeteraId,
                                       BigDecimal monto) {
        int flags = 0;

        // Flag 1: muchas transacciones en el último minuto (ráfaga)
        LocalDateTime ventana = LocalDateTime.now().minusMinutes(1);
        List<Transaccion> recientes = transaccionRepository
                .findRecientesDeUsuario(usuarioId, ventana);
        if (recientes.size() >= 5) flags++;

        // Flag 2: monto inusualmente alto (más de 3x el promedio)
        BigDecimal promedio = transaccionRepository.promedioValorDeUsuario(usuarioId);
        if (promedio != null && promedio.compareTo(BigDecimal.ZERO) > 0) {
            if (monto.compareTo(promedio.multiply(BigDecimal.valueOf(3))) > 0) flags++;
        }

        // Flag 3: horario inusual (madrugada)
        int hora = LocalDateTime.now().getHour();
        if (hora >= 0 && hora < 5) flags++;

        return switch (flags) {
            case 0  -> NivelRiesgo.NINGUNO;
            case 1  -> NivelRiesgo.BAJO;
            case 2  -> NivelRiesgo.MEDIO;
            default -> NivelRiesgo.ALTO;
        };
    }

    // =========================================================
    // ANALYTICS
    // =========================================================

    public BigDecimal montoTotalEntreFechas(LocalDateTime desde, LocalDateTime hasta) {
        return transaccionRepository.sumaTotalEntreFechas(desde, hasta);
    }

    public List<Object[]> frecuenciaPorTipo() {
        return transaccionRepository.contarPorTipo();
    }

    public List<Object[]> usuariosMasActivos(int top) {
        return transaccionRepository.findUsuariosMasActivos(PageRequest.of(0, top));
    }

    // =========================================================
    // HELPERS PRIVADOS
    // =========================================================

    private void apilar(Long usuarioId, Transaccion tx) {
        pilasReversion
                .computeIfAbsent(usuarioId, k -> new ArrayDeque<>())
                .push(tx); // push = addFirst → O(1)
    }

    private void agregarAristaGrafo(Long origen, Long destino, BigDecimal monto) {
        grafoTransferencias
                .computeIfAbsent(origen, k -> new HashMap<>())
                .merge(destino,
                        new int[]{1, monto.intValue()},
                        (existente, nuevo) -> new int[]{
                                existente[0] + 1,
                                existente[1] + nuevo[1]
                        });
    }

    private boolean dfsBuscarCiclo(Long nodo, Set<Long> visitados, Set<Long> enPila) {
        visitados.add(nodo);
        enPila.add(nodo);

        for (Long vecino : grafoTransferencias
                .getOrDefault(nodo, Collections.emptyMap()).keySet()) {
            if (!visitados.contains(vecino)) {
                if (dfsBuscarCiclo(vecino, visitados, enPila)) return true;
            } else if (enPila.contains(vecino)) {
                return true; // ciclo encontrado
            }
        }

        enPila.remove(nodo);
        return false;
    }

    private Transaccion construirTransaccion(TipoTransaccion tipo, BigDecimal valor,
                                             Billetera origen, Billetera destino,
                                             String descripcion) {
        return new Transaccion(
                LocalDateTime.now(), tipo, valor,
                origen, destino,
                EstadoTransaccion.COMPLETADA, 0, NivelRiesgo.NINGUNO);
    }

    /**
     * Política de puntos:
     * Recarga:       1 punto por cada 100
     * Retiro:        2 puntos por cada 100
     * Transferencia: 3 puntos por cada 100
     */
    public int calcularPuntos(TipoTransaccion tipo, BigDecimal monto) {
        int unidades = monto.divide(BigDecimal.valueOf(100)).intValue();
        return switch (tipo) {
            case RECARGA       -> unidades * 1;
            case RETIRO        -> unidades * 2;
            case TRANSFERENCIA -> unidades * 3;
            default            -> 0;
        };
    }
}