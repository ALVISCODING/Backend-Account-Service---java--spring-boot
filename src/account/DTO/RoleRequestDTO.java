package account.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoleRequestDTO {

    @JsonProperty("user")
    private String name;

    @JsonProperty("role")
    private String role;

    private String operation;

    public RoleRequestDTO() {
    }

    public RoleRequestDTO(String name, String role, String operation) {
        this.name = name;
        this.role = role;
        this.operation = operation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
