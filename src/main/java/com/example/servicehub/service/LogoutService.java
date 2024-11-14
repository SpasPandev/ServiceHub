package com.example.servicehub.service;

import com.example.servicehub.model.entity.Token;
import com.example.servicehub.repository.TokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    public LogoutService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            clearJwtCookie(response);

            return;
        }

        jwt = authHeader.substring(7);

        Token storedToken = tokenRepository.findByToken(jwt).orElse(null);

        if (storedToken != null) {

            storedToken.setExpired(true);
            storedToken.setRevoked(true);

            tokenRepository.save(storedToken);
        }

        SecurityContextHolder.clearContext();
    }

    private static void clearJwtCookie(HttpServletResponse response) {

        Cookie cookie = new Cookie("jwtToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
