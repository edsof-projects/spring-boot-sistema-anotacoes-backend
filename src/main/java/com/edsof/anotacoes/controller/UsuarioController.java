package com.edsof.anotacoes.controller;

import com.edsof.anotacoes.business.service.UsuarioService;
import com.edsof.anotacoes.infrastructure.dtos.UsuarioEntradaDTO;
import com.edsof.anotacoes.infrastructure.dtos.UsuarioSaidaDTO;
import com.edsof.anotacoes.infrastructure.entity.Usuario;
import com.edsof.anotacoes.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // LISTAR TODOS
    @GetMapping
    public List<UsuarioSaidaDTO> listarTodos() {
        return usuarioService.listarTodos();
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public UsuarioSaidaDTO buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    // BUSCAR POR EMAIL
    @GetMapping("/email")
    public ResponseEntity<Usuario> buscaUsuarioPorEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorEmail(email));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioSaidaDTO> getUsuarioLogado(Authentication auth) {
        // Pega o usuário logado pelo email do token
        Usuario usuarioLogado      = usuarioService.buscarPorEmail(auth.getName());
        UsuarioSaidaDTO usuarioDTO = usuarioService.converterParaDTO(usuarioLogado);
        return ResponseEntity.ok(usuarioDTO);
    }

    // CADASTRAR
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsuarioSaidaDTO> cadastrar(@ModelAttribute UsuarioEntradaDTO dto) throws IOException {
        UsuarioSaidaDTO usuarioCadastrado = usuarioService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCadastrado);
    }

    // EDITAR POR ID
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsuarioSaidaDTO> editar(
            @PathVariable Long id,
            @ModelAttribute UsuarioEntradaDTO dto
    ) throws IOException {
        return ResponseEntity.ok(usuarioService.editar(dto, id));
    }

    // DELETAR POR ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // DELETAR POR EMAIL
    @DeleteMapping("/email/{email}")
    public ResponseEntity<Void> deletaUsuarioPorEmail(@PathVariable String email) {
        usuarioService.deletaUsuarioPorEmail(email);
        return ResponseEntity.ok().build();
    }

}