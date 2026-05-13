package billeteradigitalback.billeteradigitalback.Controller;

import billeteradigitalback.billeteradigitalback.Enums.NivelUsuario;
import billeteradigitalback.billeteradigitalback.Model.Beneficio;
import billeteradigitalback.billeteradigitalback.Service.BeneficioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/beneficios")
@CrossOrigin(origins = "*")
public class BeneficioController {

    private final BeneficioService beneficioService;

    public BeneficioController(BeneficioService beneficioService) {
        this.beneficioService = beneficioService;
    }

    // =========================================================
    // POST /api/beneficios
    // Crear beneficio (acción de administrador)
    // Body: { "descripcion": "...", "puntosNecesarios": 200, "nivelRequerido": "PLATA" }
    // =========================================================
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {
        try {
            String descripcion = body.get("descripcion").toString();
            int puntos = Integer.parseInt(body.get("puntosNecesarios").toString());
            NivelUsuario nivel = NivelUsuario.valueOf(
                    body.get("nivelRequerido").toString().toUpperCase());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(beneficioService.crearBeneficio(descripcion, puntos, nivel));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // GET /api/beneficios
    // Listar todos los beneficios activos
    // =========================================================
    @GetMapping
    public ResponseEntity<List<Beneficio>> listarActivos() {
        return ResponseEntity.ok(beneficioService.listarActivos());
    }

    // =========================================================
    // GET /api/beneficios/disponibles/{usuarioId}
    // Beneficios que puede canjear el usuario según nivel y puntos
    // =========================================================
    @GetMapping("/disponibles/{usuarioId}")
    public ResponseEntity<List<Beneficio>> disponibles(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(beneficioService.beneficiosDisponibles(usuarioId));
    }

    // =========================================================
    // POST /api/beneficios/{id}/canjear/{usuarioId}
    // Canjear un beneficio
    // =========================================================
    @PostMapping("/{id}/canjear/{usuarioId}")
    public ResponseEntity<?> canjear(@PathVariable Long id,
                                     @PathVariable Long usuarioId) {
        try {
            beneficioService.canjear(usuarioId, id);
            return ResponseEntity.ok(Map.of("mensaje", "Beneficio canjeado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // PATCH /api/beneficios/{id}/desactivar
    // Desactivar un beneficio
    // =========================================================
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivar(@PathVariable Long id) {
        try {
            beneficioService.desactivarBeneficio(id);
            return ResponseEntity.ok(Map.of("mensaje", "Beneficio desactivado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
