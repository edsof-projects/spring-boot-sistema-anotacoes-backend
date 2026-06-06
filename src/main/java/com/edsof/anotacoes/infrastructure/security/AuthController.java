package com.edsof.anotacoes.infrastructure.security;

import com.edsof.anotacoes.business.service.EmailService;
import com.edsof.anotacoes.business.service.UsuarioService;
import com.edsof.anotacoes.infrastructure.entity.PasswordResetToken;
import com.edsof.anotacoes.infrastructure.entity.Usuario;
import com.edsof.anotacoes.infrastructure.repository.PasswordResetTokenRepository;
import com.edsof.anotacoes.infrastructure.repository.UsuarioRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getSenha()
                    )
            );

            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));


            // pega a role do usuário autenticado
            String role = authentication.getAuthorities()
                    .stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_USER");

            // gera token com role correta
            String token = jwtUtil.generateToken(request.getEmail(), role, usuario.getId());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "role", role,
                    "photo", usuario.getUrlfoto() != null ? usuario.getUrlfoto() : "default-photo.png",
                    "id", usuario.getId()
            ));

        } catch (AuthenticationException e) {
            // log interno para auditoria
            System.out.println("Tentativa de login inválida: " + request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credenciais inválidas"));
        }
    }

    // ENVIANDO EMAIL COM TOKEN PARA ALTERAÇÃO DE SENHA PELO LINK -> ESQUECI A SENHA
    @PostMapping("/enviar-email")
    public ResponseEntity<?> resetarSenha(@RequestBody Map<String, String> body) {

        String email = body.get("email");

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {

            try {
                Usuario usuario = usuarioOpt.get();

                String token = UUID.randomUUID().toString();

                PasswordResetToken resetToken = new PasswordResetToken();
                resetToken.setToken(token);
                resetToken.setUsuario(usuario);
                resetToken.setExpiration(LocalDateTime.now().plusMinutes(30));

                tokenRepository.save(resetToken);

                //Gera e envia o email com uri para alteração da senha
                emailService.enviarEmailAlteracao(email, usuario.getNome(), token);

            } catch (Exception e) {
                e.printStackTrace(); // veja o erro real no console
            }
        }

        return ResponseEntity.ok(
                Map.of("message",
                        "E-mail cadastrado, acesse sua caixa de entrada para instruções.")
        );
    }

    // SALVAR ALTERAÇÃO DE SENHA SOLICITADA POR E-MAIL ATRAVES DO FORM RESETAR SENHA
    @PutMapping("/salvar-senha")
    public ResponseEntity<?> salvarSenha(@RequestBody Map<String, String> body) throws MessagingException {
        String token = body.get("token");
        String novaSenha = body.get("novaSenha");
        String email = body.get("email");

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty() || tokenOpt.get().getExpiration().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token inválido ou expirado"));
        }

        Usuario usuario = tokenOpt.get().getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        tokenRepository.delete(tokenOpt.get());

        return ResponseEntity.ok(Map.of("message", "Senha redefinida com sucesso!"));
    }
}
