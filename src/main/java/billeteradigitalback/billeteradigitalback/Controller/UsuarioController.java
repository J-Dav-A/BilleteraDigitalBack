package billeteradigitalback.billeteradigitalback.Controller;

import billeteradigitalback.billeteradigitalback.Dto.request.UsuarioDTO;
import billeteradigitalback.billeteradigitalback.Enums.NivelUsuario;
import billeteradigitalback.billeteradigitalback.Model.Usuario;
import billeteradigitalback.billeteradigitalback.Service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    // El controller SOLO conoce el service, nunca el repository directamente
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // =========================================================
    // POST /api/usuarios
    // Registrar nuevo usuario
    // Body: { "nombre": "...", "correo": "...", "password": "..." }
    // =========================================================
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody UsuarioDTO request) {
        try {
            Usuario creado = usuarioService.registrarUsuario(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // GET /api/usuarios
    // Listar todos los usuarios
    // =========================================================
    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    // =========================================================
    // GET /api/usuarios/{id}
    // Buscar usuario por ID — usa tabla hash O(1)
    // =========================================================
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // GET /api/usuarios/correo/{correo}
    // Buscar usuario por correo
    // =========================================================
    @GetMapping("/correo/{correo}")
    public ResponseEntity<?> buscarPorCorreo(@PathVariable String correo) {
        try {
            return ResponseEntity.ok(usuarioService.buscarPorCorreo(correo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // PUT /api/usuarios/{id}
    // Actualizar nombre del usuario
    // Body: { "nombre": "nuevo nombre" }
    // =========================================================
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @RequestBody Map<String, String> body) {
        try {
            String nuevoNombre = body.get("nombre");
            String nuevaCedula = body.get("cedula");
            String nuevoTelefono = body.get("telefono");
            String nuevoCorreo = body.get("correo");
            String nuevoPassword = body.get("password");
            return ResponseEntity.ok(usuarioService.actualizarUsuario(id,
                    nuevoNombre,
                    nuevaCedula,
                    nuevoTelefono,
                    nuevoCorreo,
                    nuevoPassword));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // DELETE /api/usuarios/{id}
    // Eliminar usuario
    // =========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok(Map.of("mensaje", "Usuario eliminado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // GET /api/usuarios/ranking
    // Ranking de usuarios por puntos — recorre el árbol BST
    // =========================================================
    @GetMapping("/ranking")
    public ResponseEntity<List<Usuario>> ranking() {
        return ResponseEntity.ok(usuarioService.rankingPorPuntos());
    }

    // =========================================================
    // GET /api/usuarios/ranking/top/{n}
    // Top N usuarios con más puntos
    // =========================================================
    @GetMapping("/ranking/top/{n}")
    public ResponseEntity<List<Usuario>> top(@PathVariable int n) {
        return ResponseEntity.ok(usuarioService.topUsuarios(n));
    }

    // =========================================================
    // GET /api/usuarios/nivel/{nivel}
    // Usuarios filtrados por nivel (BRONCE, PLATA, ORO, PLATINO)
    // =========================================================
    @GetMapping("/nivel/{nivel}")
    public ResponseEntity<?> porNivel(@PathVariable String nivel) {
        try {
            NivelUsuario nivelEnum = NivelUsuario.valueOf(nivel.toUpperCase());
            return ResponseEntity.ok(usuarioService.buscarPorNivel(nivelEnum));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Nivel inválido: " + nivel
                            + ". Use: BRONCE, PLATA, ORO, PLATINO"));
        }
    }

    // =========================================================
    // GET /api/usuarios/puntos/rango?min=0&max=500
    // Usuarios en un rango de puntos — usa subMap() del árbol
    // =========================================================
    @GetMapping("/puntos/rango")
    public ResponseEntity<List<Usuario>> porRangoPuntos(@RequestParam int min,
                                                        @RequestParam int max) {
        return ResponseEntity.ok(usuarioService.buscarPorRangoDePuntos(min, max));
    }
}