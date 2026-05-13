package billeteradigitalback.billeteradigitalback.Service;

import billeteradigitalback.billeteradigitalback.Enums.NivelUsuario;
import billeteradigitalback.billeteradigitalback.Enums.TipoAlerta;
import billeteradigitalback.billeteradigitalback.Model.Beneficio;
import billeteradigitalback.billeteradigitalback.Model.Usuario;
import billeteradigitalback.billeteradigitalback.Repository.BeneficioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class BeneficioService {

    private final BeneficioRepository beneficioRepository;
    private final UsuarioService usuarioService;
    private final AlertaService alertaService;

    public BeneficioService(BeneficioRepository beneficioRepository,
                            UsuarioService usuarioService,
                            AlertaService alertaService) {
        this.beneficioRepository = beneficioRepository;
        this.usuarioService = usuarioService;
        this.alertaService = alertaService;
    }

    // =========================================================
    // CRUD de beneficios (admin)
    // =========================================================

    public Beneficio crearBeneficio(String descripcion, int puntosNecesarios,
                                    NivelUsuario nivelRequerido) {
        Beneficio b = new Beneficio(descripcion, puntosNecesarios, nivelRequerido, true);
        return beneficioRepository.save(b);
    }

    public void desactivarBeneficio(Long id) {
        Beneficio b = buscarPorId(id);
        b.setActivo(false);
        beneficioRepository.save(b);
    }

    public Beneficio buscarPorId(Long id) {
        return beneficioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Beneficio no encontrado: " + id));
    }

    public List<Beneficio> listarActivos() {
        return beneficioRepository.findByActivoTrue();
    }

    // =========================================================
    // CANJE
    // =========================================================

    /**
     * El usuario canjea un beneficio.
     * Valida: que tenga el nivel requerido y los puntos suficientes.
     * Descuenta los puntos y envía alerta de confirmación.
     */
    public void canjear(Long usuarioId, Long beneficioId) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        Beneficio beneficio = buscarPorId(beneficioId);

        if (!beneficio.isActivo()) {
            throw new IllegalArgumentException(
                    "El beneficio no está disponible.");
        }

        // Validar nivel
        if (!tieneNivelSuficiente(usuario.getNivelUsuario(),
                beneficio.getNivelRequerido())) {
            throw new IllegalArgumentException(
                    "Nivel insuficiente. Requiere: " + beneficio.getNivelRequerido()
                            + ", Tu nivel: " + usuario.getNivelUsuario());
        }

        // Validar puntos
        if (usuario.getPuntos() < beneficio.getPuntosNecesarios()) {
            throw new IllegalArgumentException(
                    "Puntos insuficientes. Necesitas: " + beneficio.getPuntosNecesarios()
                            + ", Tienes: " + usuario.getPuntos());
        }

        // Descontar puntos
        usuarioService.canjearPuntos(usuarioId, beneficio.getPuntosNecesarios());

        // Notificar
        alertaService.crearAlerta(
                usuario,
                "Beneficio canjeado: '" + beneficio.getDescripcion() + "' por "
                        + beneficio.getPuntosNecesarios() + " puntos.",
                TipoAlerta.CANJE_BENEFICIO);
    }

    /**
     * Beneficios disponibles para un usuario según su nivel y puntos actuales.
     */
    public List<Beneficio> beneficiosDisponibles(Long usuarioId) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        return beneficioRepository
                .findByNivelRequeridoAndPuntosNecesariosLessThanEqualAndActivoTrue(
                        usuario.getNivelUsuario(),
                        usuario.getPuntos());
    }

    // =========================================================
    // HELPERS
    // =========================================================

    /**
     * Verifica que el nivel del usuario sea igual o superior al requerido.
     * BRONCE < PLATA < ORO < PLATINO
     */
    private boolean tieneNivelSuficiente(NivelUsuario nivelUsuario,
                                         NivelUsuario nivelRequerido) {
        return nivelUsuario.ordinal() >= nivelRequerido.ordinal();
    }

    /**
     * Descuento en comisión según nivel — usado por TransaccionService.
     */
    public double obtenerDescuentoComision(NivelUsuario nivel) {
        return switch (nivel) {
            case BRONCE  -> 0.0;   // sin descuento
            case PLATA   -> 0.10;  // 10% de descuento
            case ORO     -> 0.20;  // 20% de descuento
            case PLATINO -> 0.35;  // 35% de descuento
        };
    }
}