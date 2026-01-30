package com.InAula.InAula.controller;

import com.InAula.InAula.RequestDTO.ProfessorRequestDTO;
import com.InAula.InAula.ResponseDTO.MateriaResponseDTO;
import com.InAula.InAula.ResponseDTO.ProfessorResponseDTO;
import com.InAula.InAula.entity.Professor;
import com.InAula.InAula.service.materia.professor.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/professores")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    // ==========================
    // 🔐 PROFESSOR LOGADO ( /me )
    // ==========================

   @GetMapping("/me")
    public ProfessorResponseDTO professorLogado(Authentication authentication) {

        Professor professor = (Professor) authentication.getPrincipal();

        return new ProfessorResponseDTO(
                professor.getId(),
                professor.getNome(),
                professor.getEmail(),
                professor.getPerfil(),
                professor.getValorHoraAula(),
                null,
                null,
                professor.getFoto() != null
                        ? "http://localhost:8080/uploads/" + professor.getFoto()
                        : null
        );
    }

    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProfessorResponseDTO atualizarMinhaConta(
            Authentication authentication,
            @RequestPart("professor") ProfessorRequestDTO dto,
            @RequestPart(value = "foto", required = false) MultipartFile foto
    ) throws IOException {

        Long id = ((Professor) authentication.getPrincipal()).getId();
        return professorService.atualizarProfessor(id, dto, foto);
    }




    @DeleteMapping("/me")
    public void deletarMinhaConta(Authentication authentication) {
        Professor professor = (Professor) authentication.getPrincipal();
        professorService.deletarProfessor(professor.getId());
    }

    // ==========================
    // 🔓 CADASTRO / 🔒 ADMIN
    // ==========================

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProfessorResponseDTO criar(
            @Valid
            @RequestPart("professor") ProfessorRequestDTO dto,
            @RequestPart(value = "foto", required = false) MultipartFile foto
    ) {
        return professorService.criarProfessor(dto, foto);
    }


}
