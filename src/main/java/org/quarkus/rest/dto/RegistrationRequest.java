package org.quarkus.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "User registration request")
public class RegistrationRequest {

    @Schema(description = "User email address", example = "joni@gmail.com", required = true)
    @Email(message = "Email format is invalid")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "User first name", example = "User", required = true)
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(description = "User last name", example = "joni", required = true)
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Schema(description = "User password", example = "Password123!@", required = true)
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @NotBlank(message = "Password is required")
    private String password;

    public RegistrationRequest() {}

    public RegistrationRequest(String email, String firstName, String lastName, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}