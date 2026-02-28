package com.edsof.anotacoes.infrastructure.repository;

import com.edsof.anotacoes.infrastructure.dtos.AnotacaoSaidaDTO;
import com.edsof.anotacoes.infrastructure.entity.Anotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnotacaoRepository extends JpaRepository<Anotacao, Long> {

    @Query("""
    SELECT new com.edsof.anotacoes.infrastructure.dtos.AnotacaoSaidaDTO(
        a.id,
        a.titulo,
        a.descricao,
        a.usuario.id,
        a.usuario.nome
    )
    FROM Anotacao a
    ORDER BY a.titulo
""")

    List<AnotacaoSaidaDTO> listarTodasAnotacoes();


    @Query("""
    SELECT new com.edsof.anotacoes.infrastructure.dtos.AnotacaoSaidaDTO(
        a.id,
        a.titulo,
        a.descricao,
        a.usuario.id,
        a.usuario.nome
    )
    FROM Anotacao a
    WHERE a.usuario.id = :usuarioId
    ORDER BY a.titulo
""")

    List<AnotacaoSaidaDTO> listarAnotacoesPorUsuario(@Param("usuarioId") Long usuarioId);

}
