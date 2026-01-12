package com.InAula.InAula.mapper;

import com.InAula.InAula.RequestDTO.AulaRequestDTO;
import com.InAula.InAula.ResponseDTO.AulaResponseDTO;
import com.InAula.InAula.entity.Aula;

public class AulaMapper {

    private AulaMapper() {}

    // DTO de Requisição - Entidade
    public static Aula toAula(AulaRequestDTO dto) {
        Aula aula = new Aula();

        aula.setData(dto.getData());
        aula.setHoraInicio(dto.getHoraInicio());
        aula.setHoraFim(dto.getHoraFim());
        aula.setLocal(dto.getLocal());
        aula.setCapacidadeMaxima(dto.getCapacidadeMaxima());
        aula.setValorHora(dto.getValorHora());

        return aula;
    }


    // Entidade -> DTO de Resposta
    public static AulaResponseDTO toResponseDto(Aula aula) {
        AulaResponseDTO dto = new AulaResponseDTO();

        // Aula
        dto.setId(aula.getId());
        dto.setData(aula.getData());
        dto.setHoraInicio(aula.getHoraInicio());
        dto.setHoraFim(aula.getHoraFim());
        dto.setLocal(aula.getLocal());
        dto.setCapacidadeMaxima(aula.getCapacidadeMaxima());

        // Professor
        // Professor
        if (aula.getProfessor() != null) {
            dto.setIdProfessor(aula.getProfessor().getId());
            dto.setNomeProfessor(aula.getProfessor().getNome());
        }

        // Valor da aula vem da AULA, não do professor
        dto.setValorHora(aula.getValorHora());


        // Matéria
        if (aula.getMateria() != null) {
            dto.setIdMateria(aula.getMateria().getId());
            dto.setNomeMateria(aula.getMateria().getNome());
            dto.setDescricaoMateria(aula.getMateria().getDescricao());
        }

        // VAGAS EM TEMPO REAL
        int totalAlunos = aula.getAlunos() != null ? aula.getAlunos().size() : 0;
        int capacidade = aula.getCapacidadeMaxima() != null ? aula.getCapacidadeMaxima() : 0;

        dto.setTotalAlunos(totalAlunos);
        dto.setVagasDisponiveis(capacidade - totalAlunos);

        return dto;
    }



}
