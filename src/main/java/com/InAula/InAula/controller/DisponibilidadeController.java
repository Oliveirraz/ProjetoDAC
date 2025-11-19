package com.InAula.InAula.controller;

import com.InAula.InAula.RequestDTO.DisponibilidadeRequestDTO;
import com.InAula.InAula.ResponseDTO.DisponibilidadeResponseDTO;
import com.InAula.InAula.service.materia.disponibilidade.DisponibilidadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/disponibilidades")

public class DisponibilidadeController {
    private final DisponibilidadeService disponibilidadeService;


    @PostMapping
    public ResponseEntity<DisponibilidadeResponseDTO> salvarDisponibilidade(
            @RequestBody DisponibilidadeRequestDTO dto) {
        DisponibilidadeResponseDTO response = disponibilidadeService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisponibilidadeResponseDTO> buscarPorID(@PathVariable Long id) {
        DisponibilidadeResponseDTO response = disponibilidadeService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DisponibilidadeResponseDTO>> buscarTodos(){
        return ResponseEntity.ok(disponibilidadeService.buscarTodos());

    }

    @PutMapping("/{id}")
    public ResponseEntity<DisponibilidadeResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody DisponibilidadeRequestDTO dto) {

        DisponibilidadeResponseDTO response = disponibilidadeService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        disponibilidadeService.deletar(id);
        return ResponseEntity.noContent().build();
    }


}
