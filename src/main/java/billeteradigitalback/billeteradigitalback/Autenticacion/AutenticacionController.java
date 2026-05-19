package billeteradigitalback.billeteradigitalback.Autenticacion;

import billeteradigitalback.billeteradigitalback.Dto.response.AuthResponseDTO;
import billeteradigitalback.billeteradigitalback.Security.JwtService;
import billeteradigitalback.billeteradigitalback.Dto.request.LoginDTO;
import billeteradigitalback.billeteradigitalback.Model.Usuario;
import billeteradigitalback.billeteradigitalback.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AutenticacionController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request) {

        Usuario usuario = usuarioRepository
                .findByCorreo(request.getCorreo())
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));

        if (!usuario.getPassword().equals(request.getPassword())) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Contraseña incorrecta"));
        }

        String token = jwtService.generarToken(usuario.getCorreo());

        AuthResponseDTO response =
                new AuthResponseDTO(token, usuario);

        return ResponseEntity.ok(response);
    }
}