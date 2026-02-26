package com.edsof.anotacoes.controller;

import com.edsof.anotacoes.business.service.AnotacaoService;
import com.edsof.anotacoes.infrastructure.dtos.AnotacaoEntradaDTO;
import com.edsof.anotacoes.infrastructure.dtos.AnotacaoSaidaDTO;
import com.edsof.anotacoes.infrastructure.entity.Anotacao;
import com.edsof.anotacoes.infrastructure.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/anotacoes")
public class AnotacaoController {

    private final AnotacaoService anotacaoService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public List<AnotacaoSaidaDTO> listarTodas() {
        return anotacaoService.listarTodos();
    }

    @GetMapping("/{id}")
    public AnotacaoSaidaDTO buscarPorId(@PathVariable Long id) {
        return anotacaoService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Anotacao> cadastrar(@RequestBody AnotacaoEntradaDTO dto,
                                              HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long usuarioId = jwtUtil.extractAllClaims(token).get("id", Long.class);

        AnotacaoEntradaDTO dtoComUsuario = new AnotacaoEntradaDTO(
                dto.titulo(),
                dto.descricao(),
                usuarioId
        );

        Anotacao anotacaoSalva = anotacaoService.cadastrar(dtoComUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(anotacaoSalva);
    }

    @PutMapping("/{id}")
    public AnotacaoSaidaDTO editar(@RequestBody AnotacaoSaidaDTO dto, @PathVariable Long id){
        return anotacaoService.editar(dto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        anotacaoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
