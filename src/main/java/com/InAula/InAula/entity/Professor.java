package com.InAula.InAula.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_professores")
public class Professor extends Usuario{

    @Column(nullable = false)
    private String perfil;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorHoraAula;

    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Aula> aulas = new ArrayList<>();

}
