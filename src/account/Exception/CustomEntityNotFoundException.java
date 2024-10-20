package account.Exception;

import jakarta.persistence.EntityNotFoundException;

/**
 * When the entity is not found in the database
 * this is a more general exception
 */
public class CustomEntityNotFoundException extends EntityNotFoundException {
    private static final String defaultMessage = "One or more of the Entity not found";
    private String endpoint;

    public CustomEntityNotFoundException(String endpoint) {
        super(defaultMessage);
        this.endpoint = endpoint;
    }

    public CustomEntityNotFoundException(String message, String endpoint) {
        super(message);
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

}
