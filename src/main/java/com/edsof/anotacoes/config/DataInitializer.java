package com.edsof.anotacoes.config;

import com.edsof.anotacoes.infrastructure.entity.NivelAcesso;
import com.edsof.anotacoes.infrastructure.entity.Usuario;
import com.edsof.anotacoes.infrastructure.repository.NivelAcessoRepository;
import com.edsof.anotacoes.infrastructure.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            NivelAcessoRepository nivelRepo,
            UsuarioRepository     usuarioRepo,
            PasswordEncoder       passwordEncoder) {

        return args -> {

            // 1️⃣ Criar níveis se não existirem
            NivelAcesso admin = nivelRepo.findByTipo("ADMIN")
                    .orElseGet(() -> {
                        NivelAcesso n = new NivelAcesso();
                        n.setTipo("ADMIN");
                        return nivelRepo.save(n);
                    });

            NivelAcesso user = nivelRepo.findByTipo("USER")
                    .orElseGet(() -> {
                        NivelAcesso n = new NivelAcesso();
                        n.setTipo("USER");
                        return nivelRepo.save(n);
                    });

            // 2️⃣ Criar usuário ADMIN se não existir
            if (usuarioRepo.findByEmail("edsouzzas@gmail.com").isEmpty()) {

                Usuario usuario = new Usuario();
                usuario.setNome("Edi Aquino de souza");
                usuario.setEmail("edsouzzas@gmail.com");
                usuario.setSenha(passwordEncoder.encode("eas1708")); // 🔐 senha criptografada
                usuario.setUrlfoto("29d2961c-7744-4fbd-9749-762d01e72cbd_Edi.png");
                usuario.setDatacad(LocalDate.now());
                usuario.setNivelAcesso(admin);

                usuarioRepo.save(usuario);
            }
        };
    }
}
