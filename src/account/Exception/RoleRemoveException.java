package account.Exception;

/**
 * Exception thrown when there is a conflict during the removal of a role from a user.
 * This custom exception is used to indicate an issue with the role removal process.
 */
public class RoleRemoveException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "A conflict happened when trying to remove a given role from user";
    private String endpoint;

    public RoleRemoveException(String endpoint) {
        super(DEFAULT_MESSAGE);
        this.endpoint = endpoint;
    }

    public RoleRemoveException(String message, String endpoint) {
        super(message);
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
