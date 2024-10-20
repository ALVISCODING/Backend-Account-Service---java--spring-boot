package account.audit.lisitener;

import account.Entity.EventLog;
import account.Service.EventLogService;
import account.audit.events.AccessDeniedEvent;
import account.audit.events.UserActionEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
/**
 * A component that listens for authorization failure events and logs denied access attempts.
 *
 * <p>{@code AuthorizationFailureListener} handles events where users attempt to access resources without proper permissions,
 * logging the failure and triggering any necessary security actions for auditing and monitoring.</p>
 */
@Component
public class AuthorizationFailureListener {

    private final EventLogService eventLogService;


    public AuthorizationFailureListener(EventLogService eventLogService) {
        this.eventLogService = eventLogService;
    }

    /**
     *Log the event when access denied error happened
     * @param event
     */
    @EventListener
    public void handleAccessDeniedEvent(AccessDeniedEvent event){
        //convert the email to eventLog
        EventLog eventLog = new EventLog(event.getAction(), event.getSubject(), event.getObject(), event.getPath());

        eventLogService.saveLogEvent(eventLog);

    }
}
