package account.Exception;

/**
 * Exception when new password equals the old password
 */
public class PasswordMatchException extends  RuntimeException{

    private static final String defaultMessage = "The passwords must be different!";
    private String endpoint;

    public PasswordMatchException(String path) {
        super(defaultMessage); // Call to the superclass constructor with the default message
        this.endpoint = path;
    }

    public PasswordMatchException(String path, String message ) {
        super(defaultMessage);
        this.endpoint = path;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
