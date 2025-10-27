package com.revisao.revisao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name= "tb_disponibilidade")
public class Disponibilidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id_disponibilidade")
    private Long idDisponibilidade;
    @Column(name = "dia_da_semana")
    @Enumerated(EnumType.STRING)
    private DiaEnum diaDaSemana;
    private LocalTime horaDisponivel;


    public enum DiaEnum {
         SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA;
     }

}
