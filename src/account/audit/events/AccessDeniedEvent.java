package account.audit.events;


/**
 * Represents an event triggered when a user is denied access to a resource.
 *
 * <p>This event logs details such as the user, the resource they attempted to access, and the API endpoint involved.
 * Useful for auditing unauthorized access attempts and detecting potential security risks.</p>
 *
 * <p>Extends {@link UserActionEvent} to inherit common event properties.</p>
 */
public class AccessDeniedEvent extends  UserActionEvent{


    public AccessDeniedEvent(Object source, String action, String subject, String object, String path) {
        super(source, action, subject, object, path);
    }


}
