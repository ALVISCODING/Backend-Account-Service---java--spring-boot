package account.Exception;


/**
 * Custom exception for user already registered.
 */
public class UserAlreadyExistsException extends RuntimeException {

    // the default message for the end user
    private static final String DEFAULT_MESSAGE = "User exist!";
    private String endpoint;

    public UserAlreadyExistsException(String endpoint) {
        super(DEFAULT_MESSAGE);
        this.endpoint = endpoint;
    }

    public UserAlreadyExistsException(String endpoint, String message) {
        super(message);
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}