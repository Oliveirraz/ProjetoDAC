package com.InAula.InAula.controller;

import com.InAula.InAula.RequestDTO.ProfessorRequestDTO;
import com.InAula.InAula.ResponseDTO.ProfessorResponseDTO;
import com.InAula.InAula.service.materia.professor.ProfessorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("professores")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }


    @PostMapping
    public ProfessorResponseDTO criar(@RequestBody ProfessorRequestDTO dto) {
        return professorService.criarProfessor(dto);
    }

    @GetMapping
    public List<ProfessorResponseDTO> listarTodos() {
        return professorService.listarTodos();
    }

    @GetMapping("/{id}")
    public ProfessorResponseDTO buscarPorId(@PathVariable Long id) {
        return professorService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ProfessorResponseDTO atualizar(@PathVariable Long id,
                                          @RequestBody ProfessorRequestDTO dto) {
        return professorService.atualizarProfessor(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        professorService.deletarProfessor(id);
    }


}
