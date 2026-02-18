package com.edsof.anotacoes.infrastructure.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NivelAcessoEntradaDTO(

        @JsonProperty("tipo")
        String tipo
){}
