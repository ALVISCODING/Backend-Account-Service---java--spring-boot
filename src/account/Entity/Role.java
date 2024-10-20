package account.Entity;

import account.Erum.RoleGroup;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a role assigned to users in the system.
 *
 * <p>This entity defines the various roles that can be assigned
 * to users, determining their access levels and permissions within
 * the application.</p>
 */
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String nameOfTheRole;
    @NotNull
    @Enumerated(EnumType.STRING) // store the value in string in jpa
    @JsonIgnore
    private RoleGroup roleGroup;

    @ManyToMany(mappedBy = "Roles") // Inverse side of the relationship
    @JsonIgnore // Prevent serialization of users when serializing Role
    private Set<AccountUser> users = new HashSet<>(); // A role is assigned to many users


    public Role() {
    }

    public Role(String nameOfTheRole, RoleGroup roleGroup) {
        this.nameOfTheRole = nameOfTheRole;
        this.roleGroup = roleGroup;
    }

    public String getNameOfTheRole() {
        return nameOfTheRole;
    }

    public void setNameOfTheRole(String nameOfTheRole) {
        this.nameOfTheRole = nameOfTheRole;
    }

    public RoleGroup getRoleGroup() {
        return roleGroup;
    }

    public void setRoleGroup(RoleGroup roleGroup) {
        this.roleGroup = roleGroup;
    }

    public Set<AccountUser> getUsers() {
        return users;
    }

    public void setUsers(Set<AccountUser> users) {
        this.users = users;
    }
}
