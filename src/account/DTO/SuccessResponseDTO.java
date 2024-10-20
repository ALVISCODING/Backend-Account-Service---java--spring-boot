package account.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object (DTO) for handling most of the successful operation response.
 */

public class SuccessResponseDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("user")
    private String email;
    private String status;



    public SuccessResponseDTO() {
    }


    public SuccessResponseDTO( String status) {
        this.status = status;
    }

    public SuccessResponseDTO(String email, String status) {
        this.email = email;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

