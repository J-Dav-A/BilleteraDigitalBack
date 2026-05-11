package billeteradigitalback.billeteradigitalback.Repository;

import billeteradigitalback.billeteradigitalback.Model.Beneficio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {

}