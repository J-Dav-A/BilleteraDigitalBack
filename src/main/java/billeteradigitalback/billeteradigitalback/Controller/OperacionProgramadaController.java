package billeteradigitalback.billeteradigitalback.Controller;

import billeteradigitalback.billeteradigitalback.Model.OperacionProgramada;
import billeteradigitalback.billeteradigitalback.Service.OperacionProgramadaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/operaciones-programadas")
@CrossOrigin(origins = "*")
public class OperacionProgramadaController {

    private final OperacionProgramadaService operacionService;

    public OperacionProgramadaController(OperacionProgramadaService operacionService) {
        this.operacionService = operacionService;
    }

    // =========================================================
    // POST /api/operaciones-programadas
    // Programar una operación futura
    // Body: {
    //   "billeteraOrigenId": 1,
    //   "billeteraDestinoId": 2,      (opcional para retiros/recargas)
    //   "monto": 500.00,
    //   "fechaFutura": "2025-12-01T10:00:00",
    //   "tipoTransaccion": "TRANSFERENCIA",
    //   "descripcion": "Pago mensual",
    //   "recurrente": true,
    //   "diasRecurrencia": 30
    // }
    // =========================================================
    @PostMapping
    public ResponseEntity<?> programar(@RequestBody Map<String, Object> body) {
        try {
            Long origenId    = Long.valueOf(body.get("billeteraOrigenId").toString());
            Long destinoId   = body.get("billeteraDestinoId") != null
                    ? Long.valueOf(body.get("billeteraDestinoId").toString()) : null;
            BigDecimal monto = new BigDecimal(body.get("monto").toString());
            LocalDateTime fecha = LocalDateTime.parse(body.get("fechaFutura").toString());
            String tipo      = body.get("tipoTransaccion").toString();
            String desc      = body.getOrDefault("descripcion", "").toString();
            boolean recurrente = Boolean.parseBoolean(
                    body.getOrDefault("recurrente", "false").toString());
            int dias         = Integer.parseInt(
                    body.getOrDefault("diasRecurrencia", "0").toString());

            OperacionProgramada op = operacionService.programar(
                    origenId, destinoId, monto, fecha, tipo, desc, recurrente, dias);
            return ResponseEntity.status(HttpStatus.CREATED).body(op);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // DELETE /api/operaciones-programadas/{id}/cancelar
    // Cancelar una operación pendiente
    // =========================================================
    @DeleteMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        try {
            operacionService.cancelar(id);
            return ResponseEntity.ok(Map.of("mensaje", "Operación cancelada correctamente"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // GET /api/operaciones-programadas/pendientes
    // Ver todas las pendientes en la cola de prioridad
    // =========================================================
    @GetMapping("/pendientes")
    public ResponseEntity<List<OperacionProgramada>> listarPendientes() {
        return ResponseEntity.ok(operacionService.listarPendientes());
    }

    // =========================================================
    // GET /api/operaciones-programadas/pendientes/cantidad
    // Cuántas operaciones hay en la cola
    // =========================================================
    @GetMapping("/pendientes/cantidad")
    public ResponseEntity<Map<String, Integer>> cantidadPendientes() {
        return ResponseEntity.ok(Map.of("cantidad", operacionService.cantidadPendientes()));
    }

    // =========================================================
    // GET /api/operaciones-programadas/usuario/{usuarioId}
    // Operaciones programadas de un usuario
    // =========================================================
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<OperacionProgramada>> porUsuario(
            @PathVariable Long usuarioId) {
        return ResponseEntity.ok(operacionService.listarPorUsuario(usuarioId));
    }

    // =========================================================
    // POST /api/operaciones-programadas/procesar
    // Forzar procesamiento de la cola (útil para testing)
    // =========================================================
    @PostMapping("/procesar")
    public ResponseEntity<Map<String, String>> procesarManual() {
        operacionService.procesarCola();
        return ResponseEntity.ok(Map.of("mensaje", "Cola procesada manualmente"));
    }
}