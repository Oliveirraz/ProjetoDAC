package com.InAula.InAula.ResponseDTO;

import com.InAula.InAula.entity.Aluno;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

//O ResponseDTO -> serve para trafegar os dados de forma segura, sem expor toda a entidade JPA.
//Posso usar ela no controller, service dentro da minha aplicação.
public record AlunoResponseDTO(Long id, String nome, String email, String foto, List<Long> aulasId) {

}
