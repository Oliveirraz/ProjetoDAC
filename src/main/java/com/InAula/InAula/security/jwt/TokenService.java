package com.InAula.InAula.security.jwt;

import com.InAula.InAula.entity.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    private static final String SECRET = "enAula-secret-key-2026";

    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);

            return JWT.create()
                    .withSubject(usuario.getEmail())

                    .withClaim(
                            "roles",
                            usuario.getAuthorities()
                                    .stream()
                                    .map(authority -> authority.getAuthority())
                                    .toList()
                    )

                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algorithm);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar token JWT", e);
        }
    }


    public String validarToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);

            return JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException e){
            return null;
        }
    }

    private Instant gerarDataExpiracao(){
        return LocalDateTime.now()
                .plusHours(2)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}
