package com.InAula.InAula.service;

import com.InAula.InAula.repository.MateriaRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Getter
@Setter
@Service
public class MateriaService {
    private final MateriaRepository materiaRepository;


}
