package account.Erum;


/**
 * enum is a singleton and fix set of value
 *if you want to associate a value with each enum constant, you need to have a constructor.
 */
public enum RoleGroup {
    ADMINISTRATIVE("Administrative"),
    BUSINESS("Business");

    // Private field to store the associated value
    private final String value;

    // Constructor to initialize the value field
    RoleGroup(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}