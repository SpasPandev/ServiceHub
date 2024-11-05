package com.example.servicehub.repository;

import com.example.servicehub.model.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findAllByUserIdAndExpiredFalseAndRevokedFalse(Long userId);

    Optional<Token> findByToken(String token);
}
