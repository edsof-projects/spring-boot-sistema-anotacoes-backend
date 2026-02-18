package com.edsof.anotacoes.infrastructure.dtos;

public record AnotacaoEntradaDTO(
        String titulo,
        String descricao,
        Long usuarioId
) {}
