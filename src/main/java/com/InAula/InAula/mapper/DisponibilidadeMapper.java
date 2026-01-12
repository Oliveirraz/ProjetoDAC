package com.InAula.InAula.mapper;

import com.InAula.InAula.RequestDTO.DisponibilidadeRequestDTO;
import com.InAula.InAula.ResponseDTO.DisponibilidadeResponseDTO;
import com.InAula.InAula.entity.Disponibilidade;
import com.InAula.InAula.entity.Professor;
import org.modelmapper.ModelMapper;


public class DisponibilidadeMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    public static Disponibilidade toDisponibilidade(
            DisponibilidadeRequestDTO dto,
            Professor professor) {

        Disponibilidade disponibilidade = modelMapper.map(dto, Disponibilidade.class);
        disponibilidade.setProfessor(professor);
        return disponibilidade;
    }


    public static DisponibilidadeResponseDTO toResponseDto(Disponibilidade disponibilidade) {
        // ModelMapper cuida dos campos Dia/Hora
        DisponibilidadeResponseDTO dto = modelMapper.map(disponibilidade, DisponibilidadeResponseDTO.class);

        if (disponibilidade.getProfessor() != null) {
            dto.setIdProfessor(disponibilidade.getProfessor().getId());
        }

        return dto;
    }
}
