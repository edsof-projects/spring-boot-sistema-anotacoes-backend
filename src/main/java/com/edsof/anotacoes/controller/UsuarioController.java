package com.edsof.anotacoes.controller;

import com.edsof.anotacoes.business.service.EmailService;
import com.edsof.anotacoes.business.service.UsuarioService;
import com.edsof.anotacoes.infrastructure.dtos.UsuarioEntradaDTO;
import com.edsof.anotacoes.infrastructure.dtos.UsuarioSaidaDTO;
import com.edsof.anotacoes.infrastructure.entity.Usuario;
import com.edsof.anotacoes.infrastructure.repository.PasswordResetTokenRepository;
import com.edsof.anotacoes.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

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

    //OBTER A FOTO DO USUARIO
    @GetMapping("/usuarios/{id}/foto")
    public ResponseEntity<String> getFotoUsuario(@PathVariable Long id) {
        String urlFoto = usuarioService.buscarUrlFoto(id);
        if (urlFoto != null) {
            return ResponseEntity.ok(urlFoto);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioSaidaDTO> getUsuarioLogado(Authentication auth) {
        // Pega o usuário logado pelo email do token
        Usuario usuarioLogado      = usuarioService.buscarPorEmail(auth.getName());
        UsuarioSaidaDTO usuarioDTO = usuarioService.converterParaDTO(usuarioLogado);
        return ResponseEntity.ok(usuarioDTO);
    }

    // CADASTRAR NOVO USUARIO PELO ADMIN
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsuarioSaidaDTO> cadastrar(

            @RequestPart("usuario")
            UsuarioEntradaDTO dto,

            @RequestPart(value = "foto", required = false)
            MultipartFile foto

    ) throws IOException {

        dto.setFoto(foto);

        UsuarioSaidaDTO usuarioCadastrado =
                usuarioService.cadastrar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioCadastrado);
    }

    // REGISTRAR USUARIO NAO CADASTRADO A PARTIR DA TELA DE LOGIN
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registrar(
            @RequestPart("usuario") UsuarioEntradaDTO dto,
            @RequestPart(value = "foto", required = false) MultipartFile foto
    ) throws IOException {
        dto.setFoto(foto);

        try {
            UsuarioSaidaDTO usuarioCadastrado = usuarioService.cadastrar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCadastrado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // EDITAR POR ID
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsuarioSaidaDTO> editar(

            @PathVariable Long id,

            @RequestPart("usuario")
            UsuarioEntradaDTO dto,

            @RequestPart(value = "foto", required = false)
            MultipartFile foto

    ) throws IOException {

        dto.setFoto(foto);

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