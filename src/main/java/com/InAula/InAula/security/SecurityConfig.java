package com.InAula.InAula.security;

import com.InAula.InAula.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter){
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(org.springframework.security.config.Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // ENDPOINTS PÚBLICOS

                        // Login (gera JWT)
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/professores/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/alunos/login").permitAll()

                        // Cadastro inicial
                        .requestMatchers(HttpMethod.POST, "/professores").permitAll()
                        .requestMatchers(HttpMethod.POST, "/alunos").permitAll()

                        // PROFESSOR (PRÓPRIA CONTA)
                        .requestMatchers(HttpMethod.GET, "/professores/me").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.PUT, "/professores/me").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.DELETE, "/professores/me").hasRole("PROFESSOR")

                        // ALUNO (PRÓPRIA CONTA)
                        .requestMatchers(HttpMethod.GET, "/alunos/me").hasRole("ALUNO")
                        .requestMatchers(HttpMethod.PUT, "/alunos/me").hasRole("ALUNO")
                        .requestMatchers(HttpMethod.DELETE, "/alunos/me").hasRole("ALUNO")

                        // REGRAS DE NEGÓCIO

                        // Aulas → só professor
                        .requestMatchers(HttpMethod.POST, "/aulas").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.PUT, "/aulas/**").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.DELETE, "/aulas/**").hasRole("PROFESSOR")

                        // Matérias → só professor
                        .requestMatchers(HttpMethod.POST, "/materias").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.PUT, "/materias/**").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.DELETE, "/materias/**").hasRole("PROFESSOR")

                        // Qualquer outra rota exige login
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }



}
