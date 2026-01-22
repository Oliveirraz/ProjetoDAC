package com.InAula.InAula.security.auth;

import com.InAula.InAula.RequestDTO.LoginRequestDTO;
import com.InAula.InAula.ResponseDTO.LoginResponseDTO;
import com.InAula.InAula.ResponseDTO.ProfessorResponseDTO;
import com.InAula.InAula.entity.Professor;
import com.InAula.InAula.security.jwt.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager,
                          TokenService tokenService){
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO dto
    ) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getSenha()
                );

        Authentication authentication =
                authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        /*
          Após autenticar, geramos o token
         */
        String token = tokenService.gerarToken(
                (com.InAula.InAula.entity.Usuario) authentication.getPrincipal()
        );

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}

