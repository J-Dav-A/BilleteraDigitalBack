package billeteradigitalback.billeteradigitalback.Controller;

import billeteradigitalback.billeteradigitalback.Enums.TipoBilletera;
import billeteradigitalback.billeteradigitalback.Model.Billetera;
import billeteradigitalback.billeteradigitalback.Service.BilleteraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/billeteras")
@CrossOrigin(origins = "*")
public class BilleteraController {

    private final BilleteraService billeteraService;

    public BilleteraController(BilleteraService billeteraService) {
        this.billeteraService = billeteraService;
    }

    // =========================================================
    // POST /api/billeteras
    // Crear billetera
    // Body: { "usuarioId": 1, "nombre": "Ahorros", "tipo": "AHORRO", "limite": 1000.00 }
    // =========================================================
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {
        try {
            Long usuarioId = Long.valueOf(body.get("usuarioId").toString());
            String nombre  = body.get("nombre").toString();
            TipoBilletera tipo = TipoBilletera.valueOf(
                    body.get("tipo").toString().toUpperCase());
            BigDecimal limite = new BigDecimal(body.get("limite").toString());

            Billetera creada = billeteraService.crearBilletera(usuarioId, nombre, tipo, limite);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // GET /api/billeteras/{id}
    // Buscar billetera por ID — usa tabla hash O(1)
    // =========================================================
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(billeteraService.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // GET /api/billeteras/usuario/{usuarioId}
    // Listar todas las billeteras de un usuario
    // =========================================================
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Billetera>> porUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(billeteraService.listarPorUsuario(usuarioId));
    }

    // =========================================================
    // GET /api/billeteras/usuario/{usuarioId}/activas
    // Solo billeteras activas del usuario
    // =========================================================
    @GetMapping("/usuario/{usuarioId}/activas")
    public ResponseEntity<List<Billetera>> activasPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(billeteraService.listarActivasPorUsuario(usuarioId));
    }

    // =========================================================
    // GET /api/billeteras/{id}/saldo
    // Consultar saldo
    // =========================================================
    @GetMapping("/{id}/saldo")
    public ResponseEntity<?> consultarSaldo(@PathVariable Long id) {
        try {
            BigDecimal saldo = billeteraService.consultarSaldo(id);
            return ResponseEntity.ok(Map.of("saldo", saldo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // PATCH /api/billeteras/{id}/activar
    // Activar billetera
    // =========================================================
    @PatchMapping("/{id}/activar")
    public ResponseEntity<?> activar(@PathVariable Long id) {
        try {
            billeteraService.activar(id);
            return ResponseEntity.ok(Map.of("mensaje", "Billetera activada"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // PATCH /api/billeteras/{id}/desactivar
    // Desactivar billetera
    // =========================================================
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivar(@PathVariable Long id) {
        try {
            billeteraService.desactivar(id);
            return ResponseEntity.ok(Map.of("mensaje", "Billetera desactivada"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // DELETE /api/billeteras/{id}
    // Eliminar billetera
    // =========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            billeteraService.eliminarBilletera(id);
            return ResponseEntity.ok(Map.of("mensaje", "Billetera eliminada"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // GET /api/billeteras/mas-activas
    // Billeteras con más transacciones (analytics)
    // =========================================================
    @GetMapping("/mas-activas")
    public ResponseEntity<List<Billetera>> masActivas() {
        return ResponseEntity.ok(billeteraService.obtenerMasActivas());
    }
}
