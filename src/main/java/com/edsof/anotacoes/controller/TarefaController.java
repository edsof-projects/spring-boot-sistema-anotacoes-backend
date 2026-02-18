package com.edsof.anotacoes.controller;

import com.edsof.anotacoes.business.service.TarefaService;
import com.edsof.anotacoes.infrastructure.dtos.TarefaEntradaDTO;
import com.edsof.anotacoes.infrastructure.dtos.TarefaSaidaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tarefas")
public class TarefaController {

    private final TarefaService tarefaService;

    @GetMapping
    public List<TarefaSaidaDTO> listar() {
        return tarefaService.listarTarefas();
    }

    @GetMapping("/{id}")
    public TarefaSaidaDTO buscarPorId(@PathVariable Long id) {
        return tarefaService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Void> cadastrar(@RequestBody TarefaEntradaDTO dto) {
        tarefaService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarefaSaidaDTO> editar(
            @PathVariable Long id,
            @RequestBody TarefaEntradaDTO dto
    ) {
        return ResponseEntity.ok(tarefaService.editar(dto, id));
    }

    @PutMapping("/{id}/fechar")
    public ResponseEntity<TarefaSaidaDTO> fechar(@PathVariable Long id) {
        return ResponseEntity.ok(tarefaService.fechar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        tarefaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
