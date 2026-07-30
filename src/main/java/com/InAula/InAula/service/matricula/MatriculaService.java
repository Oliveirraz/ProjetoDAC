package com.InAula.InAula.service.matricula;

import com.InAula.InAula.entity.*;
import com.InAula.InAula.exception.ResourceNotFoundException;
import com.InAula.InAula.repository.AlunoRepository;
import com.InAula.InAula.repository.AulaRepository;
import com.InAula.InAula.repository.MatriculaRepository;
import com.InAula.InAula.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AulaRepository aulaRepository;
    private final AlunoRepository alunoRepository;
    private final EmailService emailService;

    private Aluno getAlunoLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return alunoRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno logado não encontrado"));
    }

    @Transactional
    public void solicitarMatricula(Long aulaId) {
        Aluno aluno = getAlunoLogado();

        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() -> new ResourceNotFoundException("Aula não encontrada"));

        boolean jaExiste = matriculaRepository.existsByAluno_IdAndAula_IdAndStatusIn(
                aluno.getId(), aula.getId(),
                List.of(MatriculaStatus.PENDENTE, MatriculaStatus.ACEITA)
        );

        if (jaExiste) {
            throw new IllegalArgumentException("Você já solicitou ou está matriculado nesta aula");
        }

        if (aula.getAlunos().size() >= aula.getCapacidadeMaxima()) {
            throw new IllegalArgumentException("Não há vagas disponíveis nesta aula");
        }

        Matricula matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setAula(aula);
        matricula.setStatus(MatriculaStatus.PENDENTE);
        matricula.setToken(UUID.randomUUID().toString());
        matricula.setDataSolicitacao(LocalDateTime.now());

        matriculaRepository.save(matricula);
        emailService.enviarSolicitacaoParaProfessor(matricula);
    }

    @Transactional
    public String responderMatricula(String token, boolean aceitar) {
        Matricula matricula = matriculaRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada"));

        if (matricula.getStatus() != MatriculaStatus.PENDENTE) {
            return "Esta solicitação já foi respondida anteriormente.";
        }

        Aula aula = matricula.getAula();

        if (!aceitar) {
            matricula.setStatus(MatriculaStatus.RECUSADA);
            matricula.setDataResposta(LocalDateTime.now());
            matriculaRepository.save(matricula);
            emailService.enviarRecusaParaAluno(matricula);
            return "Matrícula recusada. O aluno foi notificado por e-mail.";
        }

        if (aula.getAlunos().size() >= aula.getCapacidadeMaxima()) {
            matricula.setStatus(MatriculaStatus.RECUSADA);
            matricula.setDataResposta(LocalDateTime.now());
            matriculaRepository.save(matricula);
            emailService.enviarRecusaParaAluno(matricula);
            return "Não há mais vagas disponíveis. A solicitação foi recusada automaticamente.";
        }

        aula.getAlunos().add(matricula.getAluno());
        aulaRepository.save(aula);

        matricula.setStatus(MatriculaStatus.ACEITA);
        matricula.setDataResposta(LocalDateTime.now());
        matriculaRepository.save(matricula);

        emailService.enviarConfirmacaoParaAluno(matricula);
        return "Matrícula aceita com sucesso! O aluno foi notificado por e-mail.";
    }
}