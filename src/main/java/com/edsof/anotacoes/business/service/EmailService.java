package com.edsof.anotacoes.business.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailRecuperacao(String destino, String token) {

        String link = "http://localhost:5173/resetar-senha?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destino);
        message.setSubject("Recuperação de Senha");
        message.setText("Clique no link para redefinir sua senha:\n" + link);

        mailSender.send(message);
    }
    @Value("${spring.mail.username}")
    private String fromEmail;

    @PostConstruct
    public void teste() {
        System.out.println("EMAIL CONFIGURADO: " + fromEmail);
    }
}
