package com.edsof.anotacoes.business.service;

import com.edsof.anotacoes.infrastructure.dtos.TarefaEntradaDTO;
import com.edsof.anotacoes.infrastructure.dtos.TarefaSaidaDTO;
import com.edsof.anotacoes.infrastructure.entity.Tarefa;
import com.edsof.anotacoes.infrastructure.entity.Usuario;
import com.edsof.anotacoes.infrastructure.enums.StatusTarefa;
import com.edsof.anotacoes.infrastructure.repository.TarefaRepository;
import com.edsof.anotacoes.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TarefaService {
    
    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;

    // Entity → DTO de SAÍDA
    private TarefaSaidaDTO toSaidaDTO(Tarefa tarefa) {
        return new TarefaSaidaDTO(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getHistorico(),
                tarefa.getUsuario().getId(),
                tarefa.getUsuario().getNome(),
                tarefa.getData_fechamento(),
                tarefa.getStatus()
        );
    }

    // DTO de ENTRADA → Entity
    private Tarefa toEntity(TarefaEntradaDTO dto) {

        if (dto.usuarioId() == null) {
            throw new RuntimeException("usuarioId é obrigatório");
        }

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Id do usuário não encontrado"));

        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(dto.titulo());
        tarefa.setHistorico(dto.historico());
        tarefa.setUsuario(usuario);
        tarefa.setData_abertura(LocalDate.now());

        return tarefa;
    }

    public List<TarefaSaidaDTO> listarTarefasUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            // Admin vê todas
            return tarefaRepository.listarTodasTarefas();
        } else {
            // Usuário comum vê só as próprias
            Long usuarioId = getUsuarioLogadoId();
            return tarefaRepository.listarTarefasPorUsuario(usuarioId);
        }
    }

    private Long getUsuarioLogadoId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;

        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername(); // username = email
        } else {
            email = principal.toString();
        }

        return usuarioRepository.findIdByEmail(email);
    }


    public TarefaSaidaDTO buscarPorId(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        return toSaidaDTO(tarefa);
    }

    // CREATE
    public Tarefa cadastrar(TarefaEntradaDTO dto) {

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(dto.titulo());
        tarefa.setHistorico(dto.historico());
        tarefa.setUsuario(usuario);
        tarefa.setData_abertura(LocalDate.now());
        tarefa.setStatus(StatusTarefa.ABERTA);

        return tarefaRepository.save(tarefa);

    }

    private String novaLinhaHistorico(String mensagem) {
        String data = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return data + " : " + mensagem;
    }

    // UPDATE
    public TarefaSaidaDTO editar(TarefaEntradaDTO dto, Long id) {

        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        tarefa.setTitulo(dto.titulo());
        tarefa.setHistorico(dto.historico());

        if (dto.data_fechamento() != null) {
            tarefa.setData_fechamento(dto.data_fechamento());
            tarefa.setStatus(StatusTarefa.FECHADA);
        }

        return toSaidaDTO(tarefaRepository.save(tarefa));
    }

    public TarefaSaidaDTO fechar(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        LocalDate hoje = LocalDate.now();
        String dataFormatada = hoje.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        tarefa.setData_fechamento(LocalDate.now());
        tarefa.setStatus(StatusTarefa.FECHADA);

        // 🔹 Atualiza histórico
        String historicoAtual = tarefa.getHistorico();

        String novaLinha = dataFormatada + " : Tarefa fechada.";

        if (historicoAtual == null || historicoAtual.isBlank()) {
            tarefa.setHistorico(novaLinha);
        } else {
            tarefa.setHistorico(historicoAtual + "\n" + novaLinha);
        }

        return toSaidaDTO(tarefaRepository.save(tarefa));
    }


    public void excluir(Long id) {
        tarefaRepository.deleteById(id);
    }

}
