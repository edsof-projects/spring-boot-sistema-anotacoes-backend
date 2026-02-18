package com.edsof.anotacoes.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tblusuarios")

public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "email", length = 100, unique = true)
    private String email;

    @Column(name = "senha", length = 100, nullable = false)
    private String senha;

    @ManyToOne
    @JoinColumn(name = "nivelacessoid", referencedColumnName = "id", nullable = false)
    private NivelAcesso nivelAcesso;

    @Column(name = "datacad", nullable = false)
    private LocalDate datacad;

    @Column(name = "urlfoto", length = 255, nullable = true)
    private String urlfoto;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> nivelAcesso.getTipo());
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }
}

