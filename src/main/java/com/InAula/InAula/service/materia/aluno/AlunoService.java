package com.InAula.InAula.service.materia.aluno;

import com.InAula.InAula.RequestDTO.AlunoRequestDTO;
import com.InAula.InAula.ResponseDTO.AlunoResponseDTO;
import com.InAula.InAula.ResponseDTO.MateriaResponseDTO;
import com.InAula.InAula.entity.Aluno;
import com.InAula.InAula.entity.Aula;
import com.InAula.InAula.entity.Materia;
import com.InAula.InAula.exception.ResourceNotFoundException;
import com.InAula.InAula.repository.AlunoRepository;
import com.InAula.InAula.repository.MateriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class AlunoService {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    public AlunoResponseDTO criarAluno(AlunoRequestDTO alunoDTO){
        Aluno aluno = new Aluno();
        aluno.setNome(alunoDTO.nome());
        aluno.setEmail(alunoDTO.email());
        aluno.setSenha(alunoDTO.senha());

        if (alunoDTO.materiasIDs() != null && !alunoDTO.materiasIDs().isEmpty()){
            List<Materia> materias = materiaRepository.findAllById(alunoDTO.materiasIDs());
            aluno.setMaterias(materias);
        }
        Aluno alunoSalvo = alunoRepository.save(aluno);
        return toResponseDTO(alunoSalvo);//Monta meu DTO
    }

    public List<AlunoResponseDTO> listarTodos() {
        return alunoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public AlunoResponseDTO buscarPorId(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        return toResponseDTO(aluno);
    }

    private AlunoResponseDTO toResponseDTO(Aluno aluno) {
        // Converte as matérias do aluno em DTOs
        List<MateriaResponseDTO> materias = aluno.getMaterias()
                .stream()
                .map(m -> new MateriaResponseDTO(
                        m.getId(),
                        m.getNome(),
                        m.getDescricao()
                ))
                .collect(Collectors.toList());

        // Pega apenas os IDs das aulas
        List<Long> aulasIds = aluno.getAulas()
                .stream()
                .map(Aula::getId)
                .collect(Collectors.toList());

        return new AlunoResponseDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getEmail(),
                materias,
                aulasIds
        );
    }

    public AlunoResponseDTO atualizarAluno(Long id, AlunoRequestDTO alunoDTO) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));

        aluno.setNome(alunoDTO.nome());
        aluno.setEmail(alunoDTO.email());
        aluno.setSenha(alunoDTO.senha());

        if (alunoDTO.materiasIDs() != null && !alunoDTO.materiasIDs().isEmpty()) {
            List<Materia> materias = materiaRepository.findAllById(alunoDTO.materiasIDs());
            aluno.setMaterias(materias);
        } else {
            aluno.getMaterias().clear();
        }

        Aluno atualizado = alunoRepository.save(aluno);
        return toResponseDTO(atualizado);
    }

    public void deletarAluno(Long id) {
        if (!alunoRepository.existsById(id)) {
            throw new RuntimeException("Aluno não encontrado para exclusão");
        }
        alunoRepository.deleteById(id);
    }

}
