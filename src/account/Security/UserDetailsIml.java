package account.Security;

import account.Entity.AccountUser;
import account.Entity.Role;
import account.Erum.RoleGroup;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * UserDetailsIml implements the UserDetails interface from Spring Security.
 * It adapts the AccountUser entity to be used by Spring Security for authentication and authorization.
 *
 * This class wraps an AccountUser object and provides its email as the username and its password.
 *
 * Methods provided by this class include:
 * - getAuthorities: Returns the authorities granted to the user (currently returns null, meaning no authorities).
 * - getPassword: Returns the password of the wrapped AccountUser.
 * - getUsername: Returns the email (used as username) of the wrapped AccountUser.
 * - isAccountNonExpired: Indicates whether the account has expired (currently always true).
 * - isAccountNonLocked: Indicates whether the account is locked (currently always true).
 * - isCredentialsNonExpired: Indicates whether the credentials have expired (currently always true).
 * - isEnabled: Indicates whether the account is enabled (currently always true).
 *
 * This implementation is essential for integrating the application's user model with Spring Security's authentication mechanisms.
 */
    public class UserDetailsIml implements UserDetails {

        // use entity class
        private final AccountUser accountUser;

        public AccountUser getAccountUser() {
            return this.accountUser;
        }


        public UserDetailsIml(AccountUser accountUser) {
            this.accountUser = accountUser;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            // Convert each Role in accountUser's roles to a GrantedAuthority
            Set<Role> Roles = accountUser.getRoles();

            return Roles.stream()
                    .map(role -> new SimpleGrantedAuthority(role.getNameOfTheRole())) // Convert Role to SimpleGrantedAuthority
                    .collect(Collectors.toSet());
        }

        @Override
        public String getPassword() {
            return accountUser.getPassword();
        }

        @Override
        public String getUsername() {
            return accountUser.getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return !accountUser.getIsLocked();
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }




        // Method to return the RoleGroup of the first role of the user
        //as from now we only have 2 groups.
        public RoleGroup returnRoleGroup() {
            return accountUser.getRoles().stream()
                    .findFirst() // Get the first role (modify if needed)
                    .map(Role::getRoleGroup) // Map to RoleGroup
                    .orElse(null); // Return null if no roles found
        }


}
