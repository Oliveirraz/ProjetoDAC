package com.InAula.InAula.service.email;

import com.InAula.InAula.entity.Matricula;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.backend.url}")
    private String backendUrl;

    @Value("${spring.mail.username}")
    private String remetente;

    public void enviarSolicitacaoParaProfessor(Matricula matricula) {

        var aula = matricula.getAula();
        var professor = aula.getProfessor();
        var aluno = matricula.getAluno();

        String linkAceitar = backendUrl + "/api/matriculas/responder?token="
                + matricula.getToken() + "&aceitar=true";

        String linkRecusar = backendUrl + "/api/matriculas/responder?token="
                + matricula.getToken() + "&aceitar=false";

        String texto = """
                Olá, %s!

                O aluno %s deseja se matricular na sua aula de %s.

                Detalhes da aula:

                Data: %s
                Horário: %s às %s
                Local: %s
                Valor: R$ %s

                Para ACEITAR:
                %s

                Para RECUSAR:
                %s
                """.formatted(
                professor.getNome(),
                aluno.getNome(),
                aula.getMateria().getNome(),
                aula.getData(),
                aula.getHoraInicio(),
                aula.getHoraFim(),
                aula.getLocal(),
                aula.getValorHora(),
                linkAceitar,
                linkRecusar
        );

        enviar(professor.getEmail(), "EnAula - Nova solicitação de matrícula", texto);
    }

    public void enviarConfirmacaoParaAluno(Matricula matricula) {

        var aula = matricula.getAula();
        var aluno = matricula.getAluno();
        var professor = aula.getProfessor();

        String texto = """
                Olá, %s!

                Sua matrícula foi ACEITA pelo professor.

                Matéria: %s
                Descrição: %s
                Professor: %s
                Data: %s
                Horário: %s às %s
                Local: %s
                Valor: R$ %s

                Bons estudos!
                """.formatted(
                aluno.getNome(),
                aula.getMateria().getNome(),
                aula.getMateria().getDescricao(),
                professor.getNome(),
                aula.getData(),
                aula.getHoraInicio(),
                aula.getHoraFim(),
                aula.getLocal(),
                aula.getValorHora()
        );

        enviar(aluno.getEmail(), "EnAula - Matrícula confirmada!", texto);
    }

    public void enviarRecusaParaAluno(Matricula matricula) {

        var aula = matricula.getAula();
        var aluno = matricula.getAluno();

        String texto = """
                Olá, %s!

                Infelizmente sua solicitação de matrícula na aula de %s não foi aceita pelo professor.

                Você pode buscar outras aulas disponíveis na plataforma.
                """.formatted(
                aluno.getNome(),
                aula.getMateria().getNome()
        );

        enviar(aluno.getEmail(), "EnAula - Atualização da sua matrícula", texto);
    }

    private void enviar(String destinatario, String assunto, String texto) {

        try {

            System.out.println("===================================");
            System.out.println("REMETENTE : " + remetente);
            System.out.println("DESTINO   : " + destinatario);
            System.out.println("ASSUNTO   : " + assunto);
            System.out.println("===================================");

            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, false, "UTF-8");

            // Define explicitamente o remetente
            helper.setFrom(remetente, "EnAula");

            helper.setTo(destinatario);

            helper.setSubject(assunto);

            helper.setText(texto, false);

            mailSender.send(mimeMessage);

            System.out.println("E-mail enviado com sucesso!");

        } catch (Exception e) {

            System.err.println("Erro ao enviar e-mail");
            e.printStackTrace();

            throw new RuntimeException("Erro ao enviar e-mail para " + destinatario, e);
        }
    }
}