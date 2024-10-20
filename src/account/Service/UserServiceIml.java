package account.Service;

import account.Entity.Payment;
import account.Entity.Role;
import account.Exception.*;
import account.Repository.PaymentRepository;
import account.Repository.RoleRepository;
import account.Repository.UserRepository;

import account.DTO.PaymentDTO;
import account.DTO.PaymentRecordDTO;
import account.DTO.RoleRequestDTO;
import account.DTO.UserDTO;
import account.Entity.AccountUser;
import account.Erum.RoleGroup;
import account.Security.UserDetailsIml;
import account.customComparator.RoleComparator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceIml implements UserService, UserDetailsService {
    @Autowired
    private final account.Repository.UserRepository userRepository;
    private final  PasswordEncoder passwordEncoder;
    private final account.Repository.PaymentRepository paymentRepository;
    private final account.Repository.RoleRepository roleRepository;
    private final int maxFailAttempt = 5;
    private final Map<String, Integer> attemptsCache = new HashMap<>();
    private  Set<String> breachedPasswords;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM-yyyy"); // for record

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy"); //to convert string to yearMonth



    // It is recommended to place autowired on the constructor
    //This tells Spring to use this constructor
    // when creating an instance of UserService and to inject an instance of UserRepository.
    @Autowired
    public UserServiceIml(UserRepository userRepository, PasswordEncoder passwordEncoder, PaymentRepository paymentRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.paymentRepository = paymentRepository;
        this.roleRepository = roleRepository;
        this.breachedPasswords = new HashSet<>(Arrays.asList(
                "PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
                "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
                "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"
        ));

    }


    /**
     *
     * @param accountUser
     * @return the userDTO
     */
    @Override
    public UserDTO returnUserDto(AccountUser accountUser) {
        // From now, we don't use optional and will use it later on
        //Optional<AccountUser> existingUser = userRepository.findByEmailIgnoreCase(accountUser.getEmail());

        //convert the role object to the name only
        List<String> roles = accountUser.getRoles().stream()
                .map(role -> role.getNameOfTheRole())
                .sorted() // Sorts in natural order (ascending)
                .toList(); // Collects the result into a List

        //retunr the userdto to avoid circular references and invite loop
        return new UserDTO(accountUser.getId(), accountUser.getName(),
                accountUser.getLastname(), accountUser.getEmail(),
                roles);


    }

    // this method will ignore the case
    @Override
    public Optional<AccountUser> findAccountUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    @Override
    public Optional<AccountUser> findAccountUserByEmailAndPassword(String email, String password) {
        Optional<AccountUser> user= userRepository.findByEmailIgnoreCaseAndPassword(email,password);

        return user;
    }


    /**
     * Checks if the given password is not in the set of breached passwords.
     *
     * @param password the new password to check
     * @return true if the password is not in the set of breached passwords, false otherwise
     */
    @Override
    public boolean checkPasswordIsNotBreached(String password) {
        return !breachedPasswords.contains(password);
    }

    // delete this method
    @Override
    public boolean compareNewAndOldPassword(String newPassword) {

        return false;
    }

    //update the password
    @Override
    public void updatePassword(AccountUser accountUser, String newPassword) {

        accountUser.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(accountUser);
    }

    @Override
    public boolean checkIfPaymentAlreadyExist(String userName, String period) {
        return false;
    }

    @Override
    public boolean checkIfPaymentAlreadyExist(AccountUser userName, YearMonth period) {
        return paymentRepository.existsByEmployeeAndPeriod(userName,period);
    }

    @Override
    public void savePayment(List<Payment> payment) {
        paymentRepository.saveAll(payment);
    }

    //check the import list if there is a user that is not withing the database
    @Override
    public boolean checkIfEmailAlreadyExist(List<Payment> importPayment) {
        for(Payment payment : importPayment){
            if(!userRepository.existsByEmail(payment.getEmail())){
                return false;
            }
        }
       return true;
    }

    //save the user fist we need to accept the dto
    @Override
    @Transactional // must be able to roll back if encounter an error
    public boolean savePaymentList(List<PaymentDTO> importPayment) {
        //first map the dto object, then check if the user exists or not
        // then check if the payment period has been used or not for the same employee
        //if all are good return true
        List<Payment> Payments = importPayment.stream()
                .map(dto ->{
                    //in the dto, which is a payment entity
                   AccountUser employee  = userRepository.findByEmailIgnoreCase(dto.getEmail())
                           .orElseThrow(() -> new UserNotFoundException("/api/acct/payments"));

                   //if the employee data already exist for the given period, throw EX
                   if(paymentRepository.existsByEmployeeAndPeriod(employee,YearMonth.parse(dto.getPeriod(),formatter))){
                        throw new IllegalArgumentException("Salary already allocated for this period");
                   }

                   //creating new Payment entities to be saved.
                   return new Payment(dto.getEmail(),YearMonth.parse(dto.getPeriod(),formatter),dto.getSalary(),employee);
                })
                .toList();
        // Save all the Payment entities to the repository
        paymentRepository.saveAll(Payments); // Use saveAll to persist the list of Payment entities
        return true;
    }

    /**
     * update the payment
     * first check if the user exists and the salary data of that period is not empty
     * @param paymentToBeUpdated
     */
    @Override
    @Transactional
    public void updatePayment(PaymentDTO paymentToBeUpdated) {
        //check if the user exists from the database
        AccountUser employee = userRepository.findByEmailIgnoreCase(paymentToBeUpdated.getEmail())
                .orElseThrow(() -> new UserNotFoundException("/api/acct/payments"));

        // check if the payment record of the input employee's salary/period exist
        Payment existingPayment = paymentRepository.findByEmployeeAndPeriod(employee, YearMonth.parse(paymentToBeUpdated.getPeriod(),formatter))
                .orElseThrow(() -> new IllegalArgumentException("No payment found for the specified period"));

        //update the payment
        existingPayment.setSalary(paymentToBeUpdated.getSalary());

        //save from the database
        paymentRepository.save(existingPayment);
    }




    //return all the salary data
    @Override
    public List<PaymentRecordDTO> getEmployeePaymentsByUser(AccountUser user) {
        List<Payment> paymentsRecord = paymentRepository.findByEmployee(user);

        //as we need to return the payment dto record not payment
        // we need to convert payment to paymentsRecord using stream
        return paymentsRecord.stream()
                .map(Payment -> new PaymentRecordDTO(
                        user.getName(),
                        user.getLastname(),
                        Payment.getPeriod(), // using formatter
                        Payment.getSalary()))
                .sorted((p1, p2) -> {
                    YearMonth ym1 = YearMonth.parse(p1.getPeriod(), DATE_FORMATTER);
                    YearMonth ym2 = YearMonth.parse(p2.getPeriod(), DATE_FORMATTER);
                    return ym2.compareTo(ym1); // Reverse order (most recent first)
                })
                .collect(Collectors.toList());
    }

    /**
     *
     * @param user the provided account user
     * @param period the specified payment period
     * @return  the paymentRecordDTO if exist else return an empty list
     */
    @Override
    public List<PaymentRecordDTO> getEmployeePaymentsByUserAndPeriod(AccountUser user, YearMonth period) {
        Optional<Payment> paymentsRecord = paymentRepository.findByEmployeeAndPeriod(user,period);

        if(paymentsRecord.isPresent()){
            return paymentsRecord.stream()
                    .map(Payment -> new PaymentRecordDTO(
                            user.getName(),
                            user.getLastname(),
                            Payment.getPeriod(),// using formatter
                            Payment.getSalary()
                    ))
                    .sorted((p1, p2) -> {
                        YearMonth ym1 = YearMonth.parse(p1.getPeriod(), DATE_FORMATTER);
                        YearMonth ym2 = YearMonth.parse(p2.getPeriod(), DATE_FORMATTER);
                        return ym2.compareTo(ym1); // Reverse order (most recent first)
                    })
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }



    @Override
    public boolean isAccoutUserRepositoryEmpty() {
        return userRepository.count() ==0;
    }

    /**
     *
     * @param roleRequestDTO
     * @return
     */
    @Override
    public UserDTO changeUserRole(RoleRequestDTO roleRequestDTO) {
        String endpoint = "changeUserRole";

        //first check if the user exist
        //elsethrow is a build in method of the optional class
        AccountUser user = userRepository.findByEmailIgnoreCase(roleRequestDTO.getName())
                .orElseThrow(()-> new UserNotFoundException(""));

        //now find the role
        Role role = roleRepository.findByNameOfTheRoleIgnoreCase("ROLE_" +roleRequestDTO.getRole())
                .orElseThrow(() -> new CustomEntityNotFoundException("Role not found!","ChangeUserRole"));

        Set<Role> userRoles= user.getRoles();
        //check the operation
        if(roleRequestDTO.getOperation().equalsIgnoreCase("GRANT")){
            if(isIncompatibleRole(userRoles,role)){
                throw new RoleGroupConflictException("changeUserRole");
            }
            userRoles.add(role);

        } else if(roleRequestDTO.getOperation().equalsIgnoreCase("REMOVE")){
//            //if the user only has one role, it cant be removed
//            if (userRoles.size() == 1) {
//                throw new RoleRemoveException("The user must have at least one role!");
//
//                //admin cant delete by himself
//            } else if(role.getNameOfTheRole().equalsIgnoreCase("ROLE_Administrator")){
//                throw new RoleRemoveException("Can't remove ADMINISTRATOR role!");
//
//                //the user doesn't has the role to be deleted
//            } else if (!userRoles.contains(role)) {
//                throw new RoleRemoveException("The user does not have the role!");
//            } else {
//                //remove the role
//                userRoles.remove(role);
//            }
            // Check if the role to remove is the Administrator role
            if (role.getNameOfTheRole().equalsIgnoreCase("ROLE_ADMINISTRATOR")) {
                throw new RoleRemoveException("Can't remove ADMINISTRATOR role!",endpoint);
            }

            // Check if the user doesn't have the role to be deleted
            if (!userRoles.contains(role)) {
                throw new RoleRemoveException("The user does not have the role!",endpoint);
            }

            // Check if the user has only one role
            if (userRoles.size() == 1) {
                throw new RoleRemoveException("The user must have at least one role!",endpoint);
            }



            // Remove the role if all checks are passed
            userRoles.remove(role);

        }
        userRepository.save(user);

        return mapAccountusertoUserDto(user);
    }

    /**
     *
     * @return all the user record and map into the UserDTO
     */
    @Override
        public List<UserDTO> returnAllAccountUsers() {
            return userRepository.findAll().stream()
                    .map(user -> new UserDTO(
                            user.getId(),
                            user.getName(),
                            user.getLastname(),
                            user.getEmail(),
                            // Extract the nameOfTheRole from each Role object and join them into a single string
                            user.getRoles().stream()
                                    .map(Role::getNameOfTheRole)
                                    .sorted(new RoleComparator()) // sort the role
                            .collect(Collectors.toList()) // Extract role name

                    ))
                    .sorted(Comparator.comparing(UserDTO::getId)) // Sort users by id in ascending order
                    .collect(Collectors.toList());
        }

    /**
     *
      * @param email the user email
     * @return  true if the user is deleted from the database
     */
    @Override
    public boolean deleteUser(String email) {

        // check if user exit
        AccountUser user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email, HttpStatus.NOT_FOUND.value(), "deleteUser"));

        //admin can;t be delete
        if(user.getRoles().stream().anyMatch((role)->role.getRoleGroup()== RoleGroup.ADMINISTRATIVE)){
            throw new RoleGroupConflictException("Can't remove ADMINISTRATOR role!","deleteUser");
        }

        //delete user
        userRepository.delete(user);
        return true;
    }

    /**
     *
     * @return the size of the database never null
     */
    @Override
    public long theSizeOfTheDataBase() {
        return userRepository.count();
    }

    /**
     * if the fail attemt is > 5 lock the account
     * else failAttempt++
     * @param accountUser
     * @return
     */
    @Override
    public int updateFailedAttempt(AccountUser accountUser) {
        AccountUser user = userRepository.findByEmailIgnoreCase(accountUser.getName())
                .orElseThrow(()-> new UserNotFoundException(""));

        int currentFailAttempt = user.getFailAttempt();

        if(currentFailAttempt>maxFailAttempt){
            user.setLocked(true);
        } else {
            user.setFailAttempt(currentFailAttempt++);
        }

        userRepository.save(user);

        return currentFailAttempt;
    }

    /**
     * Unlock the user
     * @param email
     */
    @Override
    public void unlockAccount(String email) {
        AccountUser user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(()-> new UserNotFoundException(""));

        user.setLocked(false);
        user.setFailAttempt(0);// reset the login attempt
        userRepository.save(user);

    }

    //return true if an account is locked
    @Override
    public boolean lockAccount(String email) {
        // Find user by email
        Optional<AccountUser> userOpt = userRepository.findByEmailIgnoreCase(email);

        // Handle case where user is not found
        AccountUser user = userOpt.orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));

        // Check if the user has the role "ROLE_ADMINISTRATOR" and prevent locking
        boolean isAdministrator = user.getRoles().stream()
                .anyMatch(role -> "ROLE_ADMINISTRATOR".equalsIgnoreCase(role.getNameOfTheRole()));

        //if the user has a role of admin, throw exception
        if (isAdministrator) {
            throw new AdministratorCantNotBeLockException("Administrator account cannot be locked");
        }

        // Lock the account and save changes
        user.setLocked(true);
        userRepository.save(user);

        return true;
    }

    @Override
    public int userFailLogInAttempt(String email) {
        Optional<AccountUser> userOpt = userRepository.findByEmailIgnoreCase(email);

        if(userOpt.isPresent()){
            return userOpt.get().getFailAttempt();
        }
        return 0;
    }

    //check if the role is the same type user can;t have business and admin role at the same time
    //if the role incompatible return true
    private boolean isIncompatibleRole(Set<Role> userRoles, Role newRole){

        //check if the user is already had any role in admin group
        boolean isAdmin =  userRoles.stream().anyMatch(role -> role.getRoleGroup().equals(RoleGroup.ADMINISTRATIVE));

       //check if the user is already had any role in the business group
        boolean isBusiness = userRoles.stream().anyMatch(role -> role.getRoleGroup().equals(RoleGroup.BUSINESS));

        //check if the new role is part of the same group
        //if not return  false
        if (isAdmin && newRole.getRoleGroup() == RoleGroup.BUSINESS) {
            return true;
        } else return isBusiness && newRole.getRoleGroup() == RoleGroup.ADMINISTRATIVE;

    }






    /**
     * it is to save the user from database
     * for the first user to register it will become administrator by default
     * @param accountUser
     * @return a true if successful
     */
    public boolean saveUser(AccountUser accountUser){
        //using optional to check if the role and users are exist in the database
        Optional<AccountUser> inputUser = userRepository.findByEmailIgnoreCase(accountUser.getEmail());

        Optional<Role> adminRoleOpt = roleRepository.findByNameOfTheRole("ROLE_ADMINISTRATOR");
        Optional<Role> userRoleOpt = roleRepository.findByNameOfTheRole("ROLE_USER");

        // If the user already exists or if either of the roles (admin or user) is not found, return false
        if (inputUser.isPresent() || adminRoleOpt.isEmpty() || userRoleOpt.isEmpty()) {
            return false;
        }

        Role adminRole = adminRoleOpt.get();
        Role userRole = userRoleOpt.get();

        // Create a set of roles and add the admin role
        Set<Role> Roles = new HashSet<>();

        //if this is the first user to register, it will have a role of administrator as default

        if(isAccoutUserRepositoryEmpty()){

            //ad the user to the role as it is a many-to-many relationship
            Roles.add(adminRole);

            AccountUser newAccountUser = new AccountUser(
                    accountUser.getName(),
                    accountUser.getLastname(),
                    accountUser.getEmail().toLowerCase(),
                    passwordEncoder.encode(accountUser.getPassword()),
                    Roles
            );
            // Save the new user to the database
            userRepository.save(newAccountUser);
        } else {
            //if it is not a first user the reset will be assign of the user role by default
            //ad the user to the role as it is a many-to-many relationship
            Roles.add(userRole);

            AccountUser newAccountUser = new AccountUser(
                    accountUser.getName(),
                    accountUser.getLastname(),
                    accountUser.getEmail().toLowerCase(),
                    passwordEncoder.encode(accountUser.getPassword()),
                    Roles
            );

            userRepository.save(newAccountUser);
        }

        return true;
    }





    /**
     * Loads a UserDetails object based on the provided username (email).
     *
     * @param username The email address used to find the user.
     * @return UserDetailsIml An instance of UserDetailsIml wrapping the found AccountUser.
     * @throws UsernameNotFoundException If no user is found with the provided email.
     *
     * The method returns UserDetailsIml instead of UserDetails to provide Spring Security
     * with additional user-specific details encapsulated in UserDetailsIml, such as the
     * AccountUser entity's password and email. This allows Spring Security to perform
     * authentication and authorization based on the application's domain model.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String email = username.toLowerCase();
        Optional<AccountUser> accountUser = findAccountUserByEmail(email);

        if (accountUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        // Return an instance of UserDetailsIml as it implements userdetails interface **
        return new UserDetailsIml(accountUser.get());
    }


    /**
     * This is a private method to map the AccountUser entity to userDto
     * @param user
     * @return a userdto
     */
    private UserDTO mapAccountusertoUserDto(AccountUser user){

        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(Role::getNameOfTheRole)
                        .sorted(new RoleComparator())
                        .collect(Collectors.toList())
        );
    }

    }



