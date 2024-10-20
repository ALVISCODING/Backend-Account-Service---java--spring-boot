package account.Repository;

import account.Entity.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Repository interface for accessing and managing EventLog entities.
 *
 * <p>This interface extends JpaRepository, providing CRUD operations and
 * custom query methods for EventLog objects. It allows interaction with
 * the database to persist and retrieve event log records.</p>
 */
@Repository
public interface EventLogRepository extends JpaRepository<EventLog,Long> {

}
