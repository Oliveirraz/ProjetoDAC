package com.InAula.InAula.RequestDTO;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotNull(message = "Data da aula é obrigatória")
    @FutureOrPresent(message = "A data da aula não pode ser anterior à data atual")
    private LocalDate data;

    @NotNull(message = "Hora de início é obrigatória")
    private LocalTime horaInicio;

    @NotNull(message = "Hora de fim é obrigatória")
    private LocalTime horaFim;

    @NotBlank(message = "O local da aula é obrigatório")
    private String local;

    @NotNull(message = "A matéria é obrigatória")
    private Long idMateria;

    @Positive(message = "A capacidade máxima deve ser positiva")
    private Integer capacidadeMaxima;

    private List<Long> alunosIds;
}


