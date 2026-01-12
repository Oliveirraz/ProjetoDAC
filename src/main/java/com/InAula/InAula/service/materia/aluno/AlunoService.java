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

    // Repository acessa o banco, Service NÃO deve usar EntityManager direto
    private final AlunoRepository alunoRepository;
    private final MateriaRepository materiaRepository;

    // Caminho físico onde as fotos serão salvas
    private static final String DIRETORIO_FOTOS =
            "C:\\Users\\Glêisson\\Pictures\\fotosInAula";

    // CRIAR ALUNO
    public AlunoResponseDTO criarAluno(AlunoRequestDTO alunoDTO, MultipartFile foto) {

        //  Regra de negócio: Email não pode ser duplicado
        if (alunoRepository.findByEmail(alunoDTO.email()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        Aluno aluno = new Aluno();
        aluno.setNome(alunoDTO.nome());
        aluno.setEmail(alunoDTO.email());
        aluno.setSenha(alunoDTO.senha()); // Lembrar de Criptografar a SENHA

        // Associa matérias (se vierem IDs)
        if (alunoDTO.materiasIDs() != null && !alunoDTO.materiasIDs().isEmpty()) {
            List<Materia> materias = materiaRepository.findAllById(alunoDTO.materiasIDs());
            aluno.setMaterias(materias);
        }

        // Upload de foto é opcional
        if (foto != null && !foto.isEmpty()) {
            aluno.setFoto(salvarFotoNoDiscoSeguro(foto));
        }

        // Persistência
        Aluno salvo = alunoRepository.save(aluno);

        // Nunca retorne Entity para o frontend
        return toResponseDTO(salvo);
    }


    // SALVAR FOTO (CAMADA DE INFRAESTRUTURA)
    private String salvarFotoNoDiscoSeguro(MultipartFile foto) {

        try {
            // Nome único evita sobrescrita
            String nomeArquivo = System.currentTimeMillis() + "-" + foto.getOriginalFilename();

            // Garante que o diretório exista
            Files.createDirectories(Paths.get(DIRETORIO_FOTOS));

            // Caminho final
            Path caminho = Paths.get(DIRETORIO_FOTOS, nomeArquivo);

            // Salva o arquivo
            Files.copy(foto.getInputStream(), caminho, StandardCopyOption.REPLACE_EXISTING);

            return nomeArquivo;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar foto do aluno");
        }
    }

    // LISTAR TODOS
    public List<AlunoResponseDTO> listarTodos() {
        return alunoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // BUSCAR POR ID
    public AlunoResponseDTO buscarPorId(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));

        return toResponseDTO(aluno);
    }

    // CONVERSÃO ENTITY → DTO
    private AlunoResponseDTO toResponseDTO(Aluno aluno) {

        // Evita LazyException e controla o JSON
        List<MateriaResponseDTO> materias = aluno.getMaterias()
                .stream()
                .map(m -> new MateriaResponseDTO(
                        m.getId(),
                        m.getNome(),
                        m.getDescricao()
                ))
                .collect(Collectors.toList());

        // Retorna apenas IDs das aulas
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

    // ATUALIZAR ALUNO (PATCH)
    public AlunoResponseDTO atualizarAluno(Long id, AlunoRequestDTO alunoDTO) {

        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));

        // Atualização parcial (PATCH-like)
        if (alunoDTO.nome() != null) {
            aluno.setNome(alunoDTO.nome());
        }

        if (alunoDTO.email() != null) {
            aluno.setEmail(alunoDTO.email());
        }

        if (alunoDTO.senha() != null && !alunoDTO.senha().isBlank()) {
            aluno.setSenha(alunoDTO.senha());
        }

        // Atualiza vínculo com matérias
        if (alunoDTO.materiasIDs() != null) {
            if (alunoDTO.materiasIDs().isEmpty()) {
                aluno.getMaterias().clear();
            } else {
                aluno.setMaterias(
                        materiaRepository.findAllById(alunoDTO.materiasIDs())
                );
            }
        }

        return toResponseDTO(alunoRepository.save(aluno));
    }


    // DELETAR ALUNO E Remove vínculos com aulas
    @Transactional
    public void deletarAluno(Long id) {

        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aluno não encontrado para exclusão"
                ));

        // Remove vínculos com aulas (lado dono)
        if (aluno.getAulas() != null) {
            for (Aula aula : aluno.getAulas()) {
                aula.getAlunos().remove(aluno);
            }
            aluno.getAulas().clear();
        }

        //Remove vínculo com matérias
        if (aluno.getMaterias() != null) {
            aluno.getMaterias().clear();
        }

        alunoRepository.delete(aluno);
    }

    //  LOGIN - COM AUTENTICAÇÃO SIMPLES
    public AlunoResponseDTO login(LoginRequestDTO dto) {

        Aluno aluno = alunoRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email não encontrado"));

        // Comparação simples - em produção: senha criptografada
        if (!aluno.getSenha().equals(dto.getSenha())) {
            throw new IllegalArgumentException("Senha incorreta");
        }

        return toResponseDTO(aluno);
    }
}
