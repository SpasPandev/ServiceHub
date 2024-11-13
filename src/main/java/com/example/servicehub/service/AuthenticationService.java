package com.example.servicehub.service;

import com.example.servicehub.config.AppUser;
import com.example.servicehub.exception.UserDeletedException;
import com.example.servicehub.model.dto.*;
import com.example.servicehub.model.dto.RegisterResponseDto;
import com.example.servicehub.model.entity.Token;
import com.example.servicehub.model.entity.User;
import com.example.servicehub.model.enumeration.TokenType;
import com.example.servicehub.repository.TokenRepository;
import com.example.servicehub.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

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


    public ResponseEntity<?> register(RegisterRequestDto registerRequestDto) {

        if (!registerRequestDto.getPassword().equals(registerRequestDto.getConfirmPassword())) {

            return new ResponseEntity<>(
                    "Password and confirm password must match!",
                    HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(registerRequestDto.getEmail())) {

            return new ResponseEntity<>(
                    "Email address is already in use. Please use a different email address.",
                    HttpStatus.CONFLICT);
        }

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

        RegisterResponseDto registerResponseDto = new RegisterResponseDto();
        registerResponseDto.setJwtToken(jwtToken);

        return new ResponseEntity<>(registerResponseDto, HttpStatus.CREATED);
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

    public ResponseEntity<LoginResponseDto> login(LoginRequestDto loginRequestDto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()));

        User user = userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow();

        if (user.isDeleted()) {

            throw new UserDeletedException("This account has been deleted!");
        }

        AppUser appUser = new AppUser(user);

        String jwtToken = jwtService.generateToken(appUser);

        revokeAllUserTokens(user);

        saveUserToken(user, jwtToken);

//        HERE
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(appUser, null, appUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setJwtToken(jwtToken);

        return ResponseEntity.ok(loginResponseDto);
    }

    public void revokeAllUserTokens(User user) {

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

    public Optional<User> findUserByEmail(String email) {

        return userRepository.findByEmail(email);
    }
}
