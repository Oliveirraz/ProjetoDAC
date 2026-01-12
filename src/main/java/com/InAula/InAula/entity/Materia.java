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

    //Essa ligação esta morta, pois a ideia mudou antes o aluno poderia se cadastrar ou selecionar
    //Materias, mas pelo estilo de negocio ele apenas se matricula na aula.
    @ManyToMany(mappedBy = "materias")
    private List<Aluno> alunos = new ArrayList<>();

    @ManyToMany(mappedBy = "materias")
    private List<Professor> professores = new ArrayList<>();

    @OneToMany(mappedBy = "materia")
    private List<Aula> aulas = new ArrayList<>();


}
