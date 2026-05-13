package billeteradigitalback.billeteradigitalback.Service;

import billeteradigitalback.billeteradigitalback.Model.Usuario;
import billeteradigitalback.billeteradigitalback.Repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class AutenticacionService {

    private final UsuarioRepository usuarioRepository;

    public AutenticacionService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Verifica credenciales del usuario.
     * Retorna el usuario si son correctas, lanza excepción si no.
     *
     * NOTA: En producción la contraseña debería hashearse con BCrypt.
     * Por el alcance académico del proyecto se compara directo.
     */
    public Usuario iniciarSesion(String correo, String password) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new NoSuchElementException(
                        "No existe una cuenta con ese correo."));

        if (!usuario.isActivo()) {
            throw new IllegalStateException("La cuenta está desactivada.");
        }

        if (!usuario.getPassword().equals(password)) {
            throw new IllegalArgumentException("Contraseña incorrecta.");
        }

        return usuario;
    }

    public void cerrarSesion(Long usuarioId) {
        // En una implementación con JWT aquí se invalidaría el token.
        // Para el proyecto académico solo se confirma el logout.
        System.out.println("[Auth] Usuario " + usuarioId + " cerró sesión.");
    }

    public boolean existeCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }
}