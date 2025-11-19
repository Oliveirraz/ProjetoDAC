package com.InAula.InAula.controller;

import com.InAula.InAula.RequestDTO.AlunoRequestDTO;
import com.InAula.InAula.ResponseDTO.AlunoResponseDTO;
import com.InAula.InAula.service.materia.aluno.AlunoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("alunos")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    @PostMapping
    public AlunoResponseDTO criarAluno(@RequestBody AlunoRequestDTO alunoDTO){
        return alunoService.criarAluno(alunoDTO);
    }

    @GetMapping
    public List<AlunoResponseDTO> listarTodosAlunos(){
        return alunoService.listarTodos();
    }

    @GetMapping("{id}")
    public AlunoResponseDTO buscarPorId(@PathVariable Long id){
        return alunoService.buscarPorId(id);
    }

    @PutMapping("{id}")
    public  AlunoResponseDTO atualizarAluno(@PathVariable Long id, @RequestBody AlunoRequestDTO alunoRequestDTO){
        return alunoService.atualizarAluno(id, alunoRequestDTO);
    }

    @DeleteMapping("{id}")
    public void deletarAluno(@PathVariable Long id){
        alunoService.deletarAluno(id);
    }
}
