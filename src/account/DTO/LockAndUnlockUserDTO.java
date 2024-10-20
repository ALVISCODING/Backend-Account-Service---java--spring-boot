package account.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

/**
 * Data Transfer Object (DTO) for handling user lock and unlock requests.
 * This DTO contains the necessary fields for the operation of locking and unlocking the user.
 */
    public class LockAndUnlockUserDTO {
        @NotEmpty
        private String email;
        @NotEmpty
        private String operation;

        @JsonCreator
        public LockAndUnlockUserDTO(
                @JsonProperty("user") String email,
                @JsonProperty("operation") String operation) {
            this.email = email;
            this.operation = operation;
        }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
