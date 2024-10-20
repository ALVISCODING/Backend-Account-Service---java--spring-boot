package account.DTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) for handling AccountUser Entity data.
 * This class is used to encapsulate the necessary information for AccountUser Entity
 * to the end user.
 */
public class UserDTO {
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private List<String> roles;

    public UserDTO() {
    }

    public UserDTO(Long id, String name, String lastname, String email,List<String> roles) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.roles = roles != null ? new ArrayList<>(roles) : new ArrayList<>();
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
