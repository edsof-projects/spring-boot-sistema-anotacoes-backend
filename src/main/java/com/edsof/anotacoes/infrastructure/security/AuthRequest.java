package com.edsof.anotacoes.infrastructure.security;

public class AuthRequest {

    private String email;
    private String senha;

    // Construtor vazio (necessário para o Spring fazer o binding)
    public AuthRequest() {}

    // Getters e Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
