package com.InAula.InAula.service.materia.aluno;

import com.InAula.InAula.RequestDTO.AlunoRequestDTO;
import com.InAula.InAula.RequestDTO.LoginRequestDTO;
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

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.io.IOException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlunoService {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    // Caminho fixo no Windows
    private final String DIRETORIO_FOTOS = "C:\\Users\\Glêisson\\Pictures\\fotosInAula";

    // Criando aluno com a foto
    public AlunoResponseDTO criarAluno(AlunoRequestDTO alunoDTO, MultipartFile foto) {

        Aluno aluno = new Aluno();
        aluno.setNome(alunoDTO.nome());
        aluno.setEmail(alunoDTO.email());
        aluno.setSenha(alunoDTO.senha());

        // associa matérias
        if (alunoDTO.materiasIDs() != null && !alunoDTO.materiasIDs().isEmpty()) {
            List<Materia> materias = materiaRepository.findAllById(alunoDTO.materiasIDs());
            aluno.setMaterias(materias);
        }

        // salva a foto (SE FOI ENVIADA)
        if (foto != null && !foto.isEmpty()) {
            try {
                String nomeArquivo = salvarFotoNoDisco(foto);
                aluno.setFoto(nomeArquivo); // salva só o nome do arquivo
            } catch (IOException e) {
                throw new RuntimeException("Erro ao salvar foto: " + e.getMessage());
            }
        }

        Aluno alunoSalvo = alunoRepository.save(aluno);
        return toResponseDTO(alunoSalvo);
    }

    // Salvando no meu HD
    private String salvarFotoNoDisco(MultipartFile foto) throws IOException {

        // nome único
        String nomeArquivo = System.currentTimeMillis() + "-" + foto.getOriginalFilename();

        // caminho final
        Path caminho = Paths.get(DIRETORIO_FOTOS, nomeArquivo);

        // cria diretório se não existir
        Files.createDirectories(Paths.get(DIRETORIO_FOTOS));

        // salva o arquivo
        Files.copy(foto.getInputStream(), caminho, StandardCopyOption.REPLACE_EXISTING);

        return nomeArquivo;
    }


    // Lista todos os alunos.
    public List<AlunoResponseDTO> listarTodos() {
        return alunoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Busca por id do Aluno.
    public AlunoResponseDTO buscarPorId(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        return toResponseDTO(aluno);
    }

    // Convete de Aluno para AlunoDTO.
    private AlunoResponseDTO toResponseDTO(Aluno aluno) {

        List<MateriaResponseDTO> materias = aluno.getMaterias()
                .stream()
                .map(m -> new MateriaResponseDTO(
                        m.getId(),
                        m.getNome(),
                        m.getDescricao()
                ))
                .collect(Collectors.toList());

        List<Long> aulasIds = aluno.getAulas()
                .stream()
                .map(Aula::getId)
                .collect(Collectors.toList());

        return new AlunoResponseDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getEmail(),
                aluno.getFoto() != null
                        ? "http://localhost:8080/uploads/" + aluno.getFoto()
                        : null,
                materias,
                aulasIds
        );
    }


    // Atualizo o meu Aluno
    public AlunoResponseDTO atualizarAluno(Long id, AlunoRequestDTO alunoDTO) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));

        // 🔹 Atualização parcial segura
        if (alunoDTO.nome() != null) {
            aluno.setNome(alunoDTO.nome());
        }

        if (alunoDTO.email() != null) {
            aluno.setEmail(alunoDTO.email());
        }

        if (alunoDTO.senha() != null && !alunoDTO.senha().isBlank()) {
            aluno.setSenha(alunoDTO.senha());
        }

        if (alunoDTO.materiasIDs() != null) {
            if (!alunoDTO.materiasIDs().isEmpty()) {
                List<Materia> materias = materiaRepository.findAllById(alunoDTO.materiasIDs());
                aluno.setMaterias(materias);
            } else {
                aluno.getMaterias().clear();
            }
        }

        Aluno atualizado = alunoRepository.save(aluno);
        return toResponseDTO(atualizado);
    }


    // Deleto meu Aluno
    public void deletarAluno(Long id) {
        if (!alunoRepository.existsById(id)) {
            throw new RuntimeException("Aluno não encontrado para exclusão");
        }
        alunoRepository.deleteById(id);
    }

    public AlunoResponseDTO login(LoginRequestDTO dto) {

        Aluno aluno = alunoRepository
                .findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Email não encontrado"));

        if (!aluno.getSenha().equals(dto.getSenha())) {
            throw new RuntimeException("Senha incorreta");
        }

        // ✅ converte entidade → DTO corretamente
        return toResponseDTO(aluno);
    }



}
