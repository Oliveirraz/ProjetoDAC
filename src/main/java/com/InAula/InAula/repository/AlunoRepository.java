package com.InAula.InAula.repository;


import com.InAula.InAula.entity.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

//Através da JpaRepository é que tenho acesso aos meus métodos para manipular o objeto, no caso o objeto Aluno.
//<Aluno é a entidade que sera gerenciada e Long é a minha chave primaria>
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
}
