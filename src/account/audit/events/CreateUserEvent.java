package account.audit.events;


/**
 * Represents an event triggered when a new AccountUser entity is created.
 *
 * <p>This event logs details such as the user, the resource they attempted to access, and the API endpoint involved.
 * Useful for auditing unauthorized access attempts and detecting potential security risks.</p>
 *
 * <p>Extends {@link UserActionEvent} to inherit common event properties.</p>
 */
public class CreateUserEvent extends UserActionEvent{

    private static final String DEFAULT_SUBJECT = "Anonymous";

    public CreateUserEvent(Object source, String action, String subject, String object, String path) {

        //ensure when the first user was created, it will still have the subject
        super(source, action, (subject==null? DEFAULT_SUBJECT: subject), object, path);
    }
}
