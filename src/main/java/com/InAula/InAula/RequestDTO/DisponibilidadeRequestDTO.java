package com.InAula.InAula.RequestDTO;

import com.InAula.InAula.entity.Disponibilidade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DisponibilidadeRequestDTO {
    private Disponibilidade.DiaEnum diaDaSemana;
    private LocalTime horaDisponivel;
    private Long idProfessor;
}
