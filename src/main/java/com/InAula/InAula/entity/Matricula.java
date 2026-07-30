package com.InAula.InAula.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "matricula")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_aluno", nullable = false)
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "id_aula", nullable = false)
    private Aula aula;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatriculaStatus status = MatriculaStatus.PENDENTE;

    // token único usado nos links do e-mail (aceitar/recusar sem precisar logar)
    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime dataSolicitacao;

    private LocalDateTime dataResposta;
}