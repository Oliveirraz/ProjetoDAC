package com.InAula.InAula.RequestDTO;

import java.util.List;

//O Request representa o formato dos dados que a minha API espera receber.
//Ela contém somente os campos necessários para o cadastro.
public record AlunoRequestDTO(String nome, String email, String senha, List<Long> materiasIDs, String foto) {

}
