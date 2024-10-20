package account.Exception;

/**
 * When a user is assigned to a role group that is not allowed
 */
public class RoleGroupConflictException extends RuntimeException{
    private static final String defaultMessage = "The user cannot combine administrative and business roles!";
    private String endpoint;

    public RoleGroupConflictException(String endpint) {
        super(defaultMessage);
        this.endpoint = endpint;
    }

    public RoleGroupConflictException(String message, String endpint) {
        super(message);
        this.endpoint = endpint;
    }



    public String getEndpoint() {
        return endpoint;
    }


}
