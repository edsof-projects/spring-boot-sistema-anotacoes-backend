package com.edsof.anotacoes.infrastructure.dtos;

import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;

public record TarefaEntradaDTO(
        String titulo,
        String historico,
        LocalDate data_fechamento,
        @FutureOrPresent(message = "O prazo deve ser hoje ou uma data futura")
        LocalDate data_prazo,
        Long usuarioId

) {}
