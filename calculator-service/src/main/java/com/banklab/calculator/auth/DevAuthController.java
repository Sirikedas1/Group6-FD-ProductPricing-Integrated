package com.banklab.calculator.auth;

import com.banklab.calculator.exception.CalculationException;
import com.banklab.calculator.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

/**
 * DEV LOGIN STAND-IN - same request/response shape as Group 10's
 * POST /api/auth/login (cust_auth_swagger_api.yaml), so the Angular login page
 * works unchanged when you switch to their real service (Step 16).
 *
 * Exists only when calculator.dev-auth.enabled=true.
 */
@RestController
@ConditionalOnProperty(name = "calculator.dev-auth.enabled", havingValue = "true")
@Tag(name = "Dev login stand-in", description = "Temporary login until Group 10's auth service is running")
public class DevAuthController {

    public record LoginRequest(
            @NotBlank(message = "Username is required") String username,
            @NotBlank(message = "Password is required") String password) {
    }

    public record LoginResponse(String token, String tokenType, long expiresIn, String role, String username) {
    }

    public record CurrentUser(String username, String role, List<String> categories) {
    }

    private final DevUserStore users;
    private final JwtService jwtService;

    public DevAuthController(DevUserStore users, JwtService jwtService) {
        this.users = users;
        this.jwtService = jwtService;
    }

    @PostMapping("/api/auth/login")
    @Operation(summary = "Log in with a demo user and receive a JWT token")
    public LoginResponse login(@Valid @RequestBody LoginRequest body) {
        DevUserStore.DevUser user = users.authenticate(body.username(), body.password())
                .orElseThrow(() -> CalculationException.unauthorized(
                        "INVALID_CREDENTIALS", "Invalid username or password"));
        String token = jwtService.createToken(user.username(), user.role());
        return new LoginResponse(token, "Bearer", jwtService.getExpirySeconds(), user.role(), user.username());
    }

    @GetMapping("/api/auth/me")
    @Operation(summary = "Who am I? (needs a token)")
    public CurrentUser me(Principal principal) {
        if (principal == null) {
            throw CalculationException.unauthorized("UNAUTHORIZED", "Please log in first.");
        }
        DevUserStore.DevUser user = users.find(principal.getName())
                .orElseThrow(() -> CalculationException.notFound("USER_NOT_FOUND", "Unknown user"));
        return new CurrentUser(user.username(), user.role(), user.categories());
    }
}
