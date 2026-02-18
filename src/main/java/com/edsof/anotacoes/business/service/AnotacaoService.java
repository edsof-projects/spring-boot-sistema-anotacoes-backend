package com.edsof.anotacoes.business.service;

import com.edsof.anotacoes.infrastructure.dtos.AnotacaoEntradaDTO;
import com.edsof.anotacoes.infrastructure.dtos.AnotacaoSaidaDTO;
import com.edsof.anotacoes.infrastructure.entity.Anotacao;
import com.edsof.anotacoes.infrastructure.entity.Usuario;
import com.edsof.anotacoes.infrastructure.repository.AnotacaoRepository;
import com.edsof.anotacoes.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnotacaoService {

    private final AnotacaoRepository anotacaoRepository;
    private final UsuarioRepository usuarioRepository;

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
    private Anotacao toEntity(AnotacaoEntradaDTO dto) {

        if (dto.usuarioId() == null) {
            throw new RuntimeException("usuarioId é obrigatório");
        }

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Id do usuário não encontrado"));

        Anotacao anotacao = new Anotacao();
        anotacao.setTitulo(dto.titulo());
        anotacao.setDescricao(dto.descricao());
        anotacao.setUsuario(usuario);
        anotacao.setDatacad(LocalDate.now());

        return anotacao;
    }

    public List<AnotacaoSaidaDTO> listarTodos() {
        return anotacaoRepository.listarAnotacoes();
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
