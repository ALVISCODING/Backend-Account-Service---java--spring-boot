package account.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.YearMonth;
/**
 * Represents a payment record for an employee.
 *
 * <p>This entity is linked to the user and includes fields for
 * the employee's email, the payment period, and the salary.
 * A unique constraint on the combination of "email" and "period"
 * ensures that each employee has only one payment record per period,
 * preventing duplicate entries. Violations of this constraint
 * will trigger a DataIntegrityViolationException.</p>
 *
 * <p>The payment period is stored as a {@link YearMonth}
 * to efficiently handle monthly payments.</p>
 */
@Entity
@Table(name = "payments", uniqueConstraints = {@UniqueConstraint(columnNames = {"email", "period"})})
// The unique constraint on the "email" and "period" fields
// ensures each employee has only one payment record per period.
// The unique constraint on "email" and "period" prevents duplicate entries.
// Violations will trigger a DataIntegrityViolationException,
// which can be handled to maintain data integrity.
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Pattern(regexp = ".*@acme\\.com$", message = "Email must end with @acme.com")
    private String email;

    // Changed from String to YearMonth to handle periods more efficiently
    @NotNull
    @JsonFormat(pattern = "MM-yyyy")
    private YearMonth period;


    @NotNull
    @PositiveOrZero
    private Long salary;

    //using ManyToOne
    //ensures that every Payment is linked to a valid User
    //The @JsonFormat(pattern = "MM-yyyy") ensures that when serialized/deserialized as JSON,
    // it follows the MM-yyyy format.
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AccountUser employee;

    // Default constructor
    public Payment() {
    }

    public Payment(String email, YearMonth period, Long salary, AccountUser employee) {
        this.email = email;
        this.period = period;
        this.salary = salary;
        this.employee = employee;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public YearMonth getPeriod() {
        return period;
    }

    public void setPeriod(YearMonth period) {
        this.period = period;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }

    public AccountUser getEmployee() {
        return employee;
    }

    public void setEmployee(AccountUser employee) {
        this.employee = employee;
    }
}




