package com.InAula.InAula.entity;

import jakarta.persistence.*;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_aulas")
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Time horaInicio;

    @Column(nullable = false)
    private  Time horaFim;

    @Column(nullable = false)
    private String local;

    @ManyToOne
    @JoinColumn(name = "id_professor", nullable = false)
    private Professor professor;

    @ManyToMany
    @JoinTable(
            name = "tb_aulas_alunos",
            joinColumns = @JoinColumn(name = "id_aula"),
            inverseJoinColumns = @JoinColumn(name = "id_aluno")
    )
    private List<Aluno> alunos = new ArrayList<>();



}
