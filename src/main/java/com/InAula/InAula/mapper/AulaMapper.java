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
        AulaResponseDTO dto = new AulaResponseDTO();
        dto.setId(aula.getId());
        dto.setData(aula.getData());
        dto.setHoraInicio(aula.getHoraInicio());
        dto.setHoraFim(aula.getHoraFim());
        dto.setLocal(aula.getLocal());
        dto.setValorHora(aula.getValorHora());
        dto.setValorTotal(aula.getValorTotal());  // Calculado
        dto.setDuracaoHoras(aula.getDuracaoEmHoras());  // Calculado
        dto.setIdProfessor(aula.getProfessor().getId());
        dto.setAlunosIds(aula.getAlunos().stream()
                .map(aluno -> aluno.getId())
                .collect(Collectors.toList()));
        return dto;
    }
}
