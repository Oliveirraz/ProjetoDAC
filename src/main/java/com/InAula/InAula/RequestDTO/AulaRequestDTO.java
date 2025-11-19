package com.InAula.InAula.RequestDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AulaRequestDTO {

    private Time horaInicio;
    private Time horaFim;
    private String local;
    private Long idProfessor;
    private List<Long> alunosIds;
}
