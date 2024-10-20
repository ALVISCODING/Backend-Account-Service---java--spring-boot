package account.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a user account in the system.
 *
 * <p>This entity stores user details such as name, email, and password,
 * as well as account status information including lock status and failed login attempts.
 * The relationship with roles is managed through a many-to-many association.</p>
 */
@Entity
public class AccountUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String lastname;

    @NotBlank
    @Pattern(regexp = ".*@acme\\.com$", message = "Email must end with @acme.com")
    private String email;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Length(min = 12,message = "The minimum length of the password is 12 characters")
    private String password;


    private Boolean isLocked=false;

    @Column(name = "fail_attempt", nullable = false, columnDefinition = "int default 0")
    private int failAttempt = 0;

    private LocalDate lockTime ;

    @ManyToMany(fetch = FetchType.EAGER)    //create a join table
    @JoinTable(
            name = "accountUser_roles", // name of that table  and  *This is the owning side*
            joinColumns = @JoinColumn(name = "accountUser_id"),  //Defines the foreign key column for the current entity.
            inverseJoinColumns = @JoinColumn(name = "role_id") //Defines the foreign key column for another entity (which is a Role)
    )
    private Set<Role> Roles = new HashSet<>();

    public AccountUser() {
    }

    public AccountUser(String name, String lastname, String email, String password , Set<Role> Roles) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.Roles = Roles;
    }


    public Boolean getLocked() {
        return isLocked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return Roles;
    }

    public void setRoles(Set<Role> Roles) {
        this.Roles = Roles;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public int getFailAttempt() {
        return failAttempt;
    }

    public void setFailAttempt(int failAttempt) {
        this.failAttempt = failAttempt;
    }

    public LocalDate getLockTime() {
        return lockTime;
    }

    public void setLockTime(LocalDate lockTime) {
        this.lockTime = lockTime;
    }

    @Override
    public String toString() {
        return "AccountUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + Roles +
                '}';
    }
}
