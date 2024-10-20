package account.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Exception thrown when the user entity is not found from the database.
 */
public class UserNotFoundException extends RuntimeException{
    private static final String defaultMessage = "User not found!";
    private int statusCode;
    private String endpoint;

    public UserNotFoundException(String endpoint) {
        super(defaultMessage);
        this.endpoint = endpoint;
    }

    public UserNotFoundException( int statusCode, String endpoint) {
        super(defaultMessage);
        this.statusCode = statusCode;
        this.endpoint = endpoint;
    }


    public UserNotFoundException(String message, int statusCode, String endpoint) {
        super(message);
        this.statusCode = statusCode;
        this.endpoint = endpoint;
    }


    public String getEndpoint() {
        return endpoint;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
