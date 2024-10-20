package account.DTO;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Data Transfer Object (DTO) for handling payment-related data.
 * This class is used to encapsulate the necessary information for processing payments,
 * such as the employee's email, payment period, and salary amount.
 */
public class PaymentRecordDTO {

    private String name;
    private String lastname;
    private String period;
    private String salary;

    public PaymentRecordDTO() {
    }

    public PaymentRecordDTO(String name, String lastname, YearMonth period, Long salary) {
        this.name = name;
        this.lastname = lastname;
        this.period = period.format(DateTimeFormatter.ofPattern("MMMM-yyyy"));
        this.salary = formatSalary(salary);
    }

    private String formatSalary(Long salary) {
        // Assuming salary is in cents
        long dollars = salary / 100;
        long cents = salary % 100;
        return dollars + " dollar(s) " + cents + " cent(s)";
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

}
