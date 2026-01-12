package com.InAula.InAula.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Aula")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "data")
    private LocalDate data;

    @Column(nullable = false, name = "horaInicio")
    private LocalTime horaInicio;

    @Column(nullable = false, name = "horaFim")
    private LocalTime horaFim;

    @Column(nullable = false, name = "local")
    private String local;

    @Column(nullable = false, name = "valorHora")
    private BigDecimal valorHora;

    @Column(name = "capacidade_maxima")
    private Integer capacidadeMaxima;



    @ManyToOne
    @JoinColumn(name = "id_materia", nullable = false)
    private Materia materia;


    @ManyToMany
    @JoinTable(name = "aulas_alunos",
            joinColumns = @JoinColumn(name = "id_aula"),
            inverseJoinColumns = @JoinColumn(name = "id_aluno")
    )
    private List<Aluno> alunos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "id_professor", nullable = false)
    private Professor professor;


}