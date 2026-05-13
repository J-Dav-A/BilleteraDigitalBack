package billeteradigitalback.billeteradigitalback.Repository;

import billeteradigitalback.billeteradigitalback.Model.Beneficio;
import billeteradigitalback.billeteradigitalback.Enums.NivelUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {

    // Solo beneficios activos — lo que ve el usuario al consultar
    List<Beneficio> findByActivoTrue();

    // Beneficios accesibles para un nivel específico
    List<Beneficio> findByNivelRequeridoAndActivoTrue(NivelUsuario nivel);

    // Beneficios canjeables con X puntos o menos
    List<Beneficio> findByPuntosNecesariosLessThanEqualAndActivoTrue(int puntos);

    // Beneficios canjeables para un usuario según su nivel Y sus puntos
    List<Beneficio> findByNivelRequeridoAndPuntosNecesariosLessThanEqualAndActivoTrue(
            NivelUsuario nivel, int puntos);
}