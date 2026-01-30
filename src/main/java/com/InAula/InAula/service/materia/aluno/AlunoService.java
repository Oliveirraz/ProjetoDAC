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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlunoService {

    // Repository é responsável por acesso ao banco
    private final AlunoRepository alunoRepository;

    // Usado para criptografar senha (padrão da apostila)
    private final PasswordEncoder passwordEncoder;

    // Diretório físico para salvar fotos
    private static final String DIRETORIO_FOTOS =
            "C:\\Users\\Glêisson\\Pictures\\fotosInAula";


    public AlunoResponseDTO criarAluno(AlunoRequestDTO alunoDTO, MultipartFile foto) {

        // Regra de negócio: email único
        if (alunoRepository.findByEmail(alunoDTO.email()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        Aluno aluno = new Aluno();
        aluno.setNome(alunoDTO.nome());
        aluno.setEmail(alunoDTO.email());

        aluno.setSenha(passwordEncoder.encode(alunoDTO.senha()));

        // Upload de foto é opcional
        if (foto != null && !foto.isEmpty()) {
            aluno.setFoto(salvarFotoNoDiscoSeguro(foto));
        }

        Aluno salvo = alunoRepository.save(aluno);

        return toResponseDTO(salvo);
    }


    private String salvarFotoNoDiscoSeguro(MultipartFile foto) {

        try {
            String nomeArquivo =
                    System.currentTimeMillis() + "-" + foto.getOriginalFilename();

            Files.createDirectories(Paths.get(DIRETORIO_FOTOS));

            Path caminho = Paths.get(DIRETORIO_FOTOS, nomeArquivo);

            Files.copy(
                    foto.getInputStream(),
                    caminho,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return nomeArquivo;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar foto do aluno");
        }
    }


    public List<AlunoResponseDTO> listarTodos() {
        return alunoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }


    public AlunoResponseDTO buscarPorId(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Aluno não encontrado")
                );

        return toResponseDTO(aluno);
    }


    @Transactional(readOnly = true)
    public AlunoResponseDTO buscarAlunoLogado(String email) {

        Aluno aluno = alunoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        return new AlunoResponseDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getEmail(),
                aluno.getFoto() != null
                        ? "http://localhost:8080/uploads/" + aluno.getFoto()
                        : null,
                aluno.getAulas()
                        .stream()
                        .map(Aula::getId)
                        .toList()
        );
    }


    private AlunoResponseDTO toResponseDTO(Aluno aluno) {

        // Retorna apenas IDs das aulas
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


    public AlunoResponseDTO atualizarAluno(
            Long id,
            AlunoRequestDTO alunoDTO,
            MultipartFile foto
    ) {

        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Aluno não encontrado")
                );

        if (alunoDTO.nome() != null) {
            aluno.setNome(alunoDTO.nome());
        }

        if (alunoDTO.email() != null) {
            aluno.setEmail(alunoDTO.email());
        }

        if (alunoDTO.senha() != null && !alunoDTO.senha().isBlank()) {
            aluno.setSenha(passwordEncoder.encode(alunoDTO.senha()));
        }

        if (foto != null && !foto.isEmpty()) {
            aluno.setFoto(salvarFotoNoDiscoSeguro(foto));
        }

        Aluno atualizado = alunoRepository.save(aluno);

        return toResponseDTO(atualizado);
    }



    @Transactional
    public void deletarAluno(Long id) {

        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Aluno não encontrado")
                );

        // Remove vínculo com aulas
        if (aluno.getAulas() != null) {
            for (Aula aula : aluno.getAulas()) {
                aula.getAlunos().remove(aluno);
            }
            aluno.getAulas().clear();
        }

        alunoRepository.delete(aluno);
    }
}

