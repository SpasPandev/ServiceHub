package com.example.servicehub.service;

import com.example.servicehub.repository.TokenRepository;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }


    public void deleteAllInvalidTokens() {

        tokenRepository.deleteAllByExpiredIsTrueAndRevokedIsTrue();
    }
}
