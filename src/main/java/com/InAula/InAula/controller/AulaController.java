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


    // Criar aula (professor logado)
    @PostMapping("/professor/me")
    public ResponseEntity<AulaResponseDTO> criarAulaProfessorLogado(
            @Valid @RequestBody AulaRequestDTO dto) {

        AulaResponseDTO response =
                aulaService.salvarAulaProfessorLogado(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Listar aulas do professor logado (paginado)
    @GetMapping("/professor/me")
    public ResponseEntity<Page<AulaResponseDTO>> listarAulasProfessorLogado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        return ResponseEntity.ok(
                aulaService.listarAulasProfessorLogado(page, size));
    }

    // Buscar aula do professor logado por ID
    @GetMapping("/professor/me/{id}")
    public ResponseEntity<AulaResponseDTO> buscarAulaProfessorLogadoPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                aulaService.buscarAulaProfessorLogadoPorId(id));
    }

    // Atualizar aula do professor logado
    @PutMapping("/professor/me/{id}")
    public ResponseEntity<AulaResponseDTO> atualizarAulaProfessorLogado(
            @PathVariable Long id,
            @RequestBody AulaRequestDTO dto) {

        return ResponseEntity.ok(
                aulaService.atualizarAulaProfessorLogado(id, dto));
    }

    // Deletar aula do professor logado
    @DeleteMapping("/professor/me/{id}")
    public ResponseEntity<Void> deletarAulaProfessorLogado(
            @PathVariable Long id) {

        aulaService.deletarAulaProfessorLogado(id);
        return ResponseEntity.noContent().build();
    }



}
