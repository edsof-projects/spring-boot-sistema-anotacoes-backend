package com.edsof.anotacoes.infrastructure.repository;

import com.edsof.anotacoes.infrastructure.dtos.TarefaSaidaDTO;
import com.edsof.anotacoes.infrastructure.entity.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    @Query("""
    SELECT new com.edsof.anotacoes.infrastructure.dtos.TarefaSaidaDTO(
        t.id,
        t.titulo,
        t.historico,
        t.usuario.id,
        t.usuario.nome,
        t.data_fechamento,
        t.data_prazo,
        t.status
    )
    FROM Tarefa t    
    ORDER BY t.data_abertura
""")

    List<TarefaSaidaDTO> listarTodasTarefas();


    @Query("""
    SELECT new com.edsof.anotacoes.infrastructure.dtos.TarefaSaidaDTO(
        t.id,
        t.titulo,
        t.historico,
        t.usuario.id,
        t.usuario.nome,
        t.data_fechamento,
        t.data_prazo,
        t.status
    )
    FROM Tarefa t
    WHERE t.status   = 'ABERTA'
    AND t.usuario.id = :usuarioId
    ORDER BY t.data_abertura
""")
     List<TarefaSaidaDTO> listarTarefasPorUsuario(@Param("usuarioId") Long usuarioId);

}
