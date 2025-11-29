package org.quarkus.rest.controller;

import org.quarkus.rest.dto.*;
import org.quarkus.rest.entity.User;
import org.quarkus.rest.repository.UserRepository;
import org.quarkus.rest.service.TokenService;
import org.quarkus.rest.service.PasswordService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import java.time.LocalDateTime;

@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityScheme(
    securitySchemeName = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class MembershipResource {

    @Inject
    UserRepository userRepository;

    @Inject
    TokenService tokenService;

    @Inject
    PasswordService passwordService;

    @Inject
    JsonWebToken jwt;

  
    @POST
    @Path("/registration")
    @Transactional
    @Operation(
        summary = "User Registration",
        description = "Register a new user in the system"
    )
    @APIResponses(value = {
        @APIResponse(
            responseCode = "201",
            description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @APIResponse(
            responseCode = "400",
            description = "Bad request - Email already registered or invalid input"
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public Response registration(
        @RequestBody(
            description = "User registration information",
            required = true,
            content = @Content(schema = @Schema(implementation = RegistrationRequest.class))
        )
        @Valid RegistrationRequest request) {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Email already registered"))
                        .build();
            }

            // Create new user
            User user = new User();
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPassword(passwordService.encrypt(request.getPassword()));
            user.setProfileImage("https://yoururlapi.com/profile.jpeg"); // Default profile image

            userRepository.persist(user);

            ApiResponse<Object> response = ApiResponse.success("User registered successfully");
            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Registration failed: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/login")
    @Operation(
        summary = "User Login",
        description = "Authenticate user and return JWT token"
    )
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @APIResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid email or password"
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public Response login(
        @RequestBody(
            description = "User login credentials",
            required = true,
            content = @Content(schema = @Schema(implementation = LoginRequest.class))
        )
        @Valid LoginRequest request) {
        try {
            System.out.println("DEBUG: Login attempt for email: " + request.getEmail());

            // Find user by email
            User user = userRepository.findByEmail(request.getEmail())
                    .orElse(null);

            if (user == null) {
                System.out.println("DEBUG: User not found");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error("Invalid email or password"))
                        .build();
            }

            System.out.println("DEBUG: User found, verifying password");
            // Verify password using bcrypt
            if (!passwordService.verify(request.getPassword(), user.getPassword())) {
                System.out.println("DEBUG: Password verification failed");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error("Invalid email or password"))
                        .build();
            }

            System.out.println("DEBUG: Password verified, generating JWT token");
            // Generate JWT token
            String token = tokenService.generateToken(user.getEmail());
            System.out.println("DEBUG: JWT token generated successfully");

            LoginResponse loginResponse = new LoginResponse(token);

            return Response.ok()
                    .entity(ApiResponse.success("login successful", loginResponse))
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Login failed: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/profile")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Get User Profile",
        description = "Get current user profile information (requires JWT token)"
    )
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Profile retrieved successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @APIResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing token"
        ),
        @APIResponse(
            responseCode = "404",
            description = "User not found"
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public Response getProfile() {
        try {
            // Get email from JWT token
            String email = jwt.getClaim("email");
            if (email == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error("Invalid token"))
                        .build();
            }

            // Find user by email
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponse.error("User not found"))
                        .build();
            }

            // Create profile response
            ProfileResponse profileResponse = new ProfileResponse(
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getProfileImage() != null ? user.getProfileImage() : "https://yoururlapi.com/profile.jpeg"
            );

            return Response.ok()
                    .entity(ApiResponse.success("successful", profileResponse))
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Failed to get profile: " + e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/profile/update")
    @Transactional
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Update User Profile",
        description = "Update current user profile information (requires JWT token)"
    )
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Profile updated successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @APIResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing token"
        ),
        @APIResponse(
            responseCode = "404",
            description = "User not found"
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public Response updateProfile(
        @RequestBody(
            description = "Profile update information",
            required = true,
            content = @Content(schema = @Schema(implementation = ProfileUpdateRequest.class))
        )
        @Valid ProfileUpdateRequest request) {
        try {
            // Get email from JWT token
            String email = jwt.getClaim("email");
            if (email == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error("Invalid token"))
                        .build();
            }

            // Find user by email
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponse.error("User not found"))
                        .build();
            }

            // Update user profile
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setUpdatedAt(LocalDateTime.now());

            // Create profile response
            ProfileResponse profileResponse = new ProfileResponse(
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getProfileImage() != null ? user.getProfileImage() : "https://yoururlapi.com/profile.jpeg"
            );

            return Response.ok()
                    .entity(ApiResponse.success("Profile updated successfully", profileResponse))
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Failed to update profile: " + e.getMessage()))
                    .build();
        }
    }
}