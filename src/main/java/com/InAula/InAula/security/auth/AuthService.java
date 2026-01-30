package com.InAula.InAula.security.auth;

import com.InAula.InAula.repository.AlunoRepository;
import com.InAula.InAula.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return professorRepository.findByEmail(email)
                .map(professor -> (UserDetails) professor)
                .orElseGet(() -> alunoRepository.findByEmail(email).orElseThrow(()
                -> new UsernameNotFoundException("Usuário não encontrado")));
    }
}
