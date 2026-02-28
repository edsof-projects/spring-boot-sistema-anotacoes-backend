package com.edsof.anotacoes.infrastructure.security;

import com.edsof.anotacoes.business.service.EmailService;
import com.edsof.anotacoes.infrastructure.entity.PasswordResetToken;
import com.edsof.anotacoes.infrastructure.entity.Usuario;
import com.edsof.anotacoes.infrastructure.repository.PasswordResetTokenRepository;
import com.edsof.anotacoes.infrastructure.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            return ResponseEntity.status(403).body("Credenciais inválidas");
        }
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<?> recuperarSenha(@RequestBody Map<String, String> body) {

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

                emailService.enviarEmailRecuperacao(email, token);

            } catch (Exception e) {
                e.printStackTrace(); // veja o erro real no console
            }
        }

        return ResponseEntity.ok(
                Map.of("message",
                        "Se e-mail cadastrado, acesse sua caixa de entrada para instruções.")
        );
    }

    @PostMapping("/resetar-senha")
    public ResponseEntity<?> resetarSenha(@RequestBody Map<String, String> body) {

        String token = body.get("token");
        String novaSenha = body.get("novaSenha");

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Token inválido."));
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.getExpiration().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Token expirado."));
        }

        Usuario usuario = resetToken.getUsuario();

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        // remove token após uso
        tokenRepository.delete(resetToken);

        return ResponseEntity.ok(
                Map.of("message", "Senha redefinida com sucesso.")
        );
    }

}