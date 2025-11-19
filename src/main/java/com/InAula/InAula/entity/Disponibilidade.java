package com.InAula.InAula.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "disponibilidade")
public class Disponibilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDisponibilidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_da_semana", nullable = false )
    private DiaEnum diaDaSemana;
    @Column(name = "hora_disponivel")
    private LocalTime horaDisponivel;

    @ManyToOne
    @JoinColumn(name = "id_professor", nullable = false)
    private Professor professor;

    public enum DiaEnum{
        SEGUNDA, TERCA, QUARTA,
        QUINTA, SEXTA;
    }
}
