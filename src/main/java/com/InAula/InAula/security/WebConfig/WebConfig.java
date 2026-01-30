package com.InAula.InAula.security.WebConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                // Aplica o CORS para TODAS as rotas da API

                .allowedOrigins("http://localhost:5173")
                // Endereço do frontend React (Vite)

                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // Métodos HTTP permitidos

                .allowedHeaders("Authorization", "Content-Type")
                // Headers que o frontend pode ENVIAR

                .exposedHeaders("Authorization");
    }
}