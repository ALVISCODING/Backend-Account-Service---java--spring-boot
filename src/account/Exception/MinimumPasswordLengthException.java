package account.Exception;

/**
 * Custom exception for passwords that are less than 12 characters.
 */
public class MinimumPasswordLengthException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Password length must be 12 chars minimum!";
    private String endpoint;

    public MinimumPasswordLengthException(String endpoint) {
        super(DEFAULT_MESSAGE);
        this.endpoint = endpoint;
    }

    public MinimumPasswordLengthException(String endpoint, String message) {
        super(message);
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}