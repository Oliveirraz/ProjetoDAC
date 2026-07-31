package com.InAula.InAula.service.materia.professor;

import com.InAula.InAula.RequestDTO.ProfessorRequestDTO;
import com.InAula.InAula.ResponseDTO.MateriaResponseDTO;
import com.InAula.InAula.ResponseDTO.ProfessorResponseDTO;
import com.InAula.InAula.entity.*;
import com.InAula.InAula.exception.ResourceNotFoundException;
import com.InAula.InAula.repository.AulaRepository;
import com.InAula.InAula.repository.MateriaRepository;
import com.InAula.InAula.repository.MatriculaRepository;
import com.InAula.InAula.repository.ProfessorRepository;
import com.InAula.InAula.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final MateriaRepository materiaRepository;
    private final PasswordEncoder passwordEncoder;

    private final AulaRepository aulaRepository;
    private final MatriculaRepository matriculaRepository;
    private final EmailService emailService;

    // mesmo padrão do aluno onde estão as fotos
    private final String DIRETORIO_FOTOS = "C:\\Users\\Glêisson\\Pictures\\fotosInAula";


    // CRIAR PROFESSOR
    public ProfessorResponseDTO criarProfessor(ProfessorRequestDTO dto, MultipartFile foto) {

        Professor professor = new Professor();
        professor.setNome(dto.nome());
        professor.setEmail(dto.email());

        // SENHA CRIPTOGRAFADA (ESSENCIAL PARA O LOGIN)
        professor.setSenha(passwordEncoder.encode(dto.senha()));

        // Perfil fixo
        professor.setPerfil("Professor");

        if (dto.valorHoraAula() != null) {
            professor.setValorHoraAula(dto.valorHoraAula());
        }

        // FOTO
        if (foto != null && !foto.isEmpty()) {
            try {
                String nomeArquivo = salvarFotoNoDisco(foto);
                professor.setFoto(nomeArquivo);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao salvar a foto do professor");
            }
        }

        Professor salvo = professorRepository.save(professor);
        return toResponseDTO(salvo);
    }





    @Transactional
    public void deletarProfessor(Long id) {

        Professor professor = professorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Professor não encontrado para exclusão"));

        // Cópia da lista de aulas (evita ConcurrentModificationException ao remover)
        List<Aula> aulas = new ArrayList<>(professor.getAulas());

        for (Aula aula : aulas) {

            // Notifica os alunos com matrícula ACEITA nessa aula
            List<Matricula> matriculasAceitas =
                    matriculaRepository.findByAula_IdAndStatus(aula.getId(), MatriculaStatus.ACEITA);

            for (Matricula matricula : matriculasAceitas) {
                try {
                    emailService.enviarExclusaoContaProfessorParaAluno(matricula);
                } catch (Exception e) {
                    System.err.println("Falha ao enviar e-mail de cancelamento para "
                            + matricula.getAluno().getEmail() + ": " + e.getMessage());
                }
            }

            // Remove TODAS as matrículas dessa aula (qualquer status), para não violar FK
            List<Matricula> todasMatriculas = matriculaRepository.findByAula_Id(aula.getId());
            matriculaRepository.deleteAll(todasMatriculas);

            aulaRepository.delete(aula);
        }

        // Remove vínculo do professor com as matérias (ManyToMany)
        List<Materia> materias = new ArrayList<>(professor.getMaterias());
        for (Materia materia : materias) {
            materia.getProfessores().remove(professor);
            professor.getMaterias().remove(materia);

            // Se a matéria não tiver mais nenhum professor vinculado, apaga
            if (materia.getProfessores().isEmpty()) {
                materiaRepository.delete(materia);
            }
        }

        professorRepository.delete(professor);
    }


    // SALVAR FOTO NO DISCO
    private String salvarFotoNoDisco(MultipartFile foto) throws IOException {

        String nomeArquivo = System.currentTimeMillis() + "-" + foto.getOriginalFilename();

        Path caminho = Paths.get(DIRETORIO_FOTOS, nomeArquivo);

        Files.createDirectories(Paths.get(DIRETORIO_FOTOS));
        Files.copy(foto.getInputStream(), caminho, StandardCopyOption.REPLACE_EXISTING);

        return nomeArquivo;
    }


    // CONVERSÃO PARA DTO
    private ProfessorResponseDTO toResponseDTO(Professor professor) {

        List<MateriaResponseDTO> materias = professor.getMaterias()
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
                materias,
                aulasIds,
                professor.getFoto() != null
                        ? "http://localhost:8080/uploads/" + professor.getFoto()
                        : null
        );
    }


    //Usado para pegar o perfil publico do professor
    @Transactional(readOnly = true)
    public ProfessorResponseDTO buscarProfessorPorId(Long id) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Professor não encontrado"));

        return toResponseDTO(professor);
    }


    public ProfessorResponseDTO atualizarProfessor(
            Long id,
            ProfessorRequestDTO dto,
            MultipartFile foto
    ) throws IOException {

        Professor professor = professorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Professor não encontrado")
                );

        if (dto.nome() != null && !dto.nome().isBlank()) {
            professor.setNome(dto.nome());
        }

        if (dto.valorHoraAula() != null) {
            professor.setValorHoraAula(dto.valorHoraAula());
        }

        // SENHA (CRÍTICO)
        if (dto.senha() != null && !dto.senha().isBlank()) {
            professor.setSenha(passwordEncoder.encode(dto.senha()));
        }

        if (foto != null && !foto.isEmpty()) {
            professor.setFoto(salvarFotoNoDisco(foto));
        }

        Professor atualizado = professorRepository.save(professor);
        return toResponseDTO(atualizado);
    }
















}
