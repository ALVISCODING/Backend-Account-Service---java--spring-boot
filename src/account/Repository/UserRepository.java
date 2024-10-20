package account.Repository;

import account.Entity.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/**
 * Repository interface for managing AccountUser entities.
 *
 * <p>Extends JpaRepository to provide CRUD operations for AccountUser objects.</p>
 */
@Repository
public interface UserRepository extends JpaRepository<AccountUser, Long> {

    Optional<AccountUser> findByEmailIgnoreCase(String email);

    Optional<AccountUser> findByEmailIgnoreCaseAndPassword(String email, String password);

    boolean existsByEmail(String email);

    //to see how many employees have the given role
    //long countByRole(Role role);




}
