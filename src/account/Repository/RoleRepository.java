package account.Repository;

import account.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing Role entities.
 *
 * <p>Extends JpaRepository to provide CRUD operations for Role objects.</p>
 */
@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {


    Optional<Role> findByNameOfTheRoleIgnoreCase(String role);

    Optional<Role> findByNameOfTheRole(String roleAdministrator);
}
