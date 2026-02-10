package com.InAula.InAula.controller;

import com.InAula.InAula.RequestDTO.AlunoRequestDTO;
import com.InAula.InAula.ResponseDTO.AlunoResponseDTO;
import com.InAula.InAula.entity.Aluno;
import com.InAula.InAula.entity.Aula;
import com.InAula.InAula.service.materia.aluno.AlunoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/alunos")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    @PostMapping(consumes = {"multipart/form-data"})
    public AlunoResponseDTO criarAluno(
            @Valid
            @RequestPart("aluno") AlunoRequestDTO alunoDTO,
            @RequestPart(value = "foto", required = false) MultipartFile foto
    ) {
        return alunoService.criarAluno(alunoDTO, foto);
    }

    // ==========================
    // 🔐 USUÁRIO LOGADO ( /me )
    // ==========================


    @GetMapping("/me")
    public AlunoResponseDTO alunoLogado(Authentication authentication) {

        // O principal é um Usuario (pai de Aluno)
        String email = authentication.getName();

        // Delegamos tudo para o service
        return alunoService.buscarAlunoLogado(email);
    }


    @PutMapping(
            value = "/me",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public AlunoResponseDTO atualizarMinhaConta(
            Authentication authentication,

            @RequestPart("aluno") AlunoRequestDTO alunoDTO,
            @RequestPart(value = "foto", required = false) MultipartFile foto
    ) {

        Aluno aluno = (Aluno) authentication.getPrincipal();

        return alunoService.atualizarAluno(
                aluno.getId(),
                alunoDTO,
                foto
        );
    }


    @DeleteMapping("/me")
    public void deletarMinhaConta(Authentication authentication) {
        Aluno aluno = (Aluno) authentication.getPrincipal();
        alunoService.deletarAluno(aluno.getId());
    }

}
