package com.InAula.InAula.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AulaResponseDTO {

    // Aula
    private Long id;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private String local;
    private Integer capacidadeMaxima;

    // Controle de vagas
    private Integer totalAlunos;
    private Integer vagasDisponiveis;

    // Professor
    private Long idProfessor;
    private String nomeProfessor;
    private BigDecimal valorHora;

    // Matéria
    private Long idMateria;
    private String nomeMateria;
    private String descricaoMateria;
}


