package com.edsof.anotacoes.business.service;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailConfirmacao(String destino, String nomeUsuario, String token) throws MessagingException {
        String link = "http://localhost:5173/auth/confirmar-cadastro?token=" + token;

        String corpoEmail = "<p>Olá, <strong>" + nomeUsuario + "</strong>,</p>"
                + "<p>Bem-vindo! Clique no link abaixo para confirmar seu cadastro e definir sua senha pessoal:</p>"
                + "<p><a href=\"" + link + "\">Confirmar cadastro</a></p>";

        MimeMessage mimeMessage  = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(destino);
        helper.setSubject("Confirmação de Cadastro");
        helper.setText(corpoEmail, true); // true = envia como HTML

        mailSender.send(mimeMessage);
    }

    public void enviarEmailAlteracao(String destino, String nomeUsuario, String token) throws MessagingException {
        String link = "http://localhost:5173/auth/alterar-senha?token=" + token;

        String corpoEmail = "<p>Olá, <strong>" + nomeUsuario + "</strong>, acesse o link abaixo para redefinir sua senha.</p>"
                + "<p><a href=\"" + link + "\">" + link + "</a></p>";

        MimeMessage mimeMessage  = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(destino);
        helper.setSubject("Alteração de Senha");
        helper.setText(corpoEmail, true); // true = envia como HTML

        mailSender.send(mimeMessage);
    }

    @Value("${spring.mail.username}")
    private String fromEmail;

    @PostConstruct
    public void teste() {
        System.out.println("EMAIL CONFIGURADO: " + fromEmail);
    }

}
