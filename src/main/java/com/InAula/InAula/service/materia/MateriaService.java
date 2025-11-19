package com.InAula.InAula.service.materia;


import com.InAula.InAula.RequestDTO.MateriaRequestDTO;
import com.InAula.InAula.ResponseDTO.MateriaResponseDTO;
import com.InAula.InAula.entity.Materia;
import com.InAula.InAula.exception.ResourceNotFoundException;
import com.InAula.InAula.repository.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MateriaService {


    @Autowired
    private MateriaRepository materiaRepository;

    public MateriaResponseDTO criar(MateriaRequestDTO dto) {
        Materia materia = new Materia();
        materia.setNome(dto.nome());
        materia.setDescricao(dto.descricao());

        materiaRepository.save(materia);

        return toResponse(materia);
    }

    public List<MateriaResponseDTO> listarTodos() {
        return materiaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public MateriaResponseDTO buscarPorId(Long id) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() ->  new RuntimeException("Matéria não encontrado"));

        return toResponse(materia);
    }

    public MateriaResponseDTO atualizar(Long id, MateriaRequestDTO dto) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matéria não encontrado"));

        materia.setNome(dto.nome());
        materia.setDescricao(dto.descricao());

        materiaRepository.save(materia);

        return toResponse(materia);
    }

    public void deletar(Long id) {
        if (!materiaRepository.existsById(id)) {
            throw new RuntimeException("Matéria não encontrado para exclusão");
        }

        materiaRepository.deleteById(id);
    }

    private MateriaResponseDTO toResponse(Materia materia) {
        return new MateriaResponseDTO(
                materia.getId(),
                materia.getNome(),
                materia.getDescricao()
        );
    }


}
