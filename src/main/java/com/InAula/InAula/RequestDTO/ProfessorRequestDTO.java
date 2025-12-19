package com.InAula.InAula.RequestDTO;

import java.math.BigDecimal;
import java.util.List;

public record ProfessorRequestDTO(String nome, String email, String senha, String perfil, BigDecimal valorHoraAula, List<Long> materiasIds, String foto) {
}
