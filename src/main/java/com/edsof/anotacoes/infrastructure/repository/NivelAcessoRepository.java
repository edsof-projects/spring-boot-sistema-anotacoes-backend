package com.edsof.anotacoes.infrastructure.repository;

import com.edsof.anotacoes.infrastructure.dtos.NivelAcessoSaidaDTO;
import com.edsof.anotacoes.infrastructure.entity.NivelAcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NivelAcessoRepository extends JpaRepository<NivelAcesso, Long> {
    boolean existsByTipo(String tipo);
    Optional<NivelAcesso>findByTipo(String tipo);

    @Query("""
        SELECT new com.edsof.anotacoes.infrastructure.dtos.NivelAcessoSaidaDTO(
            n.id,
            n.tipo
        )
        FROM NivelAcesso n
        ORDER BY UPPER(TRIM(n.tipo))
    """)
    List<NivelAcessoSaidaDTO> listarAcessos();
}
