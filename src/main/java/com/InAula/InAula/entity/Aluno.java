package com.InAula.InAula.entity;


import com.InAula.InAula.entity.Aula;
import com.InAula.InAula.entity.Materia;
import com.InAula.InAula.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "Aluno")
@Getter
@Setter
public class Aluno extends Usuario {

    @ManyToMany(mappedBy = "alunos")
    private List<Aula> aulas = new ArrayList<>();

    // Relação Morta pois agora o aluno se relaciona diretamento com aula.
    @ManyToMany
    @JoinTable(
            name = "aluno_materia",
            joinColumns = @JoinColumn(name = "aluno_id"),
            inverseJoinColumns = @JoinColumn(name = "materia_id")
    )
    private List<Materia> materias = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ALUNO"));
    }

}
