package com.InAula.InAula.service.materia.professor;

import com.InAula.InAula.RequestDTO.ProfessorRequestDTO;
import com.InAula.InAula.ResponseDTO.MateriaResponseDTO;
import com.InAula.InAula.ResponseDTO.ProfessorResponseDTO;
import com.InAula.InAula.entity.Aula;
import com.InAula.InAula.entity.Professor;
import com.InAula.InAula.exception.ResourceNotFoundException;
import com.InAula.InAula.repository.MateriaRepository;
import com.InAula.InAula.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final MateriaRepository materiaRepository;
    private final PasswordEncoder passwordEncoder;

    // mesmo padrão do aluno onde estão as fotos
    private final String DIRETORIO_FOTOS = "C:\\Users\\Glêisson\\Pictures\\fotosInAula";


    // CRIAR PROFESSOR
    public ProfessorResponseDTO criarProfessor(ProfessorRequestDTO dto, MultipartFile foto) {

        Professor professor = new Professor();
        professor.setNome(dto.nome());
        professor.setEmail(dto.email());
        professor.setSenha(passwordEncoder.encode(dto.senha()));

        //Perfil fixo
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

    // LISTAR PROFESSORES
    public List<ProfessorResponseDTO> listarTodos() {
        return professorRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }


    // BUSCAR POR ID
    public ProfessorResponseDTO buscarPorId(Long id) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Professor não encontrado"));
        return toResponseDTO(professor);
    }




    // DELETAR PROFESSOR
    public void deletarProfessor(Long id) {

        if (!professorRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Professor não encontrado para exclusão");
        }

        professorRepository.deleteById(id);
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


    // LISTAR MATÉRIAS DO PROFESSOR
    public List<MateriaResponseDTO> listarMateriasDoProfessor(Long professorId) {

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Professor não encontrado"));

        return professor.getMaterias()
                .stream()
                .map(m -> new MateriaResponseDTO(
                        m.getId(),
                        m.getNome(),
                        m.getDescricao()
                ))
                .collect(Collectors.toList());
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

        if (foto != null && !foto.isEmpty()) {
            professor.setFoto(salvarFotoNoDisco(foto));
        }

        Professor atualizado = professorRepository.save(professor);
        return toResponseDTO(atualizado);
    }















}
