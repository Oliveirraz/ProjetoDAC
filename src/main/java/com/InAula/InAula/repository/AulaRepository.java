package com.InAula.InAula.repository;

import com.InAula.InAula.entity.Aula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AulaRepository extends JpaRepository<Aula, Long> {

    // Listar aulas por professor (com paginação)
    Page<Aula> findByProfessor_Id(Long professorId, Pageable pageable);

    // BUSCA + PAGINAÇÃO - Matéria OU Professor
    @Query("""
        SELECT a FROM Aula a
        WHERE 
          (:termo IS NULL OR 
           LOWER(a.materia.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR
           LOWER(a.professor.nome) LIKE LOWER(CONCAT('%', :termo, '%'))
          )
        """)
    Page<Aula> buscarPorMateriaOuProfessor(@Param("termo") String termo, Pageable pageable);

    // MÉTODO — aulas em que o aluno está matriculado
    @Query("""
        SELECT a
        FROM Aula a
        JOIN a.alunos al
        WHERE al.id = :alunoId
    """)
    List<Aula> buscarAulasDoAluno(@Param("alunoId") Long alunoId);
}
