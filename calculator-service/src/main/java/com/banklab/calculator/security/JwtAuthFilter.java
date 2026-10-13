package com.banklab.calculator.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Runs before every request. If the request carries
 *     Authorization: Bearer <token>
 * and the token is genuine, it tells Spring Security who the user is and
 * what role they have. If there is no token, or it is fake/expired, the user
 * stays "anonymous" and protected addresses answer 401.
 *
 * NOTE: this class is deliberately NOT a @Component. It is created inside
 * SecurityConfig so it runs exactly once, inside the security chain.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();
            try {
                JwtService.TokenUser user = jwtService.parse(token);
                if (user.username() != null && user.role() != null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user.username(), null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + user.role())));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException | IllegalArgumentException e) {
                // Fake, damaged or expired token: leave the user anonymous.
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }
}
