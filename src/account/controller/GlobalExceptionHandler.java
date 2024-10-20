package account.controller;

import account.DTO.FullErrorResponseBodyDTO;
import account.Exception.*;
import jakarta.persistence.ElementCollection;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Centralized handler for all exceptions thrown by the application.
 *
 * <p>This class intercepts exceptions across all endpoints and provides a unified
 * response structure. Each handled exception includes a default message, HTTP status,
 * and the endpoint (path) where the exception occurred.</p>
 *
 * <p>The handler returns a custom DTO that contains detailed information about
 * the exception, allowing for easier debugging and consistent error handling
 * across the entire application.</p>
 *
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Private method to extract request details
    private RequestDetails getRequestDetails() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();

        String httpMethod = request != null ? request.getMethod() : "UNKNOWN";
        String requestURI = request != null ? request.getRequestURI() : "UNKNOWN";

        return new RequestDetails(httpMethod, requestURI);
    }

    // Custom response class to encapsulate request details
    private static class RequestDetails {
        private final String httpMethod;
        private final String requestURI;

        public RequestDetails(String httpMethod, String requestURI) {
            this.httpMethod = httpMethod;
            this.requestURI = requestURI;
        }

        public String getHttpMethod() {
            return httpMethod;
        }

        public String getRequestURI() {
            return requestURI;
        }
    }


    /**
     * Handles UserAlreadyExistsException and returns a 400 Bad Request response
     * with details about the error, including the message and the endpoint.
     *
     * @param ex the exception that was thrown
     * @return a ResponseEntity with the error details and HTTP status
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {

        // we create a error message dto first
        FullErrorResponseBodyDTO response = new FullErrorResponseBodyDTO(
                HttpStatus.BAD_REQUEST.value(), // http error code
                "Bad Request", // error status
                ex.getMessage(), // The default message set in the exception
                ex.getEndpoint()  // the path that exception was thrown
        );

        //we create a responseEntity with body and http protocol
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles MinimumPasswordLengthException and returns a 400 Bad Request response
     * with details about the error, including the message and the endpoint.
     *
     * @param ex the exception that was thrown
     * @return a ResponseEntity with the error details and HTTP status
     */
    @ExceptionHandler(MinimumPasswordLengthException.class)
    public ResponseEntity<?> handleMinimumPasswordLengthException(MinimumPasswordLengthException ex) {

        FullErrorResponseBodyDTO response = new FullErrorResponseBodyDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(), // The message set in the exception
                ex.getEndpoint()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles BreachedPasswordException and returns a 400 Bad Request response
     * with details about the error, including the message and the endpoint.
     *
     * @param ex the exception that was thrown
     * @return a ResponseEntity with the error details and HTTP status
     */
    @ExceptionHandler(BreachedPasswordException.class)
    public ResponseEntity<?> handleBreachedPasswordException(BreachedPasswordException ex) {

        FullErrorResponseBodyDTO response = new FullErrorResponseBodyDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(), // The message set in the exception
                ex.getEndpoint()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles PasswordMatchException and returns a 400 Bad Request response
     * with details about the error, including the message and the endpoint.
     *
     * @param ex the exception that was thrown
     * @return a ResponseEntity with the error details and HTTP status
     */
    @ExceptionHandler(PasswordMatchException.class)
    public ResponseEntity<?> handlePasswordNotMatchException(PasswordMatchException ex){
        FullErrorResponseBodyDTO response = new FullErrorResponseBodyDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(), // The message set in the exception
                ex.getEndpoint()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles UserNotFoundException and returns a 404 Not Found response
     * with details about the error, including the message and the endpoint.
     *
     * @param ex the exception that was thrown
     * @return a ResponseEntity with the error details and HTTP status
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException ex) {
        // Create a DTO
        FullErrorResponseBodyDTO response = new FullErrorResponseBodyDTO(
                HttpStatus.NOT_FOUND.value(),
                //ex.getStatusCode() == HttpStatus.NOT_FOUND.value() ? "Not Found" : "Bad Request", //as delete user need to return 404
                "Not Found",  //as delete user need to return 404
                ex.getMessage(), // The message set in the exception
                ex.getEndpoint()
        );

        // Set the appropriate status code based on the exception
        //HttpStatus status = ex.getStatusCode() == HttpStatus.NOT_FOUND.value() ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles PaymentDTOException and returns a 400 Bad Request response
     * with details about the error, including the message and the endpoint.
     * this error is related to the input filed within the paymentDTO
     *
     * @param ex the exception that was thrown
     * @return a ResponseEntity with the error details and HTTP status
     */
    @ExceptionHandler(PaymentDTOException.class)
    public ResponseEntity<?>handlePaymentDTOException(PaymentDTOException ex) {
        FullErrorResponseBodyDTO response = new FullErrorResponseBodyDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(), // The message set in the exception
                ex.getEndpoint()
        );
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }


    /**
     * Handles CustomEntityNotFoundException and returns a 404 Not Found response
     * with details about the error, including the message and the endpoint.
     *
     * @param ex the exception that was thrown
     * @return a ResponseEntity with the error details and HTTP status
     */
    @ExceptionHandler(CustomEntityNotFoundException.class)
    public ResponseEntity<?>handleCustomEntityNotFoundException(CustomEntityNotFoundException ex){
        FullErrorResponseBodyDTO response = new FullErrorResponseBodyDTO(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(), // The message set in the exception
                ex.getEndpoint()
        );
        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
    }


    /**
     * Handles RoleGroupConflictException and returns a 400 Bad Request response
     * with details about the error, including the message and the endpoint.
     *
     * @param ex the exception that was thrown
     * @return a ResponseEntity with the error details and HTTP status
     */
    @ExceptionHandler(RoleGroupConflictException.class)
    public ResponseEntity<?>handleRoleGroupConflictException(RoleGroupConflictException ex){
        FullErrorResponseBodyDTO responce = new FullErrorResponseBodyDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                ex.getEndpoint()
        );
        return new ResponseEntity<>(responce,HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles RoleRemoveException and returns a 400 Bad Request response
     * with details about the error, including the message and the endpoint.
     *
     * @param ex the exception that was thrown
     * @return a ResponseEntity with the error details and HTTP status
     */
    @ExceptionHandler(RoleRemoveException.class)
    public ResponseEntity<?>handleRoleRemoveException(RoleRemoveException ex){
        FullErrorResponseBodyDTO responce = new FullErrorResponseBodyDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                ex.getEndpoint()
        );
        return new ResponseEntity<>(responce,HttpStatus.BAD_REQUEST);

    }


    /**
     * Handles AdministratorCantNotBeLockException and returns a 400 Bad Request response
     * with details about the error, including the message and the endpoint.
     *
     * @param ex the exception that was thrown
     * @return a ResponseEntity with the error details and HTTP status
     */
    @ExceptionHandler(AdministratorCantNotBeLockException.class)
    public ResponseEntity<?>handleAdministratorCantNotBeLockException(AdministratorCantNotBeLockException ex){
        FullErrorResponseBodyDTO responce = new FullErrorResponseBodyDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                getRequestDetails().requestURI
        );
        return new ResponseEntity<>(responce,HttpStatus.BAD_REQUEST);
    }


}
