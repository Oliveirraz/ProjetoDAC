package com.InAula.InAula.service.materia.aula;

import com.InAula.InAula.RequestDTO.AulaRequestDTO;
import com.InAula.InAula.ResponseDTO.AulaResponseDTO;
import com.InAula.InAula.entity.*;
import com.InAula.InAula.exception.ResourceNotFoundException;
import com.InAula.InAula.mapper.AulaMapper;
import com.InAula.InAula.repository.*;
import com.InAula.InAula.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.InAula.InAula.ResponseDTO.AlunoResponseDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AulaService {

    // DEPENDÊNCIAS
    // Service acessa dados APENAS via Repository
    private final AulaRepository aulaRepository;
    private final ProfessorRepository professorRepository;
    private final MateriaRepository materiaRepository;
    private final AlunoRepository alunoRepository;
    private final MatriculaRepository matriculaRepository;
    private final EmailService emailService;


    private Professor getProfessorLogado() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return professorRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Professor logado não encontrado"));
    }

    private Aluno getAlunoLogado() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return alunoRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Aluno logado não encontrado"));
    }

    // Novo método de matrícula pelo token
    @Transactional
    public AulaResponseDTO matricularAlunoLogado(Long aulaId) {
        Aluno aluno = getAlunoLogado();

        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Aula não encontrada com ID: " + aulaId));

        if (aula.getAlunos().contains(aluno)) {
            throw new IllegalArgumentException("Aluno já está matriculado nesta aula");
        }

        if (aula.getAlunos().size() >= aula.getCapacidadeMaxima()) {
            throw new IllegalArgumentException("Não há vagas disponíveis nesta aula");
        }

        aula.getAlunos().add(aluno);
        return AulaMapper.toResponseDto(aulaRepository.save(aula));
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
        //  prioriza o valor enviado pelo frontend, com fallback para o perfil do professor
        BigDecimal valorHora = (dto.getValorHora() != null && dto.getValorHora().compareTo(BigDecimal.ZERO) > 0)
                ? dto.getValorHora()
                : (professor.getValorHoraAula() != null ? professor.getValorHoraAula() : BigDecimal.ZERO);
        aula.setValorHora(valorHora);

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

        // Guarda os valores antigos para comparar depois e montar o e-mail de aviso
        LocalDate dataAntiga = aula.getData();
        LocalTime horaInicioAntiga = aula.getHoraInicio();
        LocalTime horaFimAntiga = aula.getHoraFim();
        String localAntigo = aula.getLocal();
        BigDecimal valorHoraAntigo = aula.getValorHora();

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

        if (dto.getAlunosIds() != null && !dto.getAlunosIds().isEmpty()) {
            List<Aluno> alunos = buscarAlunos(dto.getAlunosIds());
            validarCapacidade(alunos.size(), aula.getCapacidadeMaxima());
            aula.setAlunos(alunos);
        }

        if (dto.getValorHora() != null) {
            aula.setValorHora(dto.getValorHora());
        }

        Aula aulaAtualizada = aulaRepository.save(aula);

        // Monta o texto com o que mudou e notifica os alunos matriculados (ACEITA)
        String alteracoes = montarTextoAlteracoes(
                dataAntiga, horaInicioAntiga, horaFimAntiga, localAntigo, valorHoraAntigo,
                aulaAtualizada
        );

        if (alteracoes != null) {
            List<Matricula> matriculasAceitas =
                    matriculaRepository.findByAula_IdAndStatus(aulaId, MatriculaStatus.ACEITA);

            for (Matricula matricula : matriculasAceitas) {
                emailService.enviarAtualizacaoAulaParaAluno(matricula, alteracoes);
            }
        }

        return AulaMapper.toResponseDto(aulaAtualizada);
    }

    // Compara os valores antigos com os novos e monta o texto de alterações
    // (retorna null se nada relevante mudou, pra não disparar e-mail à toa)
    private String montarTextoAlteracoes(
            LocalDate dataAntiga,
            LocalTime horaInicioAntiga,
            LocalTime horaFimAntiga,
            String localAntigo,
            BigDecimal valorHoraAntigo,
            Aula aulaAtualizada
    ) {
        StringBuilder sb = new StringBuilder();

        if (!dataAntiga.equals(aulaAtualizada.getData())) {
            sb.append("- Data: ").append(dataAntiga)
                    .append(" → ").append(aulaAtualizada.getData()).append("\n");
        }

        if (!horaInicioAntiga.equals(aulaAtualizada.getHoraInicio())
                || !horaFimAntiga.equals(aulaAtualizada.getHoraFim())) {
            sb.append("- Horário: ").append(horaInicioAntiga).append(" às ").append(horaFimAntiga)
                    .append(" → ").append(aulaAtualizada.getHoraInicio())
                    .append(" às ").append(aulaAtualizada.getHoraFim()).append("\n");
        }

        if (!localAntigo.equals(aulaAtualizada.getLocal())) {
            sb.append("- Local: ").append(localAntigo)
                    .append(" → ").append(aulaAtualizada.getLocal()).append("\n");
        }

        if (valorHoraAntigo != null && aulaAtualizada.getValorHora() != null
                && valorHoraAntigo.compareTo(aulaAtualizada.getValorHora()) != 0) {
            sb.append("- Valor: R$ ").append(valorHoraAntigo)
                    .append(" → R$ ").append(aulaAtualizada.getValorHora()).append("\n");
        }

        return sb.length() == 0 ? null : sb.toString();
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

        // Notifica por e-mail apenas os alunos com matrícula ACEITA
        List<Matricula> matriculasAceitas =
                matriculaRepository.findByAula_IdAndStatus(aulaId, MatriculaStatus.ACEITA);

        for (Matricula matricula : matriculasAceitas) {
            emailService.enviarExclusaoAulaParaAluno(matricula);
        }

        // Remove todas as matrículas ligadas a essa aula (qualquer status),
        // para não violar a FK ao deletar a aula
        List<Matricula> todasMatriculas = matriculaRepository.findByAula_Id(aulaId);
        matriculaRepository.deleteAll(todasMatriculas);

        aulaRepository.delete(aula);
    }

    // Cancelar Aula
    @Transactional
    public void cancelarAulaProfessorLogado(Long aulaId) {

        Professor professor = getProfessorLogado();

        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Aula não encontrada com ID: " + aulaId));

        if (!aula.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalArgumentException("Você não pode cancelar esta aula");
        }

        // Notifica por e-mail apenas os alunos com matrícula ACEITA
        List<Matricula> matriculasAceitas =
                matriculaRepository.findByAula_IdAndStatus(aulaId, MatriculaStatus.ACEITA);

        for (Matricula matricula : matriculasAceitas) {
            emailService.enviarCancelamentoAulaParaAluno(matricula);
        }

        // Remove TODAS as matrículas ligadas a essa aula (qualquer status),
        // para não violar a FK ao deletar a aula
        List<Matricula> todasMatriculas = matriculaRepository.findByAula_Id(aulaId);
        matriculaRepository.deleteAll(todasMatriculas);

        aulaRepository.delete(aula);
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

    @Transactional(readOnly = true)
    public List<AulaResponseDTO> listarAulasAlunoLogado() {
        Aluno aluno = getAlunoLogado();
        return aulaRepository.buscarAulasDoAluno(aluno.getId())
                .stream()
                .map(AulaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    // Listar alunos matriculados numa aula do professor logado
    @Transactional(readOnly = true)
    public List<AlunoResponseDTO> listarAlunosDaAula(Long aulaId) {

        Professor professor = getProfessorLogado();

        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Aula não encontrada com ID: " + aulaId));

        if (!aula.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalArgumentException(
                    "Você não tem permissão para acessar esta aula");
        }

        return aula.getAlunos()
                .stream()
                .map(this::toAlunoResponseDTO)
                .collect(Collectors.toList());
    }

    // Remover um aluno de uma aula do professor logado
    @Transactional
    public void removerAlunoDaAula(Long aulaId, Long alunoId) {

        Professor professor = getProfessorLogado();

        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Aula não encontrada com ID: " + aulaId));

        if (!aula.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalArgumentException(
                    "Você não pode remover alunos desta aula");
        }

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Aluno não encontrado com ID: " + alunoId));

        if (!aula.getAlunos().contains(aluno)) {
            throw new IllegalArgumentException(
                    "Este aluno não está matriculado nesta aula");
        }

        // Remove o vínculo (libera a vaga)
        aula.getAlunos().remove(aluno);
        aulaRepository.save(aula);

        // Atualiza a matrícula ACEITA correspondente e avisa o aluno por e-mail
        matriculaRepository
                .findByAluno_IdAndAula_IdAndStatus(alunoId, aulaId, MatriculaStatus.ACEITA)
                .ifPresent(matricula -> {
                    matricula.setStatus(MatriculaStatus.CANCELADA);
                    matricula.setDataResposta(LocalDateTime.now());
                    matriculaRepository.save(matricula);

                    emailService.enviarRemocaoAlunoParaAluno(matricula);
                });
    }

    private AlunoResponseDTO toAlunoResponseDTO(Aluno aluno) {

        List<Long> aulasIds = aluno.getAulas()
                .stream()
                .map(Aula::getId)
                .toList();

        return new AlunoResponseDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getEmail(),
                aluno.getFoto() != null
                        ? "http://localhost:8080/uploads/" + aluno.getFoto()
                        : null,
                aulasIds
        );
    }

}
