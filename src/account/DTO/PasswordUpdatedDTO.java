package account.DTO;


/**
 * Data Transfer Object (DTO) for representing a password update event.
 * This class contains the necessary information after a user's password has been updated
 */
public class PasswordUpdatedDTO {
    private String email;
    private String password;

    public PasswordUpdatedDTO() {
    }

    public PasswordUpdatedDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
