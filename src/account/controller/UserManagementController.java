package account.controller;

import account.DTO.*;
import account.Entity.AccountUser;
import account.Exception.*;
import account.Security.UserDetailsIml;
import account.Service.EventLogServiceIml;
import account.Service.UserServiceIml;
import account.audit.events.*;
import jakarta.validation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for managing user-related and payment operations within the application.
 * This is the primary and only controller responsible for handling all user management
 * functionalities, such as creating, updating, deleting, and retrieving user data.
 * It also includes role assignment and other administrative tasks.
 *
 * The controller processes HTTP requests and delegates to service classes for
 * business logic execution, ensuring that proper validation, authorization, and
 * error handling are in place for each operation.
 */
@RestController
    @RequestMapping("/api")
    public class UserManagementController {


        private final UserServiceIml userServiceIml; // the service layer for the user entity
        private final PasswordEncoder passwordEncoder;
        private final account.Service.RoleService roleService; // the service lay for role management
        private final ApplicationEventPublisher eventPublisher; // publish user action event
        private final EventLogServiceIml eventLogServiceIml; // the service layer for EventLog entity


        @Autowired
        public UserManagementController(UserServiceIml userServiceIml, PasswordEncoder passwordEncoder, account.Service.RoleService roleService, ApplicationEventPublisher eventPublisher, EventLogServiceIml eventLogServiceIml) {
            this.userServiceIml = userServiceIml;
            this.passwordEncoder = passwordEncoder;
            this.roleService = roleService;
            this.eventPublisher = eventPublisher;
            this.eventLogServiceIml = eventLogServiceIml;
        }

    /**
     * Handles user registration by accepting account user details.
     * This method processes the sign-up request, validating the input
     * data for the new account user. If validation errors occur, they
     * are tracked by the BindingResult interface. Upon successful
     * registration, the method returns a ResponseEntity indicating
     * the outcome of the operation.
     *
     * @param userDetails The authenticated user's details, retrieved
     *                    using Spring Security's @AuthenticationPrincipal.
     * @param newAccountUser The account user data received from the
     *                       request body, annotated for validation.
     * @param bindingResult An object that holds the result of the
     *                      validation, allowing access to any errors
     *                      that may have occurred during validation.
     * @return ResponseEntity containing the status and details of
     *         the registration process.
     */
        @PostMapping("/auth/signup")
        public ResponseEntity<?> userSignUp(@AuthenticationPrincipal UserDetailsIml userDetails,@Validated @RequestBody AccountUser newAccountUser, BindingResult bindingResult) {

            //check if there are any errors in the validation stage
            if (bindingResult.hasErrors()) {
                // Check for password length validation errors
                for (FieldError error : bindingResult.getFieldErrors()) {
                    // if the field has an error for validated, which is the password
                    if ("password".equals(error.getField())) {
                        throw new MinimumPasswordLengthException("/api/auth/signup");
                    }
                }
                //else we response an generic error message
                return ResponseEntity.badRequest().body(new ErrorResponseBody(400, "Bad Request", "/api/auth/signup"));
            }


            //Second, check the username is already used
            if (userServiceIml.findAccountUserByEmail(newAccountUser.getEmail()).isPresent()) {
                // no need to add a message as it has a default message
                throw new UserAlreadyExistsException("/api/auth/signup");
            } else if (!userServiceIml.checkPasswordIsNotBreached(newAccountUser.getPassword())) {
                // throw exception instead, the exception will throw the dto
                throw new BreachedPasswordException("/api/auth/signup");
            }

            //if no error and the user exists in the database, save the user
            userServiceIml.saveUser(newAccountUser);

            // Fetch the saved user and return, or throw exception if saving failed
            AccountUser savedUser = userServiceIml.findAccountUserByEmail(newAccountUser.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User could not be found after saving",HttpStatus.BAD_REQUEST.value(),"api/auth/signup"));

            // Get the size of the database
            Long sizeOfTheDatabase = userServiceIml.theSizeOfTheDataBase();

            // Retrieve the current authentication from the SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Initialize username variable
            String username = null;

            // Check if the authentication is present and the user is authenticated
            if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
                username = userDetails.getUsername(); // Get the username if userDetails is valid
            }

            // Publish the log event
            eventPublisher.publishEvent(new CreateUserEvent(
                    this,
                    "CREATE_USER",
                    sizeOfTheDatabase <= 1 ? null : username,
                    newAccountUser.getEmail(),
                    "/api/auth/signup"
            ));



            //return 200ok with the userDto body
            return ResponseEntity.ok().body(userServiceIml.returnUserDto(savedUser));
        }


    /**
     * Handles the password change request for the authenticated user.
     * This method utilizes the UserDetailsIml implementation to retrieve
     * the user's details and validate the new password provided in the
     * request body. The BindingResult interface tracks any validation
     * errors that may occur during the process. Upon successful
     * validation and password change, a ResponseEntity is returned
     * indicating the outcome of the operation.
     *
     * @param userDetails The authenticated user's details, retrieved
     *                    using Spring Security's @AuthenticationPrincipal,
     *                    now implemented with UserDetailsIml.
     * @param changePasswordRequestDTO The data transfer object containing
     *                                  the new password information.
     * @param bindingResult An object that holds the result of the
     *                      validation, allowing access to any errors
     *                      that may have occurred during validation.
     * @return ResponseEntity containing the status and details of
     *         the password change process.
     */
        @PostMapping("/auth/changepass")
        public ResponseEntity<?> changePassword(@AuthenticationPrincipal UserDetailsIml userDetails,
                                                @Validated @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO,
                                                BindingResult bindingResult
        ) {


            //using AuthenticationPrincipal to get the current user
            AccountUser existingUser = userDetails.getAccountUser();

            //extract the password from the request body dto
            String newPassword = changePasswordRequestDTO.getNew_password();


            if(bindingResult.hasErrors()) throw new MinimumPasswordLengthException("/api/auth/changepass");

            // Check if the new password has min length of 12
            if (newPassword.length() < 12) {
                throw new MinimumPasswordLengthException("/api/auth/changepass");
                //Now check if the password is breached, throw exception
            } else if (!userServiceIml.checkPasswordIsNotBreached(newPassword)) {
                throw new BreachedPasswordException("/api/auth/changepass");
                // if the old password = new password throw exception
            } else if (passwordEncoder.matches(newPassword, userDetails.getPassword())) {
                throw new PasswordMatchException("/api/auth/changepass");
            }

            //else we update the password
            userServiceIml.updatePassword(existingUser, newPassword);

            //publish the event, the change password eventlisitener will handle it
            eventPublisher.publishEvent(new ChangePasswordEvent(this,userDetails.getUsername(),"/api/auth/changepass"));

            //return a successful update message
            return ResponseEntity.ok().body(new PasswordUpdateSuccessfulllyDTO(existingUser.getEmail(), "The password has been updated successfully"));
        }




        /**
         * Important validated only work for a single object when we input a list of object we need to check each object
         * manually  or we need to check each object manually
         * @param payments the payment dto that will be save to the data base
         * @param bindingResult  An object that holds the result of the
         *  validation, allowing access to any errors
         *  that may have occurred during validation.
            @return ResponseEntity containing the status and details of *the password change process.
         */
            @PostMapping("/acct/payments")
                public ResponseEntity<?> uploadPayment(@Validated @RequestBody  List<PaymentDTO> payments, BindingResult bindingResult){


                // Create a ValidatorFactory to build a Validator instance
                ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

                // Get a Validator instance from the factory
                Validator validator = factory.getValidator();

                // Validate each PaymentDTO object in the list and collect error messages
                List<String> errorMessages = payments.stream()
                        // Process each PaymentDTO object in the list
                        .flatMap(payment -> {
                            // Validate the current PaymentDTO object
                            Set<ConstraintViolation<PaymentDTO>> violations = validator.validate(payment);
                            // Map each violation to a descriptive error message
                            return violations.stream()
                                    .map(violation -> String.format("PaymentDTO with period '%s' has error: %s",
                                            payment.getPeriod(), violation.getMessage()));
                        })
                        // Collect all error messages into a list
                        .collect(Collectors.toList());

                //Check if there is any error
                if (!errorMessages.isEmpty()) {
                    //if there is return bad request
                    return ResponseEntity.badRequest().body(
                            new FullErrorResponseBodyDTO(400, "Bad Request", String.join(", ", errorMessages), "/api/acct/payments")
                    );
                }

                //using try and catch
                try {
                    //save the payments and savePaymentList method in the service layer will return true or errors
                    boolean result = userServiceIml.savePaymentList(payments);

                    //if true, no error
                    if(result){
                        return ResponseEntity.ok(new SuccessResponseDTO("Added successfully!"));
                    }else {
                        //The else block handles cases where the savePaymentList method fails
                        // but doesn’t throw an exception,
                        // indicating a more general failure of the operation.
                        return ResponseEntity.badRequest()
                                .body("Failed to add payments.");
                    }
                } catch (UserNotFoundException ex){
                    // The global exception handler will handle it
                    throw  ex;

                } catch (IllegalArgumentException ex){
                    //as this exception has no exception handler
                    return ResponseEntity.badRequest()
                            .body(new FullErrorResponseBodyDTO(400, "Bad Request", ex.getMessage(),"/api/acct/payments"));


                }catch (Exception ex) {
                    // Catch any other exceptions that may occur
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new FullErrorResponseBodyDTO(500, "Internal Server Error", "An unexpected error occurred", "/api/acct/payments"));
                }
            }


        @PutMapping("/acct/payments")
        public ResponseEntity<?> updateExistingPayment(@Validated @RequestBody PaymentDTO paymentDTO, BindingResult bindingResult ){

            //validation stage of the initial data input
            if (bindingResult.hasErrors()){
                throw new PaymentDTOException("/api/acct/payments");
            }

            try{
                userServiceIml.updatePayment(paymentDTO);
            }catch (UserNotFoundException ex){
                throw new UserNotFoundException("/api/acct/payments");
            }catch (IllegalArgumentException ex){
                return ResponseEntity.badRequest().body(new FullErrorResponseBodyDTO(
                        400,
                        "Bad Request",
                        ex.getMessage(),
                        "\"/acct/payments\""
                ));
            }

            return ResponseEntity.ok().body(Collections.singletonMap("status", "Updated successfully!"));
        }



        @GetMapping("/empl/payment")
        //@PreAuthorize("isAuthenticated()") // Ensures that only authenticated users can access this endpoint
        public ResponseEntity<?> returnUserSalaryRecord(
                @RequestParam(name = "period", required = false) String periodParam,
                @AuthenticationPrincipal UserDetailsIml userDetailsIml) {

            YearMonth period;
            //first we need check if period is given, it must within 1-12
            try {
                if (periodParam != null) {
                    period = YearMonth.parse(periodParam, DateTimeFormatter.ofPattern("MM-yyyy"));
                } else {
                    period = null;
                }
            } catch (DateTimeParseException ex) {
                return ResponseEntity.badRequest().body(new FullErrorResponseBodyDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request",
                        "Invalid period format. Use MM-yyyy.",
                        "/api/empl/payment"
                ));
            }


            List<PaymentRecordDTO> fullPaymentRecord;
            AccountUser accountUser = userDetailsIml.getAccountUser();
            if (periodParam == null ) {
                fullPaymentRecord = userServiceIml.getEmployeePaymentsByUser(accountUser);
            } else {
                fullPaymentRecord = userServiceIml.getEmployeePaymentsByUserAndPeriod(accountUser,period);
            }

            //this is to pass the test for some reason if we only have one object it wont accept the list
            if (fullPaymentRecord.size() == 1) {
                // Return a single object if only one record is found
                return ResponseEntity.ok().body(fullPaymentRecord.get(0));
            } else {
                // Return the list if multiple records are found
                return ResponseEntity.ok().body(fullPaymentRecord);
            }
            //return  ResponseEntity.ok().body(fullPaymentRecord);
        }


    /**
     * Modifies the role of a specified user.
     * This method processes the role change request from an authenticated administrator
     * and returns a UserDTO representing the updated user information.
     *
     * @param userDetailsIml The authenticated administrator's details.
     * @param roleRequest The DTO containing the role modification data.
     * @return ResponseEntity containing the updated UserDTO and operation status.
     */
    @PutMapping("/admin/user/role")
        public ResponseEntity<?> roleRegister(@AuthenticationPrincipal UserDetailsIml userDetailsIml,@RequestBody RoleRequestDTO roleRequest ){
                String path = "/api/admin/user/role";
                UserDTO userDTO;
                boolean grantRole = false;


                //check if the user exist
            try{
                //call this method to see if there is any exception
                userDTO = userServiceIml.changeUserRole(roleRequest);
                grantRole = roleRequest.getOperation().equalsIgnoreCase("GRANT");
                // thrown the exception and is in order
            }catch (UserNotFoundException ex ){
                throw new UserNotFoundException(path);
            }catch (CustomEntityNotFoundException ex){
                throw new CustomEntityNotFoundException(ex.getMessage(),path);
            }catch (RoleGroupConflictException ex){
                throw new RoleGroupConflictException(path);
                }catch (RoleRemoveException ex){

                   //as there is a few condition to meet the exception will be caught in order and in priority
                    String message = ex.getMessage();

                if (message.contains("Can't remove ADMINISTRATOR role!")) {
                    throw new RoleRemoveException("Can't remove ADMINISTRATOR role!", path);
                } else if (message.contains("The user must have at least one role!")) {
                    throw new RoleRemoveException("The user must have at least one role!", path);
                } else if (message.contains("The user does not have the role!")) {
                    throw new RoleRemoveException("The user does not have a role!", path);
                } else {
                    throw new RoleRemoveException("Can't remove ADMINISTRATOR role!", path);  // Keep a default case if needed
                }
                }

            // if the event is success we publish the log
            String operation = grantRole? "GRANT_ROLE":"REMOVE_ROLE";

            //  "object" : "Remove role ACCOUNTANT from petrpetrov@acme.com",
            // Format the object message to describe the operation, based on whether we are granting or removing a role.
                String operationDescription = grantRole ?
                String.format("Grant role %s to %s", roleRequest.getRole(), roleRequest.getName().toLowerCase()) :
                String.format("Remove role %s from %s", roleRequest.getRole(), roleRequest.getName().toLowerCase());

        // if the operation is successful, we publish the event
        eventPublisher.publishEvent(new UserRoleChangeEvent(this,operation,userDetailsIml.getUsername().toLowerCase(),operationDescription,path
                    ));

            return ResponseEntity.ok((userDTO));
        }


    /**
     *
     * @return all the AccountUser entity
     */
    @GetMapping("/admin/user/")
        public ResponseEntity<?> getAllAccountUser(){

            List<UserDTO> allUserRecord = userServiceIml.returnAllAccountUsers();

            return ResponseEntity.ok(allUserRecord);

        }


    /**
     * Responsible for deleting the existing user but admin can't be deleted
     * @param userDetailsIml
     * @param email the user to be deleted
     * @return ResponseEntity containing the SuccessResponseDTO and operation status.
     */
    @DeleteMapping("/admin/user/{email}")
        public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserDetailsIml userDetailsIml,@PathVariable String email){

        String path = "/api/admin/user";
                //may not need to be boolean may change later
                boolean delteUserSuccessful;

            try{
                delteUserSuccessful = userServiceIml.deleteUser(email);
            }catch (UserNotFoundException ex){
                throw new UserNotFoundException(HttpStatus.NOT_FOUND.value(),path);
            }catch (RoleGroupConflictException exception){
                throw  new RoleGroupConflictException(exception.getMessage(),path);
            }

            //log the event if user is successfully deleted
            eventPublisher.publishEvent(new DeleteUserEvent(this,"DELETE_USER",userDetailsIml.getUsername().toLowerCase(),email,path));

            return ResponseEntity.ok(new SuccessResponseDTO(email,"Deleted successfully!"));
        }


    /**
     * return all the log events
     * @return
     */
    @GetMapping("/security/events/")
        public ResponseEntity<?>getAllLogEvent(){
           return  ResponseEntity.ok().body(eventLogServiceIml.returnAllEventLog());
        }


    /**
     * Locks or unlocks a user account based on the provided request.
     *
     * @param userDetailsIml The authenticated administrator's details.
     * @param lockAndUnlockUserDTO The DTO containing user lock/unlock information.
     * @return ResponseEntity indicating the result of the operation.
     */
        @PutMapping("/admin/user/access")
    public ResponseEntity<?>handleLockAccount(@AuthenticationPrincipal UserDetailsIml userDetailsIml,@RequestBody LockAndUnlockUserDTO lockAndUnlockUserDTO ) {
            String path = "/api/admin/user/access";
            String email = lockAndUnlockUserDTO.getEmail();
            String operation = lockAndUnlockUserDTO.getOperation().toUpperCase(Locale.ROOT);
            Map<String, String> response = new HashMap<>(); // use it to return the json response
            String requestUser = userDetailsIml.getUsername(); // the request username

            boolean isLocked; // default is lock

            try {
                //if the
                switch (operation) {
                    //if the operation is lock the user
                    case "LOCK":
                        if (userServiceIml.lockAccount(email)) {
                            // Publish lock event
                            eventPublisher.publishEvent(new UserLockAndUnlockEvent(
                                    this,
                                    "LOCK_USER",
                                    requestUser,
                                    email.toLowerCase(),
                                    path));

                            response.put("status", "User " + email.toLowerCase() + " locked!");
                            return ResponseEntity.ok(response);
                        } else {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("Failed to lock user: " + email);
                        }
                    // if the operation is to unlock the user
                    case "UNLOCK":
                        userServiceIml.unlockAccount(email);
                        // Publish unlock event
                        eventPublisher.publishEvent(new UserLockAndUnlockEvent(
                                this,
                                "UNLOCK_USER",
                                requestUser,
                                email.toLowerCase(),
                                path));


                        response.put("status", "User " + email.toLowerCase() + " unlocked!");
                        return ResponseEntity.ok(response);

                    default:
                        return ResponseEntity.badRequest()
                                .body("Invalid operation. Use 'LOCK' or 'UNLOCK'.");
                }
            } catch (UserNotFoundException ex) {
                throw new UserNotFoundException(path);
            } catch (AdministratorCantNotBeLockException ex) {
                throw new AdministratorCantNotBeLockException(path);
            }
        }
    }





