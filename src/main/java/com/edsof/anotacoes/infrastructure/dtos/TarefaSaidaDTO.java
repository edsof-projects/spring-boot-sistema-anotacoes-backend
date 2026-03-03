package com.edsof.anotacoes.infrastructure.dtos;

import com.edsof.anotacoes.infrastructure.enums.StatusTarefa;

import java.time.LocalDate;

public record TarefaSaidaDTO(
        Long id,
        String titulo,
        String historico,
        Long usuarioId,
        String nomeUsuario,
        LocalDate data_fechamento,
        LocalDate data_prazo,
        StatusTarefa status
) {}
