package com.InAula.InAula.RequestDTO;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record ProfessorRequestDTO(

        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String nome,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 3, message = "A senha deve ter no mínimo 3 caracteres")
        String senha,

        String perfil,

        @PositiveOrZero(message = "O valor da hora aula deve ser positivo")
        BigDecimal valorHoraAula,

        @NotNull(message = "O professor deve ter pelo menos uma matéria")
        @Size(min = 1, message = "O professor deve estar vinculado a pelo menos uma matéria")
        List<Long> materiasIds,

        String foto) {
}
