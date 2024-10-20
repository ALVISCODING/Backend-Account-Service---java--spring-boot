package account.audit.events;

/**
 * Represents an event triggered when a user password has been changed.
 *
 * <p>This event logs details such as the user, the resource they attempted to access, and the API endpoint involved.
 * Useful for auditing unauthorized access attempts and detecting potential security risks.</p>
 *
 * <p>Extends {@link UserActionEvent} to inherit common event properties.</p>
 */
public class ChangePasswordEvent extends UserActionEvent {

    private final String email;

    public ChangePasswordEvent(Object source, String email, String path) {
        super(source, "CHANGE_PASSWORD", email, email, path);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

}
