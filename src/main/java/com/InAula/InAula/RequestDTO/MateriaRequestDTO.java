package com.InAula.InAula.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MateriaRequestDTO(
        @NotBlank(message = "O nome da matéria é obrigatório")
        @Size(max = 100, message = "O nome da matéria deve ter no máximo 100 caracteres")
        String nome,
        String descricao,
        Long professorId) {
}
