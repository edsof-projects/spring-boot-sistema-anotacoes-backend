package com.edsof.anotacoes.infrastructure.entity;

import com.edsof.anotacoes.infrastructure.enums.StatusTarefa;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbltarefas")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", length = 200, nullable = false)
    private String titulo;

    @Column(name = "historico", columnDefinition = "TEXT",nullable = false)
    private String historico;

    @ManyToOne
    @JoinColumn(name = "usuarioid", referencedColumnName = "id", nullable = false)
    private Usuario usuario;

    @Column(name = "data_abertura", nullable = false)
    private LocalDate data_abertura;

    @Column(name = "data_fechamento")
    private LocalDate data_fechamento;

    @Column(name = "data_prazo")
    @FutureOrPresent(message = "O prazo deve ser hoje ou uma data futura")
    private LocalDate data_prazo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private StatusTarefa status;

}
