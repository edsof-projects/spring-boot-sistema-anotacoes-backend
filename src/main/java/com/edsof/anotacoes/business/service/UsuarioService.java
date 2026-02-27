package com.edsof.anotacoes.business.service;

import com.edsof.anotacoes.infrastructure.dtos.UsuarioEntradaDTO;
import com.edsof.anotacoes.infrastructure.dtos.UsuarioSaidaDTO;
import com.edsof.anotacoes.infrastructure.entity.NivelAcesso;
import com.edsof.anotacoes.infrastructure.entity.Usuario;
import com.edsof.anotacoes.infrastructure.exceptions.ConflictException;
import com.edsof.anotacoes.infrastructure.exceptions.ResourceNotFoundException;
import com.edsof.anotacoes.infrastructure.repository.NivelAcessoRepository;
import com.edsof.anotacoes.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository     usuarioRepository;
    private final NivelAcessoRepository nivelAcessoRepository;
    private final PasswordEncoder       passwordEncoder;

    private static final String FOTO_PADRAO = "default-photo.png";

    @Value("${app.upload.dir}")
    private String uploadDir;

    // Entity → DTO de SAÍDA
    private UsuarioSaidaDTO toSaidaDTO(Usuario usuario) {
        String nomeFoto =
                (usuario.getUrlfoto() == null || usuario.getUrlfoto().isBlank())
                        ? FOTO_PADRAO
                        : usuario.getUrlfoto();

        String urlFoto = "http://localhost:8080/uploads/usuarios/" + nomeFoto;

        System.out.println(usuario.getNivelAcesso());

        return new UsuarioSaidaDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getNivelAcesso().getTipo(),
                usuario.getNivelAcesso().getId(),
                urlFoto
        );
    }

    public Usuario salvarFoto(Long id, MultipartFile file) throws IOException {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path uploadPath = Paths.get(uploadDir + "/usuarios");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        usuario.setUrlfoto(fileName);

        return usuarioRepository.save(usuario);
    }

    // DTO de ENTRADA → Entity
    private Usuario toEntity(UsuarioEntradaDTO dto) {

        if (dto.getNivelAcessoId() == null) {
            throw new RuntimeException("nivelAcessoId é obrigatório");
        }

        NivelAcesso nivelAcesso = nivelAcessoRepository.findById(dto.getNivelAcessoId())
                .orElseThrow(() -> new RuntimeException("Nível de acesso não encontrado"));

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setNivelAcesso(nivelAcesso);
        usuario.setDatacad(LocalDate.now());

        return usuario;
    }

    public List<UsuarioSaidaDTO> listarTodos() {
        return usuarioRepository.listarUsuarios();
    }

    public UsuarioSaidaDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return toSaidaDTO(usuario);
    }

    public Usuario buscarUsuarioPorEmail(String email){
        return usuarioRepository.findByEmail(email).orElseThrow(
                ()-> new ResourceNotFoundException("Email não encontrado : "+email));
    }

    public void deletaUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        usuarioRepository.delete(usuario);
    }

    // CREATE
    public UsuarioSaidaDTO cadastrar(UsuarioEntradaDTO dto) throws IOException {

        // Valida email
        emailExiste(dto.getEmail());

        // Converte DTO → entidade
        Usuario usuario = toEntity(dto);

        // Criptografa a senha antes de salvar
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));

        // Upload de foto
        if (dto.getFoto() != null && !dto.getFoto().isEmpty()) {
            String nomeArquivo = UUID.randomUUID() + "_" + dto.getFoto().getOriginalFilename();

            // Usa uploadDir configurado
            Path caminho = Paths.get(uploadDir + nomeArquivo);
            Files.createDirectories(caminho.getParent());
            Files.copy(dto.getFoto().getInputStream(), caminho, StandardCopyOption.REPLACE_EXISTING);

            // Salva só o nome do arquivo ou caminho relativo
            usuario.setUrlfoto(nomeArquivo);
        } else {
            usuario.setUrlfoto(FOTO_PADRAO);
        }

        // Salva no banco
        Usuario salvo = usuarioRepository.save(usuario);
        System.out.println("Salvo ID: " + salvo.getId());

        return toSaidaDTO(salvo);
    }

    public boolean verificaEmailExistente(String email){
        return usuarioRepository.existsByEmail(email);
    }

    public void emailExiste(String email) {
        if (verificaEmailExistente(email)) {
            throw new ConflictException("Duplicidade: o email " + email + " já está cadastrado");
        }
    }

    // Busca o usuário pelo email (email é usado como username no Spring Security)
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    // Converte a entidade para DTO de saída
    public UsuarioSaidaDTO converterParaDTO(Usuario usuario) {
        return new UsuarioSaidaDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getNivelAcesso().getTipo(),
                usuario.getNivelAcesso().getId(),
                usuario.getUrlfoto()
        );
    }

    // UPDATE (sem senha)
    public UsuarioSaidaDTO editar(UsuarioEntradaDTO dto, Long id) throws IOException {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        NivelAcesso nivelAcesso = nivelAcessoRepository.findById(dto.getNivelAcessoId())
                .orElseThrow(() -> new RuntimeException("Nível de acesso não encontrado"));

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setNivelAcesso(nivelAcesso);

        // 🔥 Se o usuário enviou nova foto
        if (dto.getFoto() != null && !dto.getFoto().isEmpty()) {

            // 1️⃣ Apagar foto antiga (se não for a padrão)
            String fotoAntiga = usuario.getUrlfoto();

            if (fotoAntiga != null &&
                    !fotoAntiga.equals(FOTO_PADRAO)) {

                Path caminhoFotoAntiga = Paths.get(uploadDir + fotoAntiga);

                try {
                    Files.deleteIfExists(caminhoFotoAntiga);
                } catch (IOException e) {
                    System.out.println("Não foi possível excluir a foto antiga: " + fotoAntiga);
                }
            }

            // 2️⃣ Salvar nova foto
            String nomeArquivo = UUID.randomUUID() + "_" + dto.getFoto().getOriginalFilename();
            Path caminhoNovaFoto = Paths.get(uploadDir + nomeArquivo);

            Files.createDirectories(caminhoNovaFoto.getParent());
            Files.copy(dto.getFoto().getInputStream(),
                    caminhoNovaFoto,
                    StandardCopyOption.REPLACE_EXISTING);

            usuario.setUrlfoto(nomeArquivo);
        }

        return toSaidaDTO(usuarioRepository.save(usuario));
    }

    public void excluir(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String foto = usuario.getUrlfoto();

        // 🔥 Se tiver foto e não for a padrão, apagar do disco
        if (foto != null && !foto.equals(FOTO_PADRAO)) {

            Path caminhoFoto = Paths.get(uploadDir + foto);

            try {
                Files.deleteIfExists(caminhoFoto);
                System.out.println("Foto excluída: " + foto);
            } catch (IOException e) {
                System.out.println("Erro ao excluir foto: " + foto);
            }
        }

        // Agora exclui do banco
        usuarioRepository.delete(usuario);
    }
}
