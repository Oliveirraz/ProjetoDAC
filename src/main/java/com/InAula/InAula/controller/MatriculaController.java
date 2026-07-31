package com.InAula.InAula.controller;

import com.InAula.InAula.service.matricula.MatriculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matriculas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService matriculaService;

    // Aluno logado solicita matrícula (substitui o antigo /aulas/{id}/matricular)
    @PostMapping("/aulas/{aulaId}/solicitar")
    public ResponseEntity<String> solicitar(@PathVariable Long aulaId) {
        matriculaService.solicitarMatricula(aulaId);
        return ResponseEntity.ok("Solicitação enviada! Aguarde a aprovação do professor.");
    }

    // Link clicado pelo professor no e-mail — público, sem autenticação
    @GetMapping(value = "/responder", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> responder(
            @RequestParam String token,
            @RequestParam boolean aceitar) {

        String resultado = matriculaService.responderMatricula(token, aceitar);

        String html = """
                <html><body style="font-family: sans-serif; text-align:center; padding-top:60px;">
                <h2>EnAula</h2>
                <p>%s</p>
                </body></html>
                """.formatted(resultado);

        return ResponseEntity.ok(html);
    }

    // Aluno logado cancela a própria matrícula (aceita) em uma aula
    @DeleteMapping("/aulas/{aulaId}/cancelar")
    public ResponseEntity<String> cancelar(@PathVariable Long aulaId) {
        matriculaService.cancelarMatricula(aulaId);
        return ResponseEntity.ok("Matrícula cancelada com sucesso. O professor foi notificado por e-mail.");
    }
}