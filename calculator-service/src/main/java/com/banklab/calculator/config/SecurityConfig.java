package com.banklab.calculator.config;

import com.banklab.calculator.dto.ErrorResponse;
import com.banklab.calculator.security.JwtAuthFilter;
import com.banklab.calculator.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.List;

/**
 * Lab L10: "Only authenticated users can simulate maturity estimates" and
 * "Access restricted to CUSTOMER role". Unauthorised users receive 403.
 *
 * Rules when calculator.security.enabled=true:
 *   - Swagger UI, health check and POST /api/auth/login : open to everyone
 *   - /api/fd/** and /api/calculator/**                  : CUSTOMER role only
 *   - no/invalid token -> 401, wrong role -> 403
 * When calculator.security.enabled=false, everything is open (like Phase A/B).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final boolean securityEnabled;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public SecurityConfig(@Value("${calculator.security.enabled:true}") boolean securityEnabled,
                          JwtService jwtService,
                          ObjectMapper objectMapper) {
        this.securityEnabled = securityEnabled;
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, e) ->
                                writeError(response, 401, "UNAUTHORIZED",
                                        "Please log in first. A valid login token is required."))
                        .accessDeniedHandler((request, response, e) ->
                                writeError(response, 403, "FORBIDDEN",
                                        "Access denied. The FD Calculator is only for customers (CUSTOMER role).")));

        if (!securityEnabled) {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        } else {
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers("/actuator/health", "/actuator/health/**", "/error").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                    .requestMatchers("/api/fd/**", "/api/calculator/**").hasRole("CUSTOMER")
                    .anyRequest().authenticated());
        }
        return http.build();
    }

    private void writeError(HttpServletResponse response, int status, String code, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), new ErrorResponse(code, message, List.of()));
    }
}
