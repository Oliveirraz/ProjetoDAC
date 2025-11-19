package com.InAula.InAula.mapper;

import com.InAula.InAula.RequestDTO.AulaRequestDTO;
import com.InAula.InAula.ResponseDTO.AulaResponseDTO;
import com.InAula.InAula.entity.Aluno;
import com.InAula.InAula.entity.Aula;
import org.modelmapper.ModelMapper;

import java.util.stream.Collectors;

public class AulaMapper {

    private AulaMapper() {}

    private static final ModelMapper modelMapper = new ModelMapper();

    // DTO de Requisição (Request) -> Entidade (Aula)
    // Usado no Service para mapear campos básicos (horaInicio, local, etc.).

    public static Aula toAula(AulaRequestDTO dto) {
        Aula aula = modelMapper.map(dto, Aula.class);
        return aula;
    }

    // Entidade (Aula) -> DTO de Resposta (Response)

    public static AulaResponseDTO toResponseDto(Aula aula) {

        // Mapeia campos diretos (idAula, horaInicio, horaFim, local)
        AulaResponseDTO dto = modelMapper.map(aula, AulaResponseDTO.class);

        // ID do Professor (ManyToOne)
        if (aula.getProfessor() != null) {
            dto.setIdProfessor(aula.getProfessor().getId());
        }

        // IDs dos Alunos (ManyToMany)
        if (aula.getAlunos() != null && !aula.getAlunos().isEmpty()) {
            dto.setAlunosIds(
                    aula.getAlunos()
                            .stream()
                            .map(Aluno::getId)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }
}
