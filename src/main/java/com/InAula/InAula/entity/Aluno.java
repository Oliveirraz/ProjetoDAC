package com.InAula.InAula.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_alunos")
public class Aluno extends Usuario{

    @ManyToMany
    private List<Aula> aulas = new ArrayList<>();
}
