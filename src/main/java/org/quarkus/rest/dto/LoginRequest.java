package org.quarkus.rest.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User Login request")
public class LoginRequest {

    @Schema(description = "User email address", example = "joni@gmail.com", required = true)
    @Email(message = "Email format is invalid")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "User password", example = "Password123!@", required = true)
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @NotBlank(message = "Password is required")
    private String password;

    public LoginRequest() {}

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}