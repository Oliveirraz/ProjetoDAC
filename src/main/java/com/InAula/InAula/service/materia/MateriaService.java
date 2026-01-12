package com.InAula.InAula.service.materia;

import com.InAula.InAula.RequestDTO.MateriaRequestDTO;
import com.InAula.InAula.ResponseDTO.MateriaResponseDTO;
import com.InAula.InAula.entity.Materia;
import com.InAula.InAula.entity.Professor;
import com.InAula.InAula.exception.ResourceNotFoundException;
import com.InAula.InAula.repository.MateriaRepository;
import com.InAula.InAula.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MateriaService {

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private ProfessorRepository professorRepository;


    // CRIAR MATÉRIA
    public MateriaResponseDTO criar(MateriaRequestDTO dto) {

        // Professor precisa existir
        Professor professor = professorRepository.findById(dto.professorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Professor não encontrado"));

        Materia materia = new Materia();
        materia.setNome(dto.nome());
        materia.setDescricao(dto.descricao());

        // associação bidirecional
        materia.getProfessores().add(professor);
        professor.getMaterias().add(materia);

        materiaRepository.save(materia);

        return toResponse(materia);
    }


    // LISTAR TODAS
    public List<MateriaResponseDTO> listarTodos() {
        return materiaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    // BUSCAR POR ID
    public MateriaResponseDTO buscarPorId(Long id) {

        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Matéria não encontrada"));

        return toResponse(materia);
    }

    // ATUALIZAR MATÉRIA
    public MateriaResponseDTO atualizar(Long id, MateriaRequestDTO dto) {

        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Matéria não encontrada"));

        materia.setNome(dto.nome());
        materia.setDescricao(dto.descricao());

        materiaRepository.save(materia);

        return toResponse(materia);
    }

    // DELETAR MATÉRIA
    @Transactional
    public void deletar(Long id) {

        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Matéria não encontrada"));

        // Remove vínculo nos DOIS lados - evita erro de FK
        materia.getProfessores().forEach(professor ->
                professor.getMaterias().remove(materia)
        );

        materia.getProfessores().clear();

        materiaRepository.delete(materia);
    }

    // CONVERSÃO PARA DTO
    private MateriaResponseDTO toResponse(Materia materia) {
        return new MateriaResponseDTO(
                materia.getId(),
                materia.getNome(),
                materia.getDescricao()
        );
    }
}
