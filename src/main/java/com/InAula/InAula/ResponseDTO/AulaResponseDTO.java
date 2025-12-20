package com.InAula.InAula.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AulaResponseDTO {

    private Long id;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private String local;
    private BigDecimal valorHora;
    private BigDecimal valorTotal;  // Calculado
    private Long duracaoHoras;      // Calculado
    private Long idProfessor;
    private List<Long> alunosIds;
}
