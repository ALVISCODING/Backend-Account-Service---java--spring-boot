package account.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


import java.time.YearMonth;

/**
 * Data Transfer Object (DTO) for accepting payment-related data.
 * This class is used to capture payment information, such as the employee's email,
 * the payment period, and the salary amount, with validation annotations.
 */
public class PaymentDTO {

    @NotNull
    @Pattern(regexp = ".*@acme\\.com$", message = "Email must end with @acme.com")
    @JsonProperty("employee")  // This maps the JSON field 'employee' to the 'email' property
    private String email;

    // When using YearMonth in a DTO, it's important to ensure that it is correctly
// serialized to and deserialized from the <MM-yyyy> format. This can be achieved
// using the @JsonFormat annotation provided by Jackson, the default JSON library
// used by Spring Boot. The annotation specifies the format to be used during
// the conversion between the YearMonth object and its JSON representation.
    @NotNull(message = "Period cannot be null")
    //@JsonFormat(pattern = "MM-yyyy", shape = JsonFormat.Shape.STRING)
    @Pattern(regexp = "^(0[1-9]|1[0-2])-[0-9]{4}$", message = "Invalid period format, expected MM-yyyy")
    private String period;

    @NotNull(message = "Salary cannot be null")
    @Min(value = 0,message = "Salary must be non-negative")
    private Long salary;

    public PaymentDTO() {
    }

    public PaymentDTO(String email, String period, Long salary) {
        this.email = email;
        this.period = period;
        this.salary = salary;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public YearMonth getPeriod() {
//        return period;
//    }
//
//    public void setPeriod(YearMonth period) {
//        this.period = period;
//    }


    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }
}
