package account.audit.events;


/**
 * Represents an event triggered when the roles of an existing AccountUser entity are changed.
 *
 * <p>This event logs details such as the user whose roles were updated, the specific roles granted or removed,
 * and the API endpoint where the action occurred. It is valuable for auditing role modifications and ensuring
 * proper access control within the system.</p>
 *
 * <p>Extends {@link UserActionEvent} to inherit common properties for logging user-related actions,
 * such as the subject (user who performed the change), object (user whose roles were modified),
 * and path (endpoint where the action took place).</p>
 */
public class UserRoleChangeEvent extends UserActionEvent{

    private final String operation ;

    public UserRoleChangeEvent(Object source, String action, String subject, String object, String path) {
        super(source, action, subject, object, path);
        this.operation = action;
    }

    public String getOperation() {
        return operation;
    }
}
