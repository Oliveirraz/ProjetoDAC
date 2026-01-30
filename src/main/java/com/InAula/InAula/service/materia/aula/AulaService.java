package com.InAula.InAula.service.materia.aula;

import com.InAula.InAula.RequestDTO.AulaRequestDTO;
import com.InAula.InAula.ResponseDTO.AulaResponseDTO;
import com.InAula.InAula.entity.Aluno;
import com.InAula.InAula.entity.Aula;
import com.InAula.InAula.entity.Materia;
import com.InAula.InAula.entity.Professor;
import com.InAula.InAula.exception.ResourceNotFoundException;
import com.InAula.InAula.mapper.AulaMapper;
import com.InAula.InAula.repository.AlunoRepository;
import com.InAula.InAula.repository.AulaRepository;
import com.InAula.InAula.repository.MateriaRepository;
import com.InAula.InAula.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AulaService {

    // DEPENDÊNCIAS
    // Service acessa dados APENAS via Repository
    private final AulaRepository aulaRepository;
    private final ProfessorRepository professorRepository;
    private final MateriaRepository materiaRepository;
    private final AlunoRepository alunoRepository;


    private Professor getProfessorLogado() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return professorRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Professor logado não encontrado"));
    }


    @Transactional
    public AulaResponseDTO salvarAulaProfessorLogado(AulaRequestDTO dto) {

        validarHorario(dto);

        Professor professor = getProfessorLogado();
        Materia materia = buscarMateria(dto.getIdMateria());

        validarProfessorMateria(professor, materia);

        List<Aluno> alunos = buscarAlunos(dto.getAlunosIds());
        validarCapacidade(alunos.size(), dto.getCapacidadeMaxima());

        Aula aula = AulaMapper.toAula(dto);
        aula.setProfessor(professor);
        aula.setMateria(materia);
        aula.setAlunos(alunos);
        aula.setValorHora(professor.getValorHoraAula());

        return AulaMapper.toResponseDto(aulaRepository.save(aula));
    }



    @Transactional(readOnly = true)
    public Page<AulaResponseDTO> listarAulasProfessorLogado(int page, int size) {

        Professor professor = getProfessorLogado();
        Pageable pageable = PageRequest.of(page, size);

        return aulaRepository.findByProfessor_Id(
                professor.getId(), pageable
        ).map(AulaMapper::toResponseDto);
    }



    @Transactional(readOnly = true)
    public AulaResponseDTO buscarAulaProfessorLogadoPorId(Long aulaId) {

        Professor professor = getProfessorLogado();

        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Aula não encontrada com ID: " + aulaId));

        if (!aula.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalArgumentException(
                    "Você não tem permissão para acessar esta aula");
        }

        return AulaMapper.toResponseDto(aula);
    }


    @Transactional
    public AulaResponseDTO atualizarAulaProfessorLogado(
            Long aulaId, AulaRequestDTO dto) {

        Professor professor = getProfessorLogado();

        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Aula não encontrada com ID: " + aulaId));

        if (!aula.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalArgumentException(
                    "Você não pode atualizar esta aula");
        }

        validarHorarioAtualizacao(aula, dto);

        if (dto.getData() != null) aula.setData(dto.getData());
        if (dto.getHoraInicio() != null) aula.setHoraInicio(dto.getHoraInicio());
        if (dto.getHoraFim() != null) aula.setHoraFim(dto.getHoraFim());

        if (dto.getLocal() != null && !dto.getLocal().isBlank()) {
            aula.setLocal(dto.getLocal());
        }

        if (dto.getCapacidadeMaxima() != null) {
            aula.setCapacidadeMaxima(dto.getCapacidadeMaxima());
        }

        if (dto.getIdMateria() != null) {
            Materia materia = buscarMateria(dto.getIdMateria());
            validarProfessorMateria(professor, materia);
            aula.setMateria(materia);
        }

        if (dto.getAlunosIds() != null) {
            List<Aluno> alunos = buscarAlunos(dto.getAlunosIds());
            validarCapacidade(alunos.size(), aula.getCapacidadeMaxima());
            aula.setAlunos(alunos);
        }

        return AulaMapper.toResponseDto(aulaRepository.save(aula));
    }


    @Transactional
    public void deletarAulaProfessorLogado(Long aulaId) {

        Professor professor = getProfessorLogado();

        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Aula não encontrada com ID: " + aulaId));

        if (!aula.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalArgumentException(
                    "Você não pode deletar esta aula");
        }

        aulaRepository.delete(aula);
    }




    // Aulas com ID



    // BUSCAR AULA POR ID
    @Transactional(readOnly = true)
    public AulaResponseDTO buscarPorId(Long id) {

        Aula aula = aulaRepository.findById(id)
                // Erro 404: recurso não encontrado
                .orElseThrow(() ->
                        new ResourceNotFoundException("Aula não encontrada com ID: " + id));

        return AulaMapper.toResponseDto(aula);
    }

    // LISTAR TODAS - SEM PAGINAÇÃO
    @Transactional(readOnly = true)
    public List<AulaResponseDTO> listarTodos() {

        return aulaRepository.findAll()
                .stream()
                .map(AulaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    // LISTAR TODAS - COM PAGINAÇÃO
    @Transactional(readOnly = true)
    public Page<AulaResponseDTO> listarTodosPaginado(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return aulaRepository.findAll(pageable)
                .map(AulaMapper::toResponseDto);
    }

    // BUSCA POR MATÉRIA OU PROFESSOR - COM FILTRO
    @Transactional(readOnly = true)
    public Page<AulaResponseDTO> buscarPorMateriaOuProfessor(
            String termo, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        // Evita buscas quebradas com string vazia
        if (termo != null && termo.trim().isEmpty()) {
            termo = null;
        }

        Page<Aula> aulas = (termo == null)
                ? aulaRepository.findAll(pageable)
                : aulaRepository.buscarPorMateriaOuProfessor(termo, pageable);

        return aulas.map(AulaMapper::toResponseDto);
    }



    // DELETAR AULA
    @Transactional
    public void deletar(Long id) {

        if (!aulaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Aula não encontrada com ID: " + id);
        }

        aulaRepository.deleteById(id);
    }

    // LISTAR AULAS POR PROFESSOR
    @Transactional(readOnly = true)
    public Page<AulaResponseDTO> listarPorProfessorPaginado(
            Long professorId, int page, int size) {

        if (!professorRepository.existsById(professorId)) {
            throw new ResourceNotFoundException(
                    "Professor não encontrado com ID: " + professorId);
        }

        Pageable pageable = PageRequest.of(page, size);

        return aulaRepository.findByProfessor_Id(professorId, pageable)
                .map(AulaMapper::toResponseDto);
    }


    // MATRICULAR ALUNO
    @Transactional
    public AulaResponseDTO matricularAluno(Long aulaId, Long alunoId) {

        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Aula não encontrada com ID: " + aulaId));

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Aluno não encontrado com ID: " + alunoId));

        // Regra de negócio: aluno não pode repetir
        if (aula.getAlunos().contains(aluno)) {
            throw new IllegalArgumentException("Aluno já está matriculado nesta aula");
        }

        // Regra de negócio: limite de vagas
        if (aula.getAlunos().size() >= aula.getCapacidadeMaxima()) {
            throw new IllegalArgumentException("Não há vagas disponíveis nesta aula");
        }

        aula.getAlunos().add(aluno);

        return AulaMapper.toResponseDto(aulaRepository.save(aula));
    }


    // LISTAR AULAS DO ALUNO
    @Transactional(readOnly = true)
    public List<AulaResponseDTO> listarAulasDoAluno(Long alunoId) {

        if (!alunoRepository.existsById(alunoId)) {
            throw new ResourceNotFoundException(
                    "Aluno não encontrado com ID: " + alunoId);
        }

        return aulaRepository.buscarAulasDoAluno(alunoId)
                .stream()
                .map(AulaMapper::toResponseDto)
                .collect(Collectors.toList());
    }


    // MÉTODOS AUXILIARES - REGRAS DE NEGÓCIO
    private void validarHorario(AulaRequestDTO dto) {

        //  Só valida se ambos existirem - DTO já garante @NotNull no create
        if (dto.getHoraInicio() != null && dto.getHoraFim() != null) {

            // Hora fim deve ser depois da hora início
            if (!dto.getHoraFim().isAfter(dto.getHoraInicio())) {
                throw new IllegalArgumentException(
                        "A hora de término da aula deve ser maior que a hora de início"
                );
            }

            // Duração mínima de 1 hora
            long minutos = java.time.Duration
                    .between(dto.getHoraInicio(), dto.getHoraFim())
                    .toMinutes();

            if (minutos < 60) {
                throw new IllegalArgumentException(
                        "O horário da aula deve ter no mínimo 1 hora de duração"
                );
            }
        }
    }


    // Busca professor ou lança 404 CASO O PROFESSOR NÃO EXISTA
    private Professor buscarProfessor(Long id) {
        return professorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Professor não encontrado com ID: " + id));
    }

    //Busca matéria ou lança 404 -  CASO A MATÉRIA NÃO EXISTA
    private Materia buscarMateria(Long id) {
        return materiaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Matéria não encontrada com ID: " + id));
    }

    // Professor só pode dar aula da matéria que ministra
    private void validarProfessorMateria(Professor professor, Materia materia) {
        if (!professor.getMaterias().contains(materia)) {
            throw new IllegalArgumentException(
                    "Professor não ministra esta matéria");
        }
    }

    // Garante que todos os alunos existam
    private List<Aluno> buscarAlunos(List<Long> alunosIds) {

        if (alunosIds == null || alunosIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Aluno> alunos = alunoRepository.findAllById(alunosIds);

        if (alunos.size() != alunosIds.size()) {
            throw new ResourceNotFoundException(
                    "Um ou mais alunos não foram encontrados");
        }

        return alunos;
    }

    // Regra de capacidade
    private void validarCapacidade(int qtdAlunos, int capacidade) {
        if (qtdAlunos > capacidade) {
            throw new IllegalArgumentException(
                    "Quantidade de alunos excede a capacidade máxima da aula");
        }
    }

    private void validarHorarioAtualizacao(Aula aula, AulaRequestDTO dto) {

        // Usa o novo valor se vier, senão mantém o antigo
        var horaInicio = dto.getHoraInicio() != null
                ? dto.getHoraInicio()
                : aula.getHoraInicio();

        var horaFim = dto.getHoraFim() != null
                ? dto.getHoraFim()
                : aula.getHoraFim();

        // Hora fim deve ser depois da hora início
        if (!horaFim.isAfter(horaInicio)) {
            throw new IllegalArgumentException(
                    "A hora de término da aula deve ser maior que a hora de início"
            );
        }

        //  Duração mínima de 1 hora
        long minutos = java.time.Duration
                .between(horaInicio, horaFim)
                .toMinutes();

        if (minutos < 60) {
            throw new IllegalArgumentException(
                    "O horário da aula deve ter no mínimo 1 hora de duração"
            );
        }
    }

}
