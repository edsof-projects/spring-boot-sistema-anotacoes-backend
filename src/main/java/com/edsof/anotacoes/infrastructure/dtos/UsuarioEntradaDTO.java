package com.edsof.anotacoes.infrastructure.dtos;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class UsuarioEntradaDTO {

    private String nome;
    private String email;
    private String senha;
    private Long nivelAcessoId;
    private MultipartFile foto;
}

