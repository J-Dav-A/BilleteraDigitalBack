package billeteradigitalback.billeteradigitalback.Controller;

import billeteradigitalback.billeteradigitalback.Model.Transaccion;
import billeteradigitalback.billeteradigitalback.Service.TransaccionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transacciones")
@CrossOrigin(origins = "*")
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    // =========================================================
    // POST /api/transacciones/recargar
    // Recargar saldo en una billetera
    // Body: { "billeteraDestinoId": 1, "monto": 500.00, "descripcion": "..." }
    // =========================================================
    @PostMapping("/recargar")
    public ResponseEntity<?> recargar(@RequestBody Map<String, Object> body) {
        try {
            Long billeteraId = Long.valueOf(body.get("billeteraDestinoId").toString());
            BigDecimal monto = new BigDecimal(body.get("monto").toString());
            String descripcion = body.getOrDefault("descripcion", "Recarga").toString();

            Transaccion tx = transaccionService.recargar(billeteraId, monto, descripcion);
            return ResponseEntity.status(HttpStatus.CREATED).body(tx);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // POST /api/transacciones/retirar
    // Retirar saldo de una billetera
    // Body: { "billeteraOrigenId": 1, "monto": 200.00, "descripcion": "..." }
    // =========================================================
    @PostMapping("/retirar")
    public ResponseEntity<?> retirar(@RequestBody Map<String, Object> body) {
        try {
            Long billeteraId = Long.valueOf(body.get("billeteraOrigenId").toString());
            BigDecimal monto = new BigDecimal(body.get("monto").toString());
            String descripcion = body.getOrDefault("descripcion", "Retiro").toString();

            Transaccion tx = transaccionService.retirar(billeteraId, monto, descripcion);
            return ResponseEntity.status(HttpStatus.CREATED).body(tx);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // POST /api/transacciones/transferir
    // Transferir entre billeteras
    // Body: { "billeteraOrigenId": 1, "billeteraDestinoId": 2, "monto": 100.00, "descripcion": "..." }
    // =========================================================
    @PostMapping("/transferir")
    public ResponseEntity<?> transferir(@RequestBody Map<String, Object> body) {
        try {
            Long origenId  = Long.valueOf(body.get("billeteraOrigenId").toString());
            Long destinoId = Long.valueOf(body.get("billeteraDestinoId").toString());
            BigDecimal monto = new BigDecimal(body.get("monto").toString());
            String descripcion = body.getOrDefault("descripcion", "Transferencia").toString();

            Transaccion tx = transaccionService.transferir(origenId, destinoId, monto, descripcion);
            return ResponseEntity.status(HttpStatus.CREATED).body(tx);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // POST /api/transacciones/revertir/{id}
    // Revertir una transacción específica
    // =========================================================
    @PostMapping("/revertir/{id}")
    public ResponseEntity<?> revertir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(transaccionService.revertirTransaccion(id));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // POST /api/transacciones/revertir/ultima/{usuarioId}
    // Revertir la última transacción del usuario — pop() de la pila
    // =========================================================
    @PostMapping("/revertir/ultima/{usuarioId}")
    public ResponseEntity<?> revertirUltima(@PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(transaccionService.revertirUltima(usuarioId));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // GET /api/transacciones/{id}
    // Buscar transacción por ID
    // =========================================================
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(transaccionService.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // GET /api/transacciones/billetera/{billeteraId}
    // Historial de una billetera (origen o destino)
    // =========================================================
    @GetMapping("/billetera/{billeteraId}")
    public ResponseEntity<List<Transaccion>> historialBilletera(
            @PathVariable Long billeteraId) {
        return ResponseEntity.ok(transaccionService.historialPorBilletera(billeteraId));
    }

    // =========================================================
    // GET /api/transacciones/usuario/{usuarioId}
    // Historial completo de un usuario
    // =========================================================
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Transaccion>> historialUsuario(
            @PathVariable Long usuarioId) {
        return ResponseEntity.ok(transaccionService.historialPorUsuario(usuarioId));
    }

    // =========================================================
    // GET /api/transacciones/usuario/{usuarioId}/rango?desde=...&hasta=...
    // Historial en rango de fechas
    // Ejemplo: ?desde=2025-01-01T00:00:00&hasta=2025-12-31T23:59:59
    // =========================================================
    @GetMapping("/usuario/{usuarioId}/rango")
    public ResponseEntity<List<Transaccion>> historialEntreFechas(
            @PathVariable Long usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(
                transaccionService.historialEntreFechas(usuarioId, desde, hasta));
    }

    // =========================================================
    // GET /api/transacciones/top/{n}
    // Top N transacciones por mayor valor
    // =========================================================
    @GetMapping("/top/{n}")
    public ResponseEntity<List<Transaccion>> topPorValor(@PathVariable int n) {
        return ResponseEntity.ok(transaccionService.topPorValor(n));
    }

    // =========================================================
    // GET /api/transacciones/analytics/monto-total?desde=...&hasta=...
    // Monto total movilizado en un período
    // =========================================================
    @GetMapping("/analytics/monto-total")
    public ResponseEntity<?> montoTotal(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        BigDecimal total = transaccionService.montoTotalEntreFechas(desde, hasta);
        return ResponseEntity.ok(Map.of("total", total));
    }

    // =========================================================
    // GET /api/transacciones/analytics/frecuencia
    // Frecuencia de transacciones por tipo
    // =========================================================
    @GetMapping("/analytics/frecuencia")
    public ResponseEntity<List<Object[]>> frecuenciaPorTipo() {
        return ResponseEntity.ok(transaccionService.frecuenciaPorTipo());
    }

    // =========================================================
    // GET /api/transacciones/analytics/usuarios-activos?top=5
    // Usuarios con más transacciones
    // =========================================================
    @GetMapping("/analytics/usuarios-activos")
    public ResponseEntity<List<Object[]>> usuariosMasActivos(
            @RequestParam(defaultValue = "5") int top) {
        return ResponseEntity.ok(transaccionService.usuariosMasActivos(top));
    }

    // =========================================================
    // GET /api/transacciones/grafo/alcanzables/{usuarioId}
    // Usuarios alcanzables desde un usuario (BFS sobre el grafo)
    // =========================================================
    @GetMapping("/grafo/alcanzables/{usuarioId}")
    public ResponseEntity<List<Long>> alcanzables(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(transaccionService.bfsAlcanzables(usuarioId));
    }

    // =========================================================
    // GET /api/transacciones/grafo/ciclos
    // Detectar si hay ciclos en el grafo de transferencias
    // =========================================================
    @GetMapping("/grafo/ciclos")
    public ResponseEntity<Map<String, Boolean>> hayCiclos() {
        return ResponseEntity.ok(Map.of("tieneCiclos", transaccionService.hayciClos()));
    }

    // =========================================================
    // GET /api/transacciones/grafo/relaciones-frecuentes?top=5
    // Top N relaciones más frecuentes entre usuarios
    // =========================================================
    @GetMapping("/grafo/relaciones-frecuentes")
    public ResponseEntity<List<String>> relacionesFrecuentes(
            @RequestParam(defaultValue = "5") int top) {
        return ResponseEntity.ok(transaccionService.relacionesFrecuentes(top));
    }
}