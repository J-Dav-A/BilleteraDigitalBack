package billeteradigitalback.billeteradigitalback.Repository;

import billeteradigitalback.billeteradigitalback.Model.Alerta;
import billeteradigitalback.billeteradigitalback.Enums.TipoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    // Todas las alertas de un usuario ordenadas por fecha
    List<Alerta> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    // Solo las no leídas — para el badge de notificaciones
    List<Alerta> findByUsuarioIdAndLeidaFalseOrderByFechaDesc(Long usuarioId);

    // Las últimas 20 alertas — para el historial reciente en memoria
    List<Alerta> findTop20ByUsuarioIdOrderByFechaDesc(Long usuarioId);

    // Alertas por tipo — útil para filtrar solo alertas de seguridad
    List<Alerta> findByUsuarioIdAndTipoAlerta(Long usuarioId, TipoAlerta tipo);

    // Contar no leídas — para el contador del frontend
    long countByUsuarioIdAndLeidaFalse(Long usuarioId);
}