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

    // 🌐 CORS (AQUI 👇)
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        var config = new org.springframework.web.cors.CorsConfiguration();

        config.setAllowedOrigins(java.util.List.of("http://localhost:5173"));
        config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(java.util.List.of("*"));
        config.setAllowCredentials(true);

        var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
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

                        // PRE-FLIGHT
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 📸 IMAGENS
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()

                        // LOGIN
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                        // CADASTRO
                        .requestMatchers(HttpMethod.POST, "/api/professores").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/alunos").permitAll()

                        // PROFESSOR
                        .requestMatchers(HttpMethod.GET, "/api/professores/me").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.PUT, "/api/professores/me").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/professores/me").hasRole("PROFESSOR")

                        // ALUNO
                        .requestMatchers(HttpMethod.GET, "/api/alunos/me").hasRole("ALUNO")
                        .requestMatchers(HttpMethod.PUT, "/api/alunos/me").hasRole("ALUNO")
                        .requestMatchers(HttpMethod.DELETE, "/api/alunos/me").hasRole("ALUNO")

                        // AULAS
                        .requestMatchers(HttpMethod.POST, "/api/aulas").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.PUT, "/api/aulas/**").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/aulas/**").hasRole("PROFESSOR")

                        // MATÉRIAS
                        .requestMatchers(HttpMethod.POST, "/api/materias").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.PUT, "/api/materias/**").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/materias/**").hasRole("PROFESSOR")

                        .anyRequest().authenticated()
                )



                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }



}
