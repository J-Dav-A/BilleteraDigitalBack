package billeteradigitalback.billeteradigitalback.Repository;

import billeteradigitalback.billeteradigitalback.Model.OperacionProgramada;
import billeteradigitalback.billeteradigitalback.Enums.EstadoOperacionProgramada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperacionProgramadaRepository extends JpaRepository<OperacionProgramada, Long> {

    // Operaciones pendientes ordenadas por fecha — para cargar la cola de prioridad al iniciar
    List<OperacionProgramada> findByEstadoOrderByFechaFuturaAsc(EstadoOperacionProgramada estado);

    // Operaciones que ya vencieron y siguen pendientes — para el procesador automático
    @Query("""
        SELECT o FROM OperacionProgramada o
        WHERE o.estado = 'PENDIENTE'
          AND o.fechaFutura <= :ahora
        ORDER BY o.fechaFutura ASC
        """)
    List<OperacionProgramada> findVencidas(@Param("ahora") LocalDateTime ahora);

    // Operaciones próximas a ejecutarse en las siguientes N horas — para alertas
    @Query("""
        SELECT o FROM OperacionProgramada o
        WHERE o.estado = 'PENDIENTE'
          AND o.fechaFutura BETWEEN :ahora AND :limite
        ORDER BY o.fechaFutura ASC
        """)
    List<OperacionProgramada> findProximas(
            @Param("ahora") LocalDateTime ahora,
            @Param("limite") LocalDateTime limite);

    // Operaciones de un usuario específico (por billetera origen)
    @Query("""
        SELECT o FROM OperacionProgramada o
        WHERE o.billeteraOrigen.usuario.id = :usuarioId
        ORDER BY o.fechaFutura ASC
        """)
    List<OperacionProgramada> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    // Operaciones recurrentes activas — para reagendar después de ejecutarse
    List<OperacionProgramada> findByRecurrenteTrue();

    // Contar operaciones pendientes de un usuario
    @Query("""
        SELECT COUNT(o) FROM OperacionProgramada o
        WHERE o.billeteraOrigen.usuario.id = :usuarioId
          AND o.estado = 'PENDIENTE'
        """)
    long contarPendientesDeUsuario(@Param("usuarioId") Long usuarioId);
}