package account.audit.lisitener;

import account.Entity.AccountUser;
import account.Entity.EventLog;
import account.Erum.ActionEventName;
import account.Security.UserDetailsIml;
import account.Service.EventLogService;
import account.Service.UserServiceIml;
import account.audit.events.UserLockAndUnlockEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
/**
 * This component listens for authentication events and handles logging and user-related actions in response to these events.
 *
 * <p>The {@code AuthenticationEventListener} is responsible for capturing events such as successful and failed login attempts,
 * and publishing additional events or triggering actions such as logging those attempts or locking user accounts after multiple failed logins.</p>
 *
 * <p>It utilizes the following services:</p>
 * <ul>
 *     <li><b>EventLogService:</b> Service layer of EventLog entity</li>
 *     <li><b>UserServiceIml:</b> Handles user-specific operations, such as updating failed login attempts or locking accounts.</li>
 *     <li><b>ApplicationEventPublisher:</b> Publishes custom application events (e.g., brute force detection) to be handled elsewhere in the application.</li>
 * </ul>
 *
 * <p>This component is crucial for implementing security-related functionality, such as tracking failed login attempts and triggering brute-force detection mechanisms.</p>
 */
@Component
public class AuthenticationEventListener {

    private final EventLogService eventLogService;
    private final UserServiceIml userServiceIml;
    private final ApplicationEventPublisher applicationEventPublisher;


    @Autowired
    public AuthenticationEventListener(EventLogService eventLogService, UserServiceIml userServiceIml, ApplicationEventPublisher applicationEventPublisher) {
        this.eventLogService = eventLogService;
        this.userServiceIml = userServiceIml;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * AuthenticationSuccessEvent  is a build in cass
     *
     * @param authenticationSuccessEvent
     */
    public void onAuthenticationSuccess(AuthenticationSuccessEvent authenticationSuccessEvent) {

    }

    /**
     ** Handles authentication failure events due to bad credentials.

     * <p>This method is triggered when a user attempts to authenticate with incorrect credentials.
     * It logs the failed attempt and performs any necessary security actions, such as increasing
     * the failed login attempt count for the user, detecting potential brute-force attacks, or locking the user account.</p>
     *  @param event the event containing details of the failed authentication attempt, such as the username and exception.
     */
    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        //get the current user input name
        String username = event.getAuthentication().getName().toLowerCase();

        // Retrieve the current HttpServletRequest
        // the input request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes())
                .getRequest();

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getResponse();

        // Check if the request is available (non-null) and log the endpoint
        if (request != null) {
            // Get the HTTP method (GET, POST, etc.)
            String httpMethod = request.getMethod();

            String attemptedUsername = event.getAuthentication().getName().toLowerCase();

            // Get the controller endpoint (e.g., "/api/admin/user")
            String requestURI = request.getRequestURI();

            Optional<AccountUser> userExist = userServiceIml.findAccountUserByEmail(attemptedUsername);

            // Prepare JSON response
            // if there is a login failure
            Map<String, Object> jsonResponse = new HashMap<>();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Authentication failed for user: " + username);
            jsonResponse.put("requestURI", requestURI);

            //if the user exists
            // the operation for loging the login fail event and update the fail attempt if required
            if (userExist.isPresent()) {
                AccountUser accountUser = userExist.get();
                int currentFailAttempt = userExist.get().getFailAttempt(); // get the use current failed login attempts
                boolean userIsLocked = accountUser.getIsLocked();
                int updatedFailedAttempts = eventLogService.updateFailedAttempt(username); // update the attempts

                //if user still withing the max attempt limit
                if (updatedFailedAttempts <= eventLogService.returnMaxAttempt()) {
                    // User has not yet reached max failed attempts
                    eventLogService.saveLogEvent(new EventLog(ActionEventName.LOGIN_FAILED.toString(), username, requestURI, requestURI));

                } else {
                    // User has reached max failed attempts, log brute force and lock events
                    eventLogService.saveLogEvent(new EventLog(ActionEventName.LOGIN_FAILED.toString(), username, requestURI, requestURI));
                    eventLogService.saveLogEvent(new EventLog(ActionEventName.BRUTE_FORCE.toString(), username, requestURI, requestURI));
                    eventLogService.saveLogEvent(new EventLog(ActionEventName.LOCK_USER.toString(), username, "Lock user "+username, requestURI));
                }
            } else {
                // User does not exist, log failed login attempt with unknown details
                eventLogService.saveLogEvent(new EventLog(ActionEventName.LOGIN_FAILED.toString(), username, requestURI, requestURI));
            }

            // Set response status and content type
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            // Write the JSON response
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(jsonResponse);
                response.getWriter().write(json);
            } catch (Exception e) {
                e.printStackTrace(); // Handle exception
            }
        }

        }
    }




