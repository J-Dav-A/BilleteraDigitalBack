package billeteradigitalback.billeteradigitalback.Controller;

import billeteradigitalback.billeteradigitalback.Model.Alerta;
import billeteradigitalback.billeteradigitalback.Service.AlertaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alertas")
@CrossOrigin(origins = "*")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    // =========================================================
    // GET /api/alertas/usuario/{usuarioId}/recientes
    // Últimas 20 alertas desde el deque en memoria — O(1)
    // =========================================================
    @GetMapping("/usuario/{usuarioId}/recientes")
    public ResponseEntity<List<Alerta>> recientes(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(alertaService.obtenerRecientes(usuarioId));
    }

    // =========================================================
    // GET /api/alertas/usuario/{usuarioId}
    // Todas las alertas del usuario desde BD
    // =========================================================
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Alerta>> todas(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(alertaService.obtenerTodas(usuarioId));
    }

    // =========================================================
    // GET /api/alertas/usuario/{usuarioId}/no-leidas
    // Solo alertas no leídas
    // =========================================================
    @GetMapping("/usuario/{usuarioId}/no-leidas")
    public ResponseEntity<List<Alerta>> noLeidas(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(alertaService.obtenerNoLeidas(usuarioId));
    }

    // =========================================================
    // GET /api/alertas/usuario/{usuarioId}/contador
    // Cantidad de alertas no leídas (para el badge del frontend)
    // =========================================================
    @GetMapping("/usuario/{usuarioId}/contador")
    public ResponseEntity<Map<String, Long>> contador(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(
                Map.of("noLeidas", alertaService.contarNoLeidas(usuarioId)));
    }

    // =========================================================
    // PATCH /api/alertas/{id}/leer
    // Marcar una alerta como leída
    // =========================================================
    @PatchMapping("/{id}/leer")
    public ResponseEntity<?> marcarLeida(@PathVariable Long id) {
        try {
            alertaService.marcarComoLeida(id);
            return ResponseEntity.ok(Map.of("mensaje", "Alerta marcada como leída"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // PATCH /api/alertas/usuario/{usuarioId}/leer-todas
    // Marcar todas las alertas del usuario como leídas
    // =========================================================
    @PatchMapping("/usuario/{usuarioId}/leer-todas")
    public ResponseEntity<Map<String, String>> marcarTodasLeidas(
            @PathVariable Long usuarioId) {
        alertaService.marcarTodasComoLeidas(usuarioId);
        return ResponseEntity.ok(Map.of("mensaje", "Todas las alertas marcadas como leídas"));
    }

    // =========================================================
    // POST /api/alertas/despachar
    // Procesar la cola FIFO de alertas pendientes
    // =========================================================
    @PostMapping("/despachar")
    public ResponseEntity<Map<String, Integer>> despachar() {
        List<Alerta> despachadas = alertaService.despacharPendientes();
        return ResponseEntity.ok(Map.of("despachadas", despachadas.size()));
    }

    // =========================================================
    // GET /api/alertas/cola/cantidad
    // Cuántas alertas hay en la cola pendiente
    // =========================================================
    @GetMapping("/cola/cantidad")
    public ResponseEntity<Map<String, Integer>> cantidadEnCola() {
        return ResponseEntity.ok(Map.of("pendientes", alertaService.contarPendientes()));
    }
}