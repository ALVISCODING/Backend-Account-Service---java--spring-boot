package account.DTO;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) for representing a full error response body.
 * This class is used to provide structured error details in HTTP responses,
 * including status code, error message, and the endpoint path.
 */
public class FullErrorResponseBodyDTO {
    private LocalDate date;
    private int status;
    private String error;
    private String message;
    private String path;

    public FullErrorResponseBodyDTO() {
        this.date = LocalDate.now();
    }

    public FullErrorResponseBodyDTO( int status, String error, String message, String path) {
        this.date = LocalDate.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDate getDate() {
        return date;
    }
}
