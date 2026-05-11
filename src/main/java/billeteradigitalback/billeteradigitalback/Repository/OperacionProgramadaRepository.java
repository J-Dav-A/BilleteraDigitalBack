package billeteradigitalback.billeteradigitalback.Repository;

import billeteradigitalback.billeteradigitalback.Model.OperacionProgramada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperacionProgramadaRepository extends JpaRepository<OperacionProgramada, Long> {

}