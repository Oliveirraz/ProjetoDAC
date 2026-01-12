package com.InAula.InAula.controller;


import com.InAula.InAula.RequestDTO.MateriaRequestDTO;
import com.InAula.InAula.ResponseDTO.MateriaResponseDTO;
import com.InAula.InAula.service.materia.MateriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("materias")
@CrossOrigin(origins = "*")
public class MateriaController {

    @Autowired
    private MateriaService materiaService;

    @PostMapping
    public MateriaResponseDTO criar(@RequestBody MateriaRequestDTO dto) {
        return materiaService.criar(dto);
    }

    @GetMapping
    public List<MateriaResponseDTO> listarTodos() {
        return materiaService.listarTodos();
    }

    @GetMapping("/{id}")
    public MateriaResponseDTO buscarPorId(@PathVariable Long id) {
        return materiaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public MateriaResponseDTO atualizar(@PathVariable Long id,
                                        @RequestBody MateriaRequestDTO dto) {
        return materiaService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        materiaService.deletar(id);
    }





}
