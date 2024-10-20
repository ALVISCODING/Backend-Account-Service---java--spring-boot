package account.audit.events;
/**
 * Represents an event triggered when an AccountUser entity is delete.
 *
 * <p>This event logs details such as the user, the resource they attempted to access, and the API endpoint involved.
 * Useful for auditing unauthorized access attempts and detecting potential security risks.</p>
 *
 * <p>Extends {@link UserActionEvent} to inherit common event properties.</p>
 */
public class DeleteUserEvent  extends  UserActionEvent{

    //login
    private final String requesterEmail;
    private final  String userToDeleteOrUpdate;

    public DeleteUserEvent(Object source, String action, String subject, String object, String path ) {
        super(source, action, subject, object, path);
        this.requesterEmail = object;
        this.userToDeleteOrUpdate = subject;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public String getUserToDeleteOrUpdate() {
        return userToDeleteOrUpdate;
    }
}
