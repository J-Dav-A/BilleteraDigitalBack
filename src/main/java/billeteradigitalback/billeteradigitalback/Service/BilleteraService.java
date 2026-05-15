package billeteradigitalback.billeteradigitalback.Service;

import billeteradigitalback.billeteradigitalback.Enums.TipoBilletera;
import billeteradigitalback.billeteradigitalback.Enums.TipoAlerta;
import billeteradigitalback.billeteradigitalback.Model.Billetera;
import billeteradigitalback.billeteradigitalback.Model.Usuario;
import billeteradigitalback.billeteradigitalback.Repository.BilleteraRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
public class BilleteraService {

    // =========================================================
    // DEPENDENCIAS
    // =========================================================

    private final BilleteraRepository billeteraRepository;
    private final UsuarioService usuarioService;
    private final AlertaService alertaService;

    public BilleteraService(BilleteraRepository billeteraRepository,
                            UsuarioService usuarioService,
                            @Lazy AlertaService alertaService) {
        this.billeteraRepository = billeteraRepository;
        this.usuarioService = usuarioService;
        this.alertaService = alertaService;
    }

    // =========================================================
    // ESTRUCTURA DE DATOS EN MEMORIA
    // =========================================================

    /**
     * TABLA HASH — HashMap<Long, Billetera>
     * Acceso O(1) por ID de billetera.
     * Evita consultas a BD en operaciones frecuentes como
     * verificar saldo antes de cada transacción.
     */
    private final Map<Long, Billetera> cacheBilleteras = new HashMap<>();

    // Umbral para alertar saldo bajo
    private static final BigDecimal UMBRAL_SALDO_BAJO = new BigDecimal("50.00");

    // =========================================================
    // INICIALIZACIÓN
    // =========================================================

    @PostConstruct
    public void inicializarEstructuras() {
        List<Billetera> todas = billeteraRepository.findAll();
        for (Billetera b : todas) {
            cacheBilleteras.put(b.getId(), b);
        }
        System.out.println("[BilleteraService] " + todas.size()
                + " billeteras cargadas en cache.");
    }

    // =========================================================
    // CRUD
    // =========================================================

    public Billetera crearBilletera(Long usuarioId, String nombre,
                                    TipoBilletera tipo, BigDecimal limite) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);

        // Validar nombre único por usuario
        if (billeteraRepository.existsByUsuarioIdAndNombre(usuarioId, nombre)) {
            throw new IllegalArgumentException(
                    "Ya tienes una billetera llamada '" + nombre + "'");
        }

        Billetera billetera = new Billetera(nombre, tipo, BigDecimal.ZERO, true, limite, usuario);
        Billetera guardada = billeteraRepository.save(billetera);

        // Insertar en cache — O(1)
        cacheBilleteras.put(guardada.getId(), guardada);

        return guardada;
    }

    /**
     * Búsqueda O(1) usando la tabla hash.
     */
    public Billetera buscarPorId(Long id) {
        Billetera enCache = cacheBilleteras.get(id);
        if (enCache != null) return enCache;

        return billeteraRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Billetera no encontrada con id: " + id));
    }

    public List<Billetera> listarPorUsuario(Long usuarioId) {
        usuarioService.buscarPorId(usuarioId); // valida que el usuario exista
        return billeteraRepository.findByUsuarioId(usuarioId);
    }

    public List<Billetera> listarActivasPorUsuario(Long usuarioId) {
        return billeteraRepository.findByUsuarioIdAndActivaTrue(usuarioId);
    }

    public void activar(Long billeteraId) {
        Billetera b = buscarPorId(billeteraId);
        b.setActiva(true);
        guardarYRefrescar(b);
    }

    public void desactivar(Long billeteraId) {
        Billetera b = buscarPorId(billeteraId);
        b.setActiva(false);
        guardarYRefrescar(b);
    }

    public void eliminarBilletera(Long billeteraId) {
        Billetera b = buscarPorId(billeteraId);
        cacheBilleteras.remove(billeteraId);
        billeteraRepository.delete(b);
    }

    // =========================================================
    // OPERACIONES DE SALDO
    // =========================================================

    /**
     * Aumenta el saldo — se usa en recargas y transferencias entrantes.
     */
    public void acreditar(Long billeteraId, BigDecimal monto) {
        Billetera b = buscarPorId(billeteraId);
        validarActiva(b);

        b.setSaldo(b.getSaldo().add(monto));
        guardarYRefrescar(b);
    }

    /**
     * Disminuye el saldo — se usa en retiros y transferencias salientes.
     * Valida saldo suficiente y límite de transacción antes de operar.
     */
    public void debitar(Long billeteraId, BigDecimal monto) {
        Billetera b = buscarPorId(billeteraId);
        validarActiva(b);
        validarSaldo(b, monto);
        validarLimite(b, monto);

        b.setSaldo(b.getSaldo().subtract(monto));
        guardarYRefrescar(b);

        // Alerta si el saldo quedó bajo
        if (b.getSaldo().compareTo(UMBRAL_SALDO_BAJO) < 0) {
            alertaService.crearAlertaSaldoBajo(b.getUsuario(), b.getNombre());
        }
    }

    public BigDecimal consultarSaldo(Long billeteraId) {
        return buscarPorId(billeteraId).getSaldo();
    }

    // =========================================================
    // VALIDACIONES (usadas por TransaccionService)
    // =========================================================

    public void validarSaldoSuficiente(Long billeteraId, BigDecimal monto) {
        validarSaldo(buscarPorId(billeteraId), monto);
    }

    // =========================================================
    // ANALYTICS
    // =========================================================

    public List<Billetera> obtenerMasActivas() {
        return billeteraRepository.findMasActivas();
    }

    public List<Object[]> actividadPorTipo() {
        return billeteraRepository.contarTransaccionesPorTipo();
    }

    // =========================================================
    // HELPERS PRIVADOS
    // =========================================================

    private void validarActiva(Billetera b) {
        if (!b.isActiva()) {
            throw new IllegalStateException(
                    "La billetera '" + b.getNombre() + "' está inactiva.");
        }
    }

    private void validarSaldo(Billetera b, BigDecimal monto) {
        if (b.getSaldo().compareTo(monto) < 0) {
            throw new IllegalArgumentException(
                    "Saldo insuficiente en '" + b.getNombre() +
                            "'. Disponible: " + b.getSaldo() +
                            ", Requerido: " + monto);
        }
    }

    private void validarLimite(Billetera b, BigDecimal monto) {
        if (b.getLimiteTransaccion() != null
                && monto.compareTo(b.getLimiteTransaccion()) > 0) {
            throw new IllegalArgumentException(
                    "El monto supera el límite de transacción de la billetera '" +
                            b.getNombre() + "'. Límite: " + b.getLimiteTransaccion());
        }
    }

    private void guardarYRefrescar(Billetera b) {
        Billetera guardada = billeteraRepository.save(b);
        cacheBilleteras.put(guardada.getId(), guardada);
    }
}