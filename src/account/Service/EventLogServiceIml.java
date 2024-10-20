package account.Service;

import account.Entity.AccountUser;
import account.Entity.EventLog;
import account.Entity.Role;
import account.Exception.AdministratorCantNotBeLockException;
import account.Repository.EventLogRepository;
import account.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
/**
 * Implementation of the EventLogService interface for managing event logs.
 *
 * <p>This service interacts with the EventLogRepository to perform CRUD operations
 * on event log entries. It provides methods to save, retrieve, and delete event logs,
 * enabling auditing and monitoring of user actions and system events.</p>
 */
@Service
public class EventLogServiceIml implements EventLogService {

    private final EventLogRepository eventLogRepository; //connect to EventLogRepository
    private final UserRepository userRepository; // connect to the UserRepository
    private final int maxFailAttempt = 5; // max login fail attempt is 5



    @Autowired
    public EventLogServiceIml(EventLogRepository eventLogRepository, UserRepository userRepository) {
        this.eventLogRepository = eventLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void saveLogEvent(EventLog eventLog) {
        eventLogRepository.save(eventLog);
    }

    @Override
    public List<EventLog> returnAllEventLog() {
        // Retrieve all EventLog entries and sort them by id
        return eventLogRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(EventLog::getId)) // Use Comparator to sort based on the id field
                .collect(Collectors.toList());
    }

    /**
     * update the fail attempt, this method will be called when log in fails happen
     * only if user exists
     * @param email
     * @return
     */
    @Override
    public int updateFailedAttempt(String email) {
        //check if the user exists
        Optional<AccountUser> user = userRepository.findByEmailIgnoreCase(email);

        if (user.isPresent()) {
            AccountUser accountUser = user.get();
            int currentFailAttempt = accountUser.getFailAttempt();

            // Increment the fail attempt count
            currentFailAttempt++;

            //to check if the user has the role of admin
            Boolean isAdministrator = accountUser.getRoles().stream()
                    .anyMatch(role -> role.getNameOfTheRole().equals("ROLE_ADMINISTRATOR"));

            // Check if the user has exceeded the max attempts
            if (currentFailAttempt > maxFailAttempt && isAdministrator) {
                //this should only work if the lock is manual
                throw new AdministratorCantNotBeLockException("updateFailedAttempt");
            } else if (currentFailAttempt > maxFailAttempt && !isAdministrator){
                accountUser.setLocked(true);
               // might lock is enable
            }
            // Update the user's fail attempts
            accountUser.setFailAttempt(currentFailAttempt);
            //save the updated user
            userRepository.save(accountUser);

            // Return the current fail attempt count (after increment)
            return currentFailAttempt;
        } else {
            // If user not found, return 0
            return 0;
        }
    }

    @Override
    public int returnMaxAttempt() {
        return maxFailAttempt;
    }


}
