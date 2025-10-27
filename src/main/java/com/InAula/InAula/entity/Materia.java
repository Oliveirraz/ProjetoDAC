package com.InAula.InAula.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Table(name= "tb_materias")
public class Materia {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name= "tb_id")
    private UUID idMateria;
    @Column(name= "nome", nullable = false,length = 100)
    private String nome;
}
