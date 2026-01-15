package com.InAula.InAula.controller;

import com.InAula.InAula.RequestDTO.ProfessorRequestDTO;
import com.InAula.InAula.ResponseDTO.MateriaResponseDTO;
import com.InAula.InAula.ResponseDTO.ProfessorResponseDTO;
import com.InAula.InAula.service.materia.professor.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/professores")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    // cria aluno com foto
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProfessorResponseDTO criar(
            @Valid
            @RequestPart("professor") ProfessorRequestDTO dto,
            @RequestPart(value = "foto", required = false) MultipartFile foto
    ) {
        return professorService.criarProfessor(dto, foto);
    }

    // lista todos os alunos
    @GetMapping
    public List<ProfessorResponseDTO> listarTodos() {
        return professorService.listarTodos();
    }

    // busca aluno
    @GetMapping("/{id}")
    public ProfessorResponseDTO buscarPorId(@PathVariable Long id) {
        return professorService.buscarPorId(id);
    }

    // atualiza aluno
    @PutMapping("/{id}")
    public ProfessorResponseDTO atualizar(
            @PathVariable Long id,
            @RequestBody ProfessorRequestDTO dto
    ) {
        return professorService.atualizarProfessor(id, dto);
    }

    // deletar
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        professorService.deletarProfessor(id);
    }

    // login
    @PostMapping("/login")
    public ProfessorResponseDTO login( @RequestBody ProfessorRequestDTO dto) {
        return professorService.login(dto.email(), dto.senha());
    }

    @GetMapping("/{id}/materias")
    public List<MateriaResponseDTO> listarMateriasDoProfessor(@PathVariable Long id) {
        return professorService.listarMateriasDoProfessor(id);
    }

}
