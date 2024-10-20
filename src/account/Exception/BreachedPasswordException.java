package account.Exception;

/**
 * Custom exception thrown when common password such as abc123 is used
 */
public class BreachedPasswordException extends RuntimeException {

    private static final String defaultMessage = "The password is in the hacker's database!";
    private String endpoint;

    public BreachedPasswordException(String endpoint) {
        super(defaultMessage);
        this.endpoint = endpoint;
    }

    public BreachedPasswordException(String endpoint, String message) {
        super(message);
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}