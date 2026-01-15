package com.InAula.InAula.controller;

import com.InAula.InAula.RequestDTO.LoginRequestDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.InAula.InAula.RequestDTO.AlunoRequestDTO;
import com.InAula.InAula.ResponseDTO.AlunoResponseDTO;
import com.InAula.InAula.service.materia.aluno.AlunoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("alunos")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    // Criando o Aluno agora com foto
    @PostMapping(consumes = {"multipart/form-data"})
    public AlunoResponseDTO criarAluno(
            @Valid
            @RequestPart("aluno") AlunoRequestDTO alunoDTO,
            @RequestPart(value = "foto", required = false) MultipartFile foto
    ) {
        return alunoService.criarAluno(alunoDTO, foto);
    }

    // Listo os alunos.
    @GetMapping
    public List<AlunoResponseDTO> listarTodosAlunos() {
        return alunoService.listarTodos();
    }

    // Busco os alunos.
    @GetMapping("{id}")
    public AlunoResponseDTO buscarPorId(@PathVariable Long id) {
        return alunoService.buscarPorId(id);
    }

    // Atualizo o aluno, mas sem atualizar a foto.
    @PutMapping("{id}")
    public AlunoResponseDTO atualizarAluno(
            @PathVariable Long id,
            @RequestBody AlunoRequestDTO alunoRequestDTO
    ) {
        return alunoService.atualizarAluno(id, alunoRequestDTO);
    }

    // Deleto o aluno.
    @DeleteMapping("{id}")
    public void deletarAluno(@PathVariable Long id) {
        alunoService.deletarAluno(id);
    }

    @PostMapping("/login")
    public AlunoResponseDTO login(@Valid @RequestBody LoginRequestDTO loginDTO) {
        return alunoService.login(loginDTO);
    }

}
