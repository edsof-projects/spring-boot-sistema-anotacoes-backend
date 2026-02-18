package com.edsof.anotacoes.controller;

import com.edsof.anotacoes.business.service.AnotacaoService;
import com.edsof.anotacoes.infrastructure.dtos.AnotacaoEntradaDTO;
import com.edsof.anotacoes.infrastructure.dtos.AnotacaoSaidaDTO;
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

    @GetMapping
    public List<AnotacaoSaidaDTO> listarTodas() {
        return anotacaoService.listarTodos();
    }

    @GetMapping("/{id}")
    public AnotacaoSaidaDTO buscarPorId(@PathVariable Long id) {
        return anotacaoService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Void> cadastrar(@RequestBody AnotacaoEntradaDTO dto) {
        anotacaoService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
