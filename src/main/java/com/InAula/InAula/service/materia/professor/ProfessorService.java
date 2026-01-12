package com.InAula.InAula.service.materia.professor;

import com.InAula.InAula.RequestDTO.ProfessorRequestDTO;
import com.InAula.InAula.ResponseDTO.MateriaResponseDTO;
import com.InAula.InAula.ResponseDTO.ProfessorResponseDTO;
import com.InAula.InAula.entity.Aula;
import com.InAula.InAula.entity.Materia;
import com.InAula.InAula.entity.Professor;
import com.InAula.InAula.exception.ResourceNotFoundException;
import com.InAula.InAula.repository.MateriaRepository;
import com.InAula.InAula.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
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

    // mesmo padrão do aluno onde estão as fotos
    private final String DIRETORIO_FOTOS = "C:\\Users\\Glêisson\\Pictures\\fotosInAula";


    // CRIAR PROFESSOR
    public ProfessorResponseDTO criarProfessor(ProfessorRequestDTO dto, MultipartFile foto) {

        Professor professor = new Professor();
        professor.setNome(dto.nome());
        professor.setEmail(dto.email());
        professor.setSenha(dto.senha());

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


    // ATUALIZAR PROFESSOR
    public ProfessorResponseDTO atualizarProfessor(Long id, ProfessorRequestDTO dto) {

        Professor professor = professorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Professor não encontrado"));

        if (dto.nome() != null) professor.setNome(dto.nome());
        if (dto.email() != null) professor.setEmail(dto.email());
        if (dto.senha() != null && !dto.senha().isBlank()) professor.setSenha(dto.senha());

        // Proteção simples de regra
        if (dto.perfil() != null && !dto.perfil().equalsIgnoreCase("Professor")) {
            throw new IllegalArgumentException(
                    "Perfil inválido. Professor não pode ter outro perfil.");
        }

        if (dto.valorHoraAula() != null) professor.setValorHoraAula(dto.valorHoraAula());
        if (dto.foto() != null) professor.setFoto(dto.foto());

        if (dto.materiasIds() != null) {
            if (!dto.materiasIds().isEmpty()) {
                List<Materia> materias = materiaRepository.findAllById(dto.materiasIds());
                professor.setMaterias(materias);
            } else {
                professor.getMaterias().clear();
            }
        }

        Professor atualizado = professorRepository.save(professor);
        return toResponseDTO(atualizado);
    }


    // DELETAR PROFESSOR
    public void deletarProfessor(Long id) {

        if (!professorRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Professor não encontrado para exclusão");
        }

        professorRepository.deleteById(id);
    }

    // LOGIN
    public ProfessorResponseDTO login(String email, String senha) {

        Professor professor = professorRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Email não encontrado"));

        if (!professor.getSenha().equals(senha)) {
            throw new IllegalArgumentException("Senha incorreta");
        }

        return toResponseDTO(professor);
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
}
