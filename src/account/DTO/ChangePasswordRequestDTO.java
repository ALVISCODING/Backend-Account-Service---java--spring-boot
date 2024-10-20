package account.DTO;

import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

/**
 * Data Transfer Object (DTO) for handling password change requests.
 * This DTO contains the necessary fields for users to submit new passwords
 * and includes validation annotations to ensure password constraints.
 */
public class ChangePasswordRequestDTO {
        @NotEmpty(message = "Password can't be blank")
        @Length(min = 12)
        private  String new_password;

    public ChangePasswordRequestDTO() {
    }

    public ChangePasswordRequestDTO(String new_password) {

        this.new_password = new_password;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }
}
