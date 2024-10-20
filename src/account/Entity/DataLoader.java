package account.Entity;

import account.Erum.RoleGroup;
import account.Repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Preloads predefined roles into the system.
 *
 * <p>This component is responsible for initializing roles
 * during application startup to ensure essential roles are available.</p>
 */
@Component
public class DataLoader {
    
        private final RoleRepository roleRepository;

        @Autowired
        public DataLoader(RoleRepository roleRepository) {
            this.roleRepository = roleRepository;
            preloadRoles();
        }

        //postConstruct is a Java annotation that marks a method to be executed after a bean is constructed and initialized.
        // It's used for tasks like setting up resources, performing validation, or customizing the bean.
    @PostConstruct
    @Transactional // Ensures the method runs within a transaction
        private void preloadRoles() {
            // Only preload if no roles exist in the database
            if (roleRepository.count() == 0) {
                roleRepository.save(new Role("ROLE_ADMINISTRATOR", RoleGroup.ADMINISTRATIVE));
                roleRepository.save(new Role("ROLE_ACCOUNTANT", RoleGroup.BUSINESS));
                roleRepository.save(new Role("ROLE_USER", RoleGroup.BUSINESS));
                roleRepository.save(new Role("ROLE_AUDITOR", RoleGroup.BUSINESS));
            }
        }
    }

