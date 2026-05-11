package billeteradigitalback.billeteradigitalback.Repository;

import billeteradigitalback.billeteradigitalback.Model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

}