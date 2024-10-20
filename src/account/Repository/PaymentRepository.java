package account.Repository;

import account.Entity.AccountUser;
import account.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
/**
 * Repository interface for accessing and managing Payment entities.
 *
 * <p>This interface extends JpaRepository, providing CRUD operations and
 * custom query methods for Payment objects. </p>
 */
public interface PaymentRepository extends JpaRepository<Payment,Long> {


    boolean existsByEmployeeAndPeriod(AccountUser userName, YearMonth period);

    Optional<Payment> findByEmployeeAndPeriod(AccountUser employee, YearMonth parse);

    List<Payment> findByEmployee(AccountUser user);
}
