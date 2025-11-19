package com.InAula.InAula.service.materia.aula;

import com.InAula.InAula.RequestDTO.AulaRequestDTO;
import com.InAula.InAula.ResponseDTO.AulaResponseDTO;
import com.InAula.InAula.entity.Aluno;
import com.InAula.InAula.entity.Aula;
import com.InAula.InAula.entity.Professor;
import com.InAula.InAula.exception.ResourceNotFoundException;
import com.InAula.InAula.mapper.AulaMapper;
import com.InAula.InAula.repository.AlunoRepository;
import com.InAula.InAula.repository.AulaRepository;
import com.InAula.InAula.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AulaService {

    private final AulaRepository aulaRepository;
    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;

    @Transactional
    public AulaResponseDTO salvarAula(AulaRequestDTO dto) {

        // Busca e Valida o Professor
        Professor professor = professorRepository.findById(dto.getIdProfessor())
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + dto.getIdProfessor()));

        // Busca os Alunos
        List<Aluno> alunos = alunoRepository.findAllById(dto.getAlunosIds());

        // Cria e Mapeia a Entidade Aula manualmente
        Aula aula = new Aula();
        aula.setHoraInicio(dto.getHoraInicio());
        aula.setHoraFim(dto.getHoraFim());
        aula.setLocal(dto.getLocal());

        // Injeta as Entidades de Chave Estrangeira
        aula.setProfessor(professor);
        aula.setAlunos(alunos);

        // Salva no Banco de Dados
        Aula salva = aulaRepository.save(aula);

        // Converte a Entidade Salva para DTO de Resposta usando o Mapper
        return AulaMapper.toResponseDto(salva);
    }

    @Transactional(readOnly = true)
    public AulaResponseDTO buscarPorId(Long id) {
        Aula aula = aulaRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Aula não encontrada com ID: " + id));
        return AulaMapper.toResponseDto(aula);

    }

    @Transactional(readOnly = true)
    public List<AulaResponseDTO> listarTodos() {
        List<Aula> aulas = aulaRepository.findAll();
        return aulas.stream().map(AulaMapper::toResponseDto).collect(Collectors.toList());
    }

    @Transactional
    public AulaResponseDTO atualizar(Long id, AulaRequestDTO dto) {

        Aula aulaExistente = aulaRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Aula não encontrada com ID: " + id));

        Professor novoProfessor = professorRepository.findById(dto.getIdProfessor())
                .orElseThrow(()
                        -> new ResourceNotFoundException("Professor não encontrado com ID: " + dto.getIdProfessor()));
        List<Aluno> novosAlunos = alunoRepository.findAllById(dto.getAlunosIds());
        aulaExistente.setHoraInicio(dto.getHoraInicio());
        aulaExistente.setHoraFim(dto.getHoraFim());
        aulaExistente.setLocal(dto.getLocal());
        aulaExistente.setProfessor(novoProfessor);
        aulaExistente.setAlunos(novosAlunos);

        Aula atualizada = aulaRepository.save(aulaExistente);
        return AulaMapper.toResponseDto(atualizada);
    }

    public void deletar(Long id) {
        if (!aulaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Aula não encontrada com ID: " + id);
        }
        aulaRepository.deleteById(id);
    }
}
