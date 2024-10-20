package account.audit.lisitener;

import account.Entity.EventLog;
import account.Service.EventLogService;
import account.audit.events.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * A listener component that handles various user-related events and logs them using the {@code EventLogService}.
 *
 * <p>{@code UserActionEventListener} listens for events such as password changes, user creation, role changes,
 * user deletion, and user lock/unlock actions. It converts these events into {@code EventLog} entries and stores them
 * for auditing and security purposes.</p>
 *
 * <p>This class processes events by capturing the action, subject, object, and path, ensuring that all relevant details are logged accurately.
 * Each method corresponds to a specific event type and adapts the logging format based on the event.</p>
 *
 * <p>The events are handled by methods annotated with {@link EventListener}, ensuring Spring's event-driven mechanism
 * triggers the appropriate logging when each event occurs.</p>
 */
@Component
    public class UserActionEventListener {


        private final EventLogService eventLogService;

        @Autowired
        public UserActionEventListener(EventLogService eventLogService) {
            this.eventLogService = eventLogService;
        }


    /**
     *
     * @param event log the ChangePasswordEvent when successful
     */
    @EventListener
        public void handleChangePasswordEvent(ChangePasswordEvent event) {

            //convert the email to eventLog
            EventLog eventLog = new EventLog(event.getAction(), event.getEmail().toLowerCase(), event.getEmail(), event.getPath());

            eventLogService.saveLogEvent(eventLog);
        }

    /**
     *
     * @param event log the user creation event
     */
    @EventListener
        public void handledCreateUserEvent(CreateUserEvent event) {

            //convert the email to eventLog
            String subject = event.getSubject().equalsIgnoreCase("Anonymous")? "Anonymous" : event.getSubject().toLowerCase();

            EventLog eventLog = new EventLog(event.getAction(), subject, event.getObject().toLowerCase(), event.getPath());

            eventLogService.saveLogEvent(eventLog);
        }


    /**
     *
     * @param event log the user's role modification event
     */
    @EventListener
        public void handleUserRoleChangeEvent(UserRoleChangeEvent event){
            EventLog eventLog = new EventLog(event.getAction(), event.getSubject().toLowerCase(), event.getObject(), event.getPath());
            eventLogService.saveLogEvent(eventLog);

        }

    /**
     *
     * @param event log when user is deleted
     */
    @EventListener
        public void handleDeleteUserEvent(DeleteUserEvent event){
            //  "path": "/api/admin/user/petrpetrov@acme.com" need to be this style

           // String path = event.getPath() + "/"+ event.getObject();
            EventLog eventLog = new EventLog(event.getAction(), event.getSubject().toLowerCase(), event.getObject(), event.getPath());
            eventLogService.saveLogEvent(eventLog);

        }

    /**
     *
     * @param event log if the use is lock or unlock
     */
    @EventListener
        public void handleUserLockAndUnlockEvent(UserLockAndUnlockEvent event){
            String operation = event.getOperation();

            String action = event.getAction().toUpperCase(Locale.ROOT);

            //e.g "Lock user maxmustermann@acme.com",
            //object will change according to the action lock & unlock
            String object = action.equals("LOCK_USER")
                    ? "Lock user " + event.getObject().toLowerCase()
                    : "Unlock user " + event.getObject().toLowerCase(); // Add a space here

            EventLog eventLog = new EventLog(action, event.getSubject().toLowerCase(), object, event.getPath());
            eventLogService.saveLogEvent(eventLog);
        }

    }
