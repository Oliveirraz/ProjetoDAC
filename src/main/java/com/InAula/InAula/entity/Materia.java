package com.InAula.InAula.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "materia")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "nome", length = 100)
    private String nome;

    @Column(nullable = false, name = "descricao")
    private String descricao;

    @ManyToMany(mappedBy = "materias")
    private List<Aluno> alunos = new ArrayList<>();

    @ManyToMany(mappedBy = "materias")
    private List<Professor> professores = new ArrayList<>();

}
