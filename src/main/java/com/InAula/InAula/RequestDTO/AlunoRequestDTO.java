package com.InAula.InAula.RequestDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

//O Request representa o formato dos dados que a minha API espera receber.
//Ela contém somente os campos necessários para o cadastro.
public record AlunoRequestDTO(

        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String nome,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 3, message = "A senha deve ter no mínimo 3 caracteres")
        String senha,

        List<Long> materiasIDs,

        String foto) {

}
