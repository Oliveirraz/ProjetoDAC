package com.InAula.InAula.ResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public record ProfessorResponseDTO(Long id, String nome, String email, String perfil, BigDecimal valorHoraAula, List<MateriaResponseDTO> materias, List<Long> aulasIds, String foto) {
}
