package com.edsof.anotacoes.controller;

import com.edsof.anotacoes.business.service.UsuarioService;
import com.edsof.anotacoes.infrastructure.dtos.UsuarioEntradaDTO;
import com.edsof.anotacoes.infrastructure.dtos.UsuarioLoginDTO;
import com.edsof.anotacoes.infrastructure.dtos.UsuarioSaidaDTO;
import com.edsof.anotacoes.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    @GetMapping
    public List<UsuarioSaidaDTO>listarTodos(){
        return usuarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public UsuarioSaidaDTO buscarPorId(@PathVariable Long id){
        return usuarioService.buscarPorId(id);
    }

    @PostMapping("/login")
    public String login(@RequestBody UsuarioLoginDTO usuarioLoginDTO){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuarioLoginDTO.email(),
                        usuarioLoginDTO.senha())
        );
        return "Bearer" + jwtUtil.generateToken(authentication.getName());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsuarioSaidaDTO> cadastrar(@ModelAttribute UsuarioEntradaDTO dto) throws IOException {
        UsuarioSaidaDTO usuarioCadastrado = usuarioService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCadastrado);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsuarioSaidaDTO> editar(
            @PathVariable Long id,
            @ModelAttribute UsuarioEntradaDTO dto
    ) throws IOException {
        return ResponseEntity.ok(usuarioService.editar(dto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>excluir(@PathVariable Long id){
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

}
