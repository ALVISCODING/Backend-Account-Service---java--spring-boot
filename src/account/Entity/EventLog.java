package account.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a log entry for user actions and system events.
 *
 * <p>This entity captures details such as the action performed,
 * the user involved, the target of the action, and the API endpoint
 * where the event occurred, aiding in auditing and monitoring.</p>
 */
@Entity
public class EventLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    LocalDate date;
    @NotNull
    String action;
    @NotNull
    String subject;
    @NotNull
    String object;
    @NotNull
    String path;

    public EventLog() {
    }

    public EventLog(String action, String subject, String object, String path) {
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
        this.date=LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventLog eventLog = (EventLog) o;
        return Objects.equals(id, eventLog.id) && Objects.equals(date, eventLog.date) && Objects.equals(action, eventLog.action) && Objects.equals(subject, eventLog.subject) && Objects.equals(object, eventLog.object) && Objects.equals(path, eventLog.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, action, subject, object, path);
    }


}
