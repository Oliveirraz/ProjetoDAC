package com.InAula.InAula.service.materia.disponibilidade;

import com.InAula.InAula.RequestDTO.DisponibilidadeRequestDTO;
import com.InAula.InAula.ResponseDTO.DisponibilidadeResponseDTO;
import com.InAula.InAula.entity.Disponibilidade;
import com.InAula.InAula.entity.Professor;
import com.InAula.InAula.exception.ResourceNotFoundException;
import com.InAula.InAula.mapper.DisponibilidadeMapper;
import com.InAula.InAula.repository.DisponibilidadeRepository;
import com.InAula.InAula.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DisponibilidadeService {
    private final DisponibilidadeRepository disponibilidadeRepository;
    private final ProfessorRepository professorRepository;


    // --- (CREATE) ---
    @Transactional
    public DisponibilidadeResponseDTO salvar(DisponibilidadeRequestDTO dto) {
        // Busca e valida a existência do Professor
        Professor professor = professorRepository.findById(dto.getIdProfessor())
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + dto.getIdProfessor()));

        Disponibilidade disponibilidade = new Disponibilidade();
        disponibilidade.setDiaDaSemana(dto.getDiaDaSemana());
        disponibilidade.setHoraDisponivel(dto.getHoraDisponivel());
        disponibilidade.setProfessor(professor);

        Disponibilidade salvo = disponibilidadeRepository.save(disponibilidade);
        return DisponibilidadeMapper.toResponseDto(salvo);
    }

    // --- (consulta por ID) ---
    @Transactional(readOnly = true)
    public DisponibilidadeResponseDTO buscarPorId(Long id) {
        Disponibilidade disponibilidade = disponibilidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidade não encontrada com ID: " + id));
        return DisponibilidadeMapper.toResponseDto(disponibilidade);
    }

    // --- (consulta todos) ---
    @Transactional(readOnly = true)
    public List<DisponibilidadeResponseDTO> buscarTodos() {
        return disponibilidadeRepository.findAll().stream()
                .map(DisponibilidadeMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    // --- (UPDATE) ---
    @Transactional
    public DisponibilidadeResponseDTO atualizar(Long id, DisponibilidadeRequestDTO dto) {
        // Busca e valida a Disponibilidade existente
        Disponibilidade disponibilidadeExistente = disponibilidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidade não encontrada com ID: " + id));

        // Busca e valida o Professor
        Professor novoProfessor = professorRepository.findById(dto.getIdProfessor())
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + dto.getIdProfessor()));

        //  Atualiza os campos
        disponibilidadeExistente.setDiaDaSemana(dto.getDiaDaSemana());
        disponibilidadeExistente.setHoraDisponivel(dto.getHoraDisponivel());
        disponibilidadeExistente.setProfessor(novoProfessor);

        Disponibilidade updatedDisponibilidade = disponibilidadeRepository.save(disponibilidadeExistente);
        return DisponibilidadeMapper.toResponseDto(updatedDisponibilidade);
    }

    // --- (DELETE) ---
    @Transactional
    public void deletar(Long id) {
        // Busca a Entidade para garantir que ela exista antes de deletar
        Disponibilidade disponibilidade = disponibilidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidade não encontrada com ID: " + id));

        disponibilidadeRepository.delete(disponibilidade);
    }
}
