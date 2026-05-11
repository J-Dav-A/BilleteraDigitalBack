package billeteradigitalback.billeteradigitalback.Repository;

import billeteradigitalback.billeteradigitalback.Model.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

}