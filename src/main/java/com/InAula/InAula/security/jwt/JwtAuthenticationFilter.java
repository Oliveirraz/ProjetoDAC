package com.InAula.InAula.security.jwt;

import com.InAula.InAula.entity.Professor;
import com.InAula.InAula.repository.ProfessorRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    private final ProfessorRepository professorRepository;

    public JwtAuthenticationFilter (TokenService tokenService, ProfessorRepository professorRepository){
        this.tokenService = tokenService;
        this.professorRepository = professorRepository;
    }


    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")){
            String token = authorizationHeader.replace("Bearer ", "");
            String email = tokenService.validarToken(token);

            if (email != null){
                Professor professor = professorRepository.findByEmail(email).orElse(null);

                if (professor != null){
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    professor,
                                    null,
                                    List.of()
                            );
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authenticationToken);
                }
            }

        }
        filterChain.doFilter(request, response);

    }

}
