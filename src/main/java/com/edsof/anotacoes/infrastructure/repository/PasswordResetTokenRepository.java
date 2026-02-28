package com.edsof.anotacoes.infrastructure.repository;

import com.edsof.anotacoes.infrastructure.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
        Optional<PasswordResetToken> findByToken(String token);
}
