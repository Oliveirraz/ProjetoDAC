package com.InAula.InAula.controller;

import com.InAula.InAula.RequestDTO.MateriaRequestDTO;
import com.InAula.InAula.ResponseDTO.MateriaResponseDTO;
import com.InAula.InAula.service.materia.MateriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("materias")
@CrossOrigin(origins = "*")
public class MateriaController {

    @Autowired
    private MateriaService materiaService;



    // Criar matéria para o professor logado
    @PostMapping("/professor/me")
    public MateriaResponseDTO criarMateriaProfessorLogado(
            @Valid @RequestBody MateriaRequestDTO dto) {

        return materiaService.criarMateriaProfessorLogado(dto);
    }

    // Listar matérias do professor logado
    @GetMapping("/professor/me")
    public List<MateriaResponseDTO> listarMateriasProfessorLogado() {
        return materiaService.listarMateriasProfessorLogado();
    }

    // Atualizar matéria do professor logado
    @PutMapping("/professor/me/{id}")
    public MateriaResponseDTO atualizarMateriaProfessorLogado(
            @PathVariable Long id,
            @RequestBody MateriaRequestDTO dto) {

        return materiaService.atualizarMateriaProfessorLogado(id, dto);
    }

    // Deletar matéria do professor logado
    @DeleteMapping("/professor/me/{id}")
    public void deletarMateriaProfessorLogado(@PathVariable Long id) {
        materiaService.deletarMateriaProfessorLogado(id);
    }
}
