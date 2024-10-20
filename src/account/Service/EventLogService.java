package account.Service;

import account.Entity.AccountUser;
import account.Entity.EventLog;

import java.util.List;

/**
 * Service interface for managing event logs in the application.
 *
 * <p>This interface defines the methods for creating and retrieving event log entries,
 * which capture important actions taken by users and the system for auditing and monitoring purposes.</p>
 */
public interface EventLogService {

    void saveLogEvent(EventLog eventLog);

    List<EventLog> returnAllEventLog();

    int updateFailedAttempt(String email);

    int returnMaxAttempt();
}
