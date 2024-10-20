package account.DTO;

import java.time.LocalDate;


/**
 * Data Transfer Object (DTO) for responseEntity.
 * This class holds the necessary fields when error an error or exceptions for
 * responseEntity in a json object,
 */
public class ErrorResponseBody {

    private LocalDate date;
    private int status;
    private String error;
    private String path;

    /**
     * No-arguments constructor that initializes the date field to the current date.
     * This constructor is necessary for frameworks like Spring and Hibernate that use reflection to instantiate the class.
     */
    public ErrorResponseBody() {
        this.date = LocalDate.now();
    }

    public ErrorResponseBody(Integer status, String error, String path) {
        this.date = LocalDate.now();
        this.status = status;
        this.error = error;
        this.path = path;
    }


    public LocalDate getDate() {
        return date;
    }

    /**
     * no need at the moment local date will be set
     * when the object is created
     * @return
     */
//    public void setDate(LocalDate date) {
//        this.date = date;
//    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
