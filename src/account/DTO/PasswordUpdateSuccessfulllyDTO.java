package account.DTO;


/**
 * Data Transfer Object (DTO) for representing a event when password is uploaded successfully.
 * This class contains the necessary information after a user's password has been updated
 */
public class PasswordUpdateSuccessfulllyDTO {
    private String email;
    private String status;

    public PasswordUpdateSuccessfulllyDTO() {
    }

    public PasswordUpdateSuccessfulllyDTO(String email, String Status) {
        this.email = email;
        this.status = Status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
