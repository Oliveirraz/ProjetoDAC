package com.InAula.InAula.controller;

import com.InAula.InAula.RequestDTO.AulaRequestDTO;
import com.InAula.InAula.ResponseDTO.AulaResponseDTO;
import com.InAula.InAula.service.materia.aula.AulaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/aulas")
public class AulaController {

    private final AulaService aulaService;

    @PostMapping
    public ResponseEntity<AulaResponseDTO> criarAula(@RequestBody AulaRequestDTO dto) {
        AulaResponseDTO responseDTO = aulaService.salvarAula(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

    }
    @GetMapping("/{id}")
    public ResponseEntity<AulaResponseDTO> buscarPorID(@PathVariable Long id) {
        AulaResponseDTO response = aulaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AulaResponseDTO>> listarTodasAulas() {
        return ResponseEntity.ok(aulaService.listarTodos());
    }
    @PutMapping("/{id}")
    public ResponseEntity<AulaResponseDTO> atualizar(@PathVariable Long id,
                                                     @RequestBody AulaRequestDTO dto) {
        AulaResponseDTO response = aulaService.atualizar(id, dto);
        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAula(@PathVariable Long id) {
        aulaService.deletar(id);
        return ResponseEntity.noContent().build();
    }


}