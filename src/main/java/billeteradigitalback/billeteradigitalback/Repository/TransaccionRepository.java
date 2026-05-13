package billeteradigitalback.billeteradigitalback.Repository;

import billeteradigitalback.billeteradigitalback.Model.Transaccion;
import billeteradigitalback.billeteradigitalback.Enums.EstadoTransaccion;
import billeteradigitalback.billeteradigitalback.Enums.NivelRiesgo;
import billeteradigitalback.billeteradigitalback.Enums.TipoTransaccion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    // ── Historial por billetera ─────────────────────────────────────────────

    // Transacciones donde la billetera es origen — para historial
    List<Transaccion> findByBilleteraOrigenIdOrderByFechaDesc(Long billeteraId);

    // Transacciones donde la billetera es destino
    List<Transaccion> findByBilleteraDestinoIdOrderByFechaDesc(Long billeteraId);

    // Todas las transacciones de una billetera (origen O destino)
    @Query("""
        SELECT t FROM Transaccion t
        WHERE t.billeteraOrigen.id = :billeteraId
           OR t.billeteraDestino.id = :billeteraId
        ORDER BY t.fecha DESC
        """)
    List<Transaccion> findByBilleteraId(@Param("billeteraId") Long billeteraId);

    // ── Historial por usuario ───────────────────────────────────────────────

    // Todas las transacciones de un usuario (por cualquiera de sus billeteras)
    @Query("""
        SELECT t FROM Transaccion t
        WHERE t.billeteraOrigen.usuario.id = :usuarioId
           OR t.billeteraDestino.usuario.id = :usuarioId
        ORDER BY t.fecha DESC
        """)
    List<Transaccion> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    // Transacciones de un usuario en un rango de fechas
    @Query("""
        SELECT t FROM Transaccion t
        WHERE (t.billeteraOrigen.usuario.id = :usuarioId
            OR t.billeteraDestino.usuario.id = :usuarioId)
          AND t.fecha BETWEEN :desde AND :hasta
        ORDER BY t.fecha DESC
        """)
    List<Transaccion> findByUsuarioIdEntreFechas(
            @Param("usuarioId") Long usuarioId,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);

    // ── Filtros por estado y tipo ───────────────────────────────────────────

    List<Transaccion> findByEstado(EstadoTransaccion estado);

    List<Transaccion> findByTipoTransaccion(TipoTransaccion tipo);

    // Transacciones reversibles de un usuario (para la pila de deshacer)
    @Query("""
        SELECT t FROM Transaccion t
        WHERE t.billeteraOrigen.usuario.id = :usuarioId
          AND t.estado = 'COMPLETADA'
        ORDER BY t.fecha DESC
        """)
    List<Transaccion> findReversiblesDeUsuario(@Param("usuarioId") Long usuarioId);

    // ── Antifraude ──────────────────────────────────────────────────────────

    // Transacciones de riesgo alto o medio
    List<Transaccion> findByNivelRiesgo(NivelRiesgo nivelRiesgo);

    // Transacciones recientes de un usuario en una ventana de tiempo
    // (para detectar ráfagas — múltiples transacciones en poco tiempo)
    @Query("""
        SELECT t FROM Transaccion t
        WHERE t.billeteraOrigen.usuario.id = :usuarioId
          AND t.fecha >= :desde
        ORDER BY t.fecha DESC
        """)
    List<Transaccion> findRecientesDeUsuario(
            @Param("usuarioId") Long usuarioId,
            @Param("desde") LocalDateTime desde);

    // Promedio del valor de transacciones de un usuario — para detectar montos inusuales
    @Query("""
        SELECT AVG(t.valor) FROM Transaccion t
        WHERE t.billeteraOrigen.usuario.id = :usuarioId
          AND t.estado = 'COMPLETADA'
        """)
    BigDecimal promedioValorDeUsuario(@Param("usuarioId") Long usuarioId);

    // ── Analytics ───────────────────────────────────────────────────────────

    // Monto total movilizado en un rango de fechas
    @Query("SELECT SUM(t.valor) FROM Transaccion t WHERE t.fecha BETWEEN :desde AND :hasta AND t.estado = 'COMPLETADA'")
    BigDecimal sumaTotalEntreFechas(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);

    // Frecuencia de transacciones por tipo
    @Query("SELECT t.tipoTransaccion, COUNT(t) FROM Transaccion t GROUP BY t.tipoTransaccion")
    List<Object[]> contarPorTipo();

    // Usuarios con más transacciones realizadas
    @Query("""
        SELECT t.billeteraOrigen.usuario.id, t.billeteraOrigen.usuario.nombre, COUNT(t)
        FROM Transaccion t
        WHERE t.billeteraOrigen IS NOT NULL
        GROUP BY t.billeteraOrigen.usuario.id, t.billeteraOrigen.usuario.nombre
        ORDER BY COUNT(t) DESC
        """)
    List<Object[]> findUsuariosMasActivos(Pageable pageable);

    // Transacciones de mayor valor — para reportes con estructuras ordenadas
    @Query("SELECT t FROM Transaccion t WHERE t.estado = 'COMPLETADA' ORDER BY t.valor DESC")
    List<Transaccion> findTopPorValor(Pageable pageable);

    // Todas las transferencias — para construir el grafo al iniciar
    @Query("SELECT t FROM Transaccion t WHERE t.tipoTransaccion = 'TRANSFERENCIA' AND t.estado = 'COMPLETADA'")
    List<Transaccion> findTodasLasTransferencias();
}