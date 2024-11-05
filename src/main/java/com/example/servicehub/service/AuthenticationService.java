package com.example.servicehub.service;

import com.example.servicehub.config.AppUser;
import com.example.servicehub.model.dto.LoginResponseDto;
import com.example.servicehub.model.dto.LoginRequestDto;
import com.example.servicehub.model.dto.RegisterRequestDto;
import com.example.servicehub.model.entity.Token;
import com.example.servicehub.model.entity.User;
import com.example.servicehub.model.enumeration.TokenType;
import com.example.servicehub.repository.TokenRepository;
import com.example.servicehub.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class AuthenticationService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public AuthenticationService(JwtService jwtService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, UserRepository userRepository, TokenRepository tokenRepository) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }


    public LoginResponseDto register(RegisterRequestDto registerRequestDto) {

        User user = new User();
        user.setEmail(registerRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        user.setName(registerRequestDto.getName());
        user.setPhoneNumber(registerRequestDto.getPhoneNumber());
        user.setRegisterAt(Timestamp.from(Instant.now()));

        User savedUser = userRepository.save(user);

        AppUser appUser = new AppUser(savedUser);

        String jwtToken = jwtService.generateToken(appUser);

        saveUserToken(savedUser, jwtToken);

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setJwtToken(jwtToken);

        return loginResponseDto;
    }

    private void saveUserToken(User user, String jwtToken) {

        Token token = new Token();
        token.setUser(user);
        token.setToken(jwtToken);
        token.setTokenType(TokenType.BEARER);
        token.setRevoked(false);
        token.setExpired(false);

        tokenRepository.save(token);
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()));

        User user = userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow();

        AppUser appUser = new AppUser(user);

        String jwtToken = jwtService.generateToken(appUser);

        revokeAllUserTokens(user);

        saveUserToken(user, jwtToken);

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setJwtToken(jwtToken);

        return loginResponseDto;
    }

    private void revokeAllUserTokens(User user) {

        List<Token> validUserTokens = tokenRepository.findAllByUserIdAndExpiredFalseAndRevokedFalse(user.getId());

        if (validUserTokens.isEmpty()) {
            return;
        }

        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }

}
