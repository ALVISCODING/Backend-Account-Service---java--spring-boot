package account.Service;

import account.DTO.PaymentDTO;
import account.DTO.PaymentRecordDTO;
import account.DTO.RoleRequestDTO;
import account.DTO.UserDTO;
import account.Entity.AccountUser;
import account.Entity.Payment;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
/**
 * Service interface for managing user-related operations.
 * Defines methods for user management tasks such as
 * creating, updating, deleting, and retrieving users.
 */
public interface UserService {

    UserDTO returnUserDto(AccountUser accountUser);

    Optional<AccountUser> findAccountUserByEmail(String email);

    Optional<AccountUser> findAccountUserByEmailAndPassword(String email, String password);

    boolean checkPasswordIsNotBreached(String password);

    boolean compareNewAndOldPassword(String newPassword);

    void updatePassword(AccountUser accountUser, String Password);

    boolean checkIfPaymentAlreadyExist(String userName, String period);

    boolean checkIfPaymentAlreadyExist(AccountUser userName, YearMonth period);

    void savePayment(List<Payment> payment);

    boolean checkIfEmailAlreadyExist(List<Payment> importPayment);

    boolean savePaymentList(List<PaymentDTO> importPayment);

    void updatePayment(PaymentDTO paymentToBeUpdated);

    List<PaymentRecordDTO> getEmployeePaymentsByUser(AccountUser user);

    List<PaymentRecordDTO> getEmployeePaymentsByUserAndPeriod(AccountUser email, YearMonth period);

    boolean isAccoutUserRepositoryEmpty();

    UserDTO changeUserRole(RoleRequestDTO roleRequestDTO);

    List<UserDTO> returnAllAccountUsers();

    boolean deleteUser(String email);

    long theSizeOfTheDataBase();

    int updateFailedAttempt(AccountUser accountUser);

    void unlockAccount(String email);

    boolean lockAccount(String email);

    int userFailLogInAttempt(String email);

}
