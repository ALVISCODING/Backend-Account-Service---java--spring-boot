package account.Exception;


/**
 * Custom exception thrown when attempting to lock an Administrator.
 * Administrators cannot be locked in the system, and this exception
 * is used to prevent such actions.
 */
public class AdministratorCantNotBeLockException extends RuntimeException{
    private static final String defaultMessage = "Can't lock the ADMINISTRATOR!";
    private String endpoint;


    public AdministratorCantNotBeLockException(String endpoint) {
        super(defaultMessage);
        this.endpoint = endpoint;
    }

    public AdministratorCantNotBeLockException(String endpoint, String message) {
        super(message);
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

}
