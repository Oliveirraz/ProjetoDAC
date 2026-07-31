package com.InAula.InAula.repository;

import com.InAula.InAula.entity.Matricula;
import com.InAula.InAula.entity.MatriculaStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    Optional<Matricula> findByToken(String token);

    boolean existsByAluno_IdAndAula_IdAndStatusIn(Long alunoId, Long aulaId, List<MatriculaStatus> status);

    Optional<Matricula> findByAluno_IdAndAula_IdAndStatus(Long alunoId, Long aulaId, MatriculaStatus status);

    List<Matricula> findByAula_IdAndStatus(Long aulaId, MatriculaStatus status);

    List<Matricula> findByAula_Id(Long aulaId);

    List<Matricula> findByAluno_Id(Long alunoId);

    List<Matricula> findByAluno_IdAndStatus(Long alunoId, MatriculaStatus status);


}