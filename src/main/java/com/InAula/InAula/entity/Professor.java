package com.InAula.InAula.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "professor")
@Getter
@Setter
public class Professor extends Usuario {

    @Column(nullable = false, name = "perfil")
    private String perfil;

    @Column(nullable = false, name = "valorHoraAula", precision = 10, scale = 2)
    private BigDecimal valorHoraAula;

    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Aula> aulas = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "professor_materia",
            joinColumns = @JoinColumn(name = "professor_id"),
            inverseJoinColumns = @JoinColumn(name = "materia_id")

    )
    private List<Materia> materias = new ArrayList<>();

    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Disponibilidade> disponibilidades = new ArrayList<>();


}
