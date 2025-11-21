package com.InAula.InAula.service.materia.aluno;

import com.InAula.InAula.RequestDTO.AlunoRequestDTO;
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

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class AlunoService {

    //Cria a injeção de dependencia, o spring me da uma instância pronta ara usar, aqui dentro.
    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    //Esse método recebe meu AlunoRequestDTO que o objeto enviado pelo cliente atraves do insomnia.
    public AlunoResponseDTO criarAluno(AlunoRequestDTO alunoDTO){
        Aluno aluno = new Aluno();// Crio objeto aluno vazio e depois cada campo do meu DTO é copiado para Aluno.
        aluno.setNome(alunoDTO.nome());
        aluno.setEmail(alunoDTO.email());
        aluno.setSenha(alunoDTO.senha());

        //Verifico se a minha lista de materia não é nula e nem vazia
        if (alunoDTO.materiasIDs() != null && !alunoDTO.materiasIDs().isEmpty()){
            List<Materia> materias = materiaRepository.findAllById(alunoDTO.materiasIDs());// faz a busca de todas as materias com ids enviado no meu DTO.
            aluno.setMaterias(materias);//Associa as materias ao Aluno
        }
        Aluno alunoSalvo = alunoRepository.save(aluno);// salvo no meu banco
        return toResponseDTO(alunoSalvo);//Retorna e Monta meu DTO
    }

    public List<AlunoResponseDTO> listarTodos() {
        return alunoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public AlunoResponseDTO buscarPorId(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        return toResponseDTO(aluno);
    }

    //Método para converter Aluno em AlunoDTO.
    private AlunoResponseDTO toResponseDTO(Aluno aluno) {
        // Converte as matérias do aluno em DTOs
        List<MateriaResponseDTO> materias = aluno.getMaterias()
                .stream()//Posso processsar a lista funcionalmente.
                .map(m -> new MateriaResponseDTO( // Para cada matera m do stream, cria um novo MateriaResponseDTO
                        m.getId(),
                        m.getNome(),
                        m.getDescricao()
                ))
                .collect(Collectors.toList()); //função é juntar todas as MateriaResponseDTO em uma lista de MateriaResponseDTO

        // Pega apenas os IDs das aulas
        List<Long> aulasIds = aluno.getAulas()// obter a lista de Aula associadas ao aluno.
                .stream()//Cria um stream para percorrer a lista
                .map(Aula::getId)//Para cada Aula, aplico um getId e transforma o item do stream no meu Integer ID
                .collect(Collectors.toList());// Coleto todos os IDs para uma List<Integer> chamada aulasIDs
        //Retono o meu AlunoResponseDTO criado com os campos preenchidos.
        return new AlunoResponseDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getEmail(),
                materias,
                aulasIds
        );
    }

    public AlunoResponseDTO atualizarAluno(Long id, AlunoRequestDTO alunoDTO) {
        Aluno aluno = alunoRepository.findById(id)//busca por id do aluno, caso não encontre gera minha exceção.
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));

        //Atualizo os dados basicos do aluno.
        aluno.setNome(alunoDTO.nome());
        aluno.setEmail(alunoDTO.email());
        aluno.setSenha(alunoDTO.senha());

        //Caso a requisição tenha Ids de materia
        if (alunoDTO.materiasIDs() != null && !alunoDTO.materiasIDs().isEmpty()) {
            List<Materia> materias = materiaRepository.findAllById(alunoDTO.materiasIDs());
            aluno.setMaterias(materias);
            //verifica se existe a lista de IDs de materias, busca no banco com FindAllById e atualiza as materias com as novas que foram enviadas
        } else {
            aluno.getMaterias().clear();
            // Caso a requisição não enviar materias, remove todas as materias do aluno.
        }

        Aluno atualizado = alunoRepository.save(aluno);
        //Salvo as alterações e retorno o Aluno
        return toResponseDTO(atualizado);
    }

    public void deletarAluno(Long id) {
        if (!alunoRepository.existsById(id)) {
            throw new RuntimeException("Aluno não encontrado para exclusão");
        }
        alunoRepository.deleteById(id);
    }

}
