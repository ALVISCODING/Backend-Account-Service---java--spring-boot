package account.audit.events;

import org.springframework.context.ApplicationEvent;


/**
 * This class serves as the base (superclass) for all user action events that will be logged within the application.
 * It captures important details about specific user actions for auditing or security purposes.
 *
 * <p>Each event logs the following information:</p>
 * <ul>
 *     <li><b>Action:</b> The specific action performed, such as CHANGE_PASSWORD, LOCK_USER, etc.</li>
 *     <li><b>Subject:</b> The user who performed the action, indicating the actor involved in the event.</li>
 *     <li><b>Object:</b> The target of the action, which could be another user, a resource, or an entity affected by the action.</li>
 *     <li><b>Path:</b> The API endpoint or resource path where the action occurred, providing context for where the event was triggered.</li>
 * </ul>
 *
 * <p>All subclasses that represent specific user actions (e.g., password changes, user locks, login failures)
 * should extend this class to inherit common properties and provide more specific context for those actions.</p>
 *
 * <p>This event is useful for auditing user activity, detecting suspicious behavior (e.g., brute force attacks),
 * and tracking changes within the system for compliance and security monitoring.</p>
 *
 * @see ApplicationEvent
 */
public class UserActionEvent extends ApplicationEvent {
    private final String action;      // e.g.CHANGE_PASSWORD, LOCK_USER
    private final String subject;     // The user who performed the action
    private final String object;      // The target of the action (e.g., another user, or a resource)
    private final String path;        // The API endpoint where the action occurred


    public UserActionEvent(Object source, String action, String subject, String object, String path) {
        super(source);
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }

    public String getAction() {
        return action;
    }

    public String getSubject() {
        return subject;
    }

    public String getObject() {
        return object;
    }

    public String getPath() {
        return path;
    }



    @Override
    public String toString() {
        return "changePassswordEvent{" +
                "action='" + action + '\'' +
                ", subject='" + subject + '\'' +
                ", object='" + object + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
