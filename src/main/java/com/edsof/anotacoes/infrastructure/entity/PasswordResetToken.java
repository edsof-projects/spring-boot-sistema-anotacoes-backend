package com.edsof.anotacoes.infrastructure.entity;

import com.edsof.anotacoes.infrastructure.dtos.UsuarioEntradaDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String token;

    private LocalDateTime expiration;

    @ManyToOne
    private Usuario usuario;

    public PasswordResetToken(String token, UsuarioEntradaDTO dto, LocalDateTime localDateTime) {
    }
}
