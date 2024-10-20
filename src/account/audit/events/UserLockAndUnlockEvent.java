package account.audit.events;

/**
 * Represents an event triggered when a new AccountUser entity is unlocked and unlock.
 *
 * <p>This event logs details such as the user, the resource they attempted to access, and the API endpoint involved.
 * Useful for auditing unauthorized access attempts and detecting potential security risks.</p>
 *
 * <p>Extends {@link UserActionEvent} to inherit common event properties.</p>
 */
public class UserLockAndUnlockEvent  extends  UserActionEvent{
    private final String requestUser;
    private final String receiveUser;
    private final String operation;


    public UserLockAndUnlockEvent(Object source, String action, String subject, String object, String path) {
        super(source, action, subject, object, path);
        this.requestUser = subject;
        this.operation = action;
        this.receiveUser = object;
    }

    public String getRequestUser() {
        return requestUser;
    }

    public String getReceiveUser() {
        return receiveUser;
    }

    public String getOperation() {
        return operation;
    }
}
