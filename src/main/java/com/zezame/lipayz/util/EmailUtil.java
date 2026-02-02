package com.zezame.lipayz.util;

import com.zezame.lipayz.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
public class EmailUtil {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromMail;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom("romian37@gmail.com");
        message.setSubject(subject);
        message.setText(body);
        try {
            mailSender.send(message);
        } catch (MailException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendWelcomeEmail(User user, String activationLink) {
        Context context = new Context();
        context.setVariable("fullName", user.getFullName());
        context.setVariable("activationLink", activationLink);
        String htmlContent = templateEngine.process("welcome.html", context);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Welcome to Lipayz!");
        message.setText(htmlContent);
        message.setFrom(fromMail);

        try {
            mailSender.send(message);
        } catch (MailException e) {
            System.out.println(e.getMessage());
        }
    }
}
