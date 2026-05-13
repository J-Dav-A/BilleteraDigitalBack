package billeteradigitalback.billeteradigitalback.Repository;

import billeteradigitalback.billeteradigitalback.Model.Billetera;
import billeteradigitalback.billeteradigitalback.Enums.TipoBilletera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BilleteraRepository extends JpaRepository<Billetera, Long> {

    // Todas las billeteras de un usuario
    List<Billetera> findByUsuarioId(Long usuarioId);

    // Solo las billeteras activas de un usuario
    List<Billetera> findByUsuarioIdAndActivaTrue(Long usuarioId);

    // Billeteras por tipo — para analytics de categorías más usadas
    List<Billetera> findByTipoBilletera(TipoBilletera tipo);

    // Verificar si un usuario ya tiene una billetera con ese nombre
    boolean existsByUsuarioIdAndNombre(Long usuarioId, String nombre);

    // Billeteras con más transacciones — para el reporte de más activas
    @Query("""
        SELECT b FROM Billetera b
        LEFT JOIN b.transaccionesOrigen t
        GROUP BY b
        ORDER BY COUNT(t) DESC
        """)
    List<Billetera> findMasActivas();

    // Contar transacciones por tipo de billetera — analytics
    @Query("""
        SELECT b.tipoBilletera, COUNT(t)
        FROM Billetera b
        LEFT JOIN b.transaccionesOrigen t
        GROUP BY b.tipoBilletera
        ORDER BY COUNT(t) DESC
        """)
    List<Object[]> contarTransaccionesPorTipo();
}