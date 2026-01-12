package com.InAula.InAula.RequestDTO;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
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
public class AulaRequestDTO {

    // ALGUMAS VALIDAÇÕES
    @NotNull(message = "Data da aula é obrigatória")
    @FutureOrPresent(message = "A data da aula não pode ser anterior à data atual")
    private LocalDate data;

    @NotNull(message = "Hora de início é obrigatória")
    private LocalTime horaInicio;

    @NotNull(message = "Hora de fim é obrigatória")
    private LocalTime horaFim;

    private String local;
    private BigDecimal valorHora;

    private Long idProfessor;
    private Long idMateria;

    private Integer capacidadeMaxima;
    private List<Long> alunosIds;
}

