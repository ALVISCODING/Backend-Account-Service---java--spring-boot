package account.Exception;

/**
 * Exception thrown when the payment data provided during validation contains errors.
 * This is primarily used during the validation stage of the initial data input.
 */
public class PaymentDTOException  extends RuntimeException {

    // the default message for the end user
    private static final String DEFAULT_MESSAGE = "One or more fields in the payment data contain errors. Please review and correct the input.";
    private String endpoint;

    public PaymentDTOException(String endpoint) {
        this.endpoint = endpoint;
    }

    public PaymentDTOException(String message, String endpoint) {
        super(message);
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}

