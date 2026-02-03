package com.zezame.lipayz.util;

import com.zezame.lipayz.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

    public void sendWelcomeEmail(User user, String activationLink) throws MessagingException {
        Context context = new Context();
        context.setVariable("fullName", user.getFullName());
        context.setVariable("activationLink", activationLink);
        String htmlContent = templateEngine.process("welcome.html", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(user.getEmail());
        helper.setSubject("Welcome to Lipayz!");
        helper.setText(htmlContent);
        helper.setFrom(fromMail);

        helper.setText(htmlContent, true);

        try {
            mailSender.send(message);
        } catch (MailException e) {
            System.out.println(e.getMessage());
        }
    }
}
