package com.edsof.anotacoes.infrastructure.dtos;

public record UsuarioSaidaDTO(
        Long id,
        String nome,
        String email,
        String acesso,
        Long nivelAcessoId,
        String foto
){}
