package com.example.servicehub.config;

import com.example.servicehub.model.entity.User;
import com.example.servicehub.repository.TokenRepository;
import com.example.servicehub.service.JwtService;
import com.example.servicehub.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final TokenRepository tokenRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService, UserService userService, TokenRepository tokenRepository) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Retrieve JWT from Authorization header or cookies
        String jwt = extractJwt(request);

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract email and validate token
        String email = jwtService.extractEmail(jwt);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            authenticateUser(request, response, jwt, email);
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwt(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        // Extract JWT from Authorization header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // Retrieve JWT from the cookies if not found in the Authorization header
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        // JWT not found
        return null;
    }

    private void authenticateUser(HttpServletRequest request, HttpServletResponse response, String jwt, String email) throws IOException {

        Optional<UserDetails> userDetailsOpt = Optional.ofNullable(userDetailsService.loadUserByUsername(email));

        // Token is invalid or user doesn't exist, skip authentication
        if (userDetailsOpt.isEmpty() || !jwtService.isTokenValid(jwt, userDetailsOpt.get())) {
            return;
        }

        boolean isTokenValid = tokenRepository.findByToken(jwt)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);

        // If token is expired or revoked
        if (!isTokenValid) {
            return;
        }

        User user = userService.findUserByEmail(email);

        if (user.isDeleted()) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("User is deleted and cannot perform any actions!");
            return;
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetailsOpt.get(), null, userDetailsOpt.get().getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}