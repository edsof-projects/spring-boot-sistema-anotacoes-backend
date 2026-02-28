package com.edsof.anotacoes.business.service;

import com.edsof.anotacoes.infrastructure.dtos.AnotacaoEntradaDTO;
import com.edsof.anotacoes.infrastructure.dtos.AnotacaoSaidaDTO;
import com.edsof.anotacoes.infrastructure.entity.Anotacao;
import com.edsof.anotacoes.infrastructure.entity.Usuario;
import com.edsof.anotacoes.infrastructure.repository.AnotacaoRepository;
import com.edsof.anotacoes.infrastructure.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AnotacaoService {

    public  final AnotacaoRepository anotacaoRepository;
    private final UsuarioRepository  usuarioRepository;

    // Entity → DTO de SAÍDA
    private AnotacaoSaidaDTO toSaidaDTO(Anotacao anotacao) {
        return new AnotacaoSaidaDTO(
                anotacao.getId(),
                anotacao.getTitulo(),
                anotacao.getDescricao(),
                anotacao.getUsuario().getId(),
                anotacao.getUsuario().getNome()
        );
    }

    // DTO de ENTRADA → Entity
    private Anotacao toEntity(AnotacaoEntradaDTO dto, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Id do usuário não encontrado"));

        Anotacao anotacao = new Anotacao();
        anotacao.setTitulo(dto.titulo());
        anotacao.setDescricao(dto.descricao());
        anotacao.setUsuario(usuario);

        return anotacao;
    }

    private Long getUsuarioLogadoId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;

        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else {
            email = principal.toString();
        }

        return usuarioRepository.findIdByEmail(email);
    }

    public List<AnotacaoSaidaDTO> listarAnotacoesUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return anotacaoRepository.listarTodasAnotacoes();
        } else {
            Long usuarioId = getUsuarioLogadoId();
            return anotacaoRepository.listarAnotacoesPorUsuario(usuarioId);
        }
    }

    public AnotacaoSaidaDTO buscarPorId(Long id) {
        Anotacao anotacao = anotacaoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Anotação não encontrada"));
        return toSaidaDTO(anotacao);
    }

    // CREATE
    public Anotacao cadastrar(AnotacaoEntradaDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Anotacao anotacao = new Anotacao();
        anotacao.setTitulo(dto.titulo());
        anotacao.setDescricao(dto.descricao());
        anotacao.setUsuario(usuario);
        anotacao.setDatacad(LocalDate.now());

        return anotacaoRepository.save(anotacao);
    }


    // UPDATE (sem senha)
    public AnotacaoSaidaDTO editar(AnotacaoSaidaDTO dto, Long id) {

        Anotacao anotacao = anotacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anotação não encontrada"));

        anotacao.setTitulo(dto.titulo());
        anotacao.setDescricao(dto.descricao());

        return toSaidaDTO(anotacaoRepository.save(anotacao));
    }

    public void excluir(Long id) {
        anotacaoRepository.deleteById(id);
    }

}
