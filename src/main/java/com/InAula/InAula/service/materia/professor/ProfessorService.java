package com.InAula.InAula.service.materia.professor;

import com.InAula.InAula.RequestDTO.ProfessorRequestDTO;
import com.InAula.InAula.ResponseDTO.MateriaResponseDTO;
import com.InAula.InAula.ResponseDTO.ProfessorResponseDTO;
import com.InAula.InAula.entity.Aula;
import com.InAula.InAula.entity.Materia;
import com.InAula.InAula.entity.Professor;
import com.InAula.InAula.repository.MateriaRepository;
import com.InAula.InAula.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private MateriaRepository materiaRepository;


    public ProfessorResponseDTO criarProfessor(ProfessorRequestDTO dto) {
        Professor professor = new Professor();

        professor.setNome(dto.nome());
        professor.setEmail(dto.email());
        professor.setSenha(dto.senha());
        professor.setPerfil(dto.perfil());
        professor.setValorHoraAula(dto.valorHoraAula());

        if (dto.materiasIds() != null && !dto.materiasIds().isEmpty()) {
            List<Materia> materias = materiaRepository.findAllById(dto.materiasIds());
            professor.setMaterias(materias);
        }

        Professor salvo = professorRepository.save(professor);
        return toResponseDTO(salvo);
    }

    public List<ProfessorResponseDTO> listarTodos() {
        return professorRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }


    public ProfessorResponseDTO buscarPorId(Long id) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
        return toResponseDTO(professor);
    }

    public ProfessorResponseDTO atualizarProfessor(Long id, ProfessorRequestDTO dto) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        professor.setNome(dto.nome());
        professor.setEmail(dto.email());
        professor.setSenha(dto.senha());
        professor.setPerfil(dto.perfil());
        professor.setValorHoraAula(dto.valorHoraAula());

        if (dto.materiasIds() != null) {
            List<Materia> materias = materiaRepository.findAllById(dto.materiasIds());
            professor.setMaterias(materias);
        }

        Professor atualizado = professorRepository.save(professor);
        return toResponseDTO(atualizado);
    }

    public void deletarProfessor(Long id) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        professorRepository.delete(professor);
    }


    private ProfessorResponseDTO toResponseDTO(Professor professor) {

        List<MateriaResponseDTO> materiasDTO = professor.getMaterias()
                .stream()
                .map(m -> new MateriaResponseDTO(
                        m.getId(),
                        m.getNome(),
                        m.getDescricao()
                ))
                .collect(Collectors.toList());

        List<Long> aulasIds = professor.getAulas()
                .stream()
                .map(Aula::getId)
                .collect(Collectors.toList());

        return new ProfessorResponseDTO(
                professor.getId(),
                professor.getNome(),
                professor.getEmail(),
                professor.getPerfil(),
                professor.getValorHoraAula(),
                materiasDTO,
                aulasIds
        );
    }

}
