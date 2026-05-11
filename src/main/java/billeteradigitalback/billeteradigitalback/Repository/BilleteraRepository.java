package billeteradigitalback.billeteradigitalback.Repository;

import billeteradigitalback.billeteradigitalback.Model.Billetera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BilleteraRepository extends JpaRepository<Billetera, Long> {

}