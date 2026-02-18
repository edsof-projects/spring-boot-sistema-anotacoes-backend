package com.edsof.anotacoes.infrastructure.dtos;

public record AnotacaoSaidaDTO(
        Long   id,
        String titulo,
        String descricao,
        Long   usuarioId,
        String nomeUsuario
) {}
