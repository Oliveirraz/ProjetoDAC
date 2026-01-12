package com.InAula.InAula.controller;

import com.InAula.InAula.RequestDTO.AulaRequestDTO;
import com.InAula.InAula.ResponseDTO.AulaResponseDTO;
import com.InAula.InAula.service.materia.aula.AulaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/aulas")
public class AulaController {

    private final AulaService aulaService;

    // Criar aula
    @PostMapping
    public ResponseEntity<AulaResponseDTO> criarAula(@Valid @RequestBody AulaRequestDTO dto) {
        AulaResponseDTO responseDTO = aulaService.salvarAula(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    // Buscar aula por ID
    @GetMapping("/{id}")
    public ResponseEntity<AulaResponseDTO> buscarPorId(@PathVariable Long id) {
        AulaResponseDTO response = aulaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    // Listar aulas - paginado e filtrável
    @GetMapping
    public ResponseEntity<Page<AulaResponseDTO>> listarAulas(
            @RequestParam(required = false) Long professorId,
            @RequestParam(required = false) String termo,
            @RequestParam(defaultValue = "0") int page, // Começo na pagina 0
            @RequestParam(defaultValue = "12") int size // Quantidade de Aulas por pagina
    ) {

        Page<AulaResponseDTO> aulasPaginadas; // Cria a variavel com resultado final

        if (professorId != null) {
            // Listagem por professor, paginada
            aulasPaginadas = aulaService.listarPorProfessorPaginado(professorId, page, size);
        } else if (termo != null && !termo.isBlank()) {
            // Busca por termo - matéria ou professor paginada
            aulasPaginadas = aulaService.buscarPorMateriaOuProfessor(termo, page, size);
        } else {
            // Listagem geral, paginada
            aulasPaginadas = aulaService.listarTodosPaginado(page, size);
        }

        return ResponseEntity.ok(aulasPaginadas);
    }


    // Deletar aula
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAula(@PathVariable Long id) {
        aulaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    //Matricula o Aluno
    @PostMapping("/{aulaId}/matricular/{alunoId}")
    public ResponseEntity<AulaResponseDTO> matricularAluno(
            @PathVariable Long aulaId,
            @PathVariable Long alunoId) {

        AulaResponseDTO response = aulaService.matricularAluno(aulaId, alunoId);
        return ResponseEntity.ok(response);
    }

    // Listar aulas do aluno
    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<AulaResponseDTO>> listarAulasDoAluno(
            @PathVariable Long alunoId
    ) {
        return ResponseEntity.ok(
                aulaService.listarAulasDoAluno(alunoId)
        );
    }

    // Testado e Funcionando
    @PutMapping("/{id}")
    public ResponseEntity<AulaResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody AulaRequestDTO dto) {

        System.out.println(" PUT /aulas/" + id);
        System.out.println("DTO: " + dto.getLocal());

        AulaResponseDTO response = aulaService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }




}
