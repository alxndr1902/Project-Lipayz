package com.zezame.lipayz.util;

import com.zezame.lipayz.model.Transaction;
import com.zezame.lipayz.model.User;
import com.zezame.lipayz.service.EmailTemplateService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailUtil {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    private final EmailTemplateService emailTemplateService;

    public void sendWelcomeEmail(User user, String activationLink) {
        Map<String, Object> model = new HashMap<>();
        model.put("fullName", user.getFullName());
        model.put("activationLink", activationLink);

        String htmlContent = emailTemplateService.getHtmlTemplate(
                "welcome.ftl",
                model
        );

        try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(user.getEmail());
        helper.setSubject("Welcome to Lipayz!");
        helper.setText(htmlContent);
        helper.setFrom(fromMail);

        helper.setText(htmlContent, true);

        mailSender.send(message);
        } catch (MessagingException e) {
            System.out.println("Error sending email: " + e.getMessage());
        }
    }

    public void sendTransactionEmail(Transaction transaction){
        sendEmail(transaction.getCustomer().getEmail(),
                "Transaction Create!",
                "create-transaction.ftl",
                buildTransactionModel(transaction));
    }

    public void sendUpdateTransactionEmail(Transaction transaction){
        sendEmail(transaction.getCustomer().getEmail(),
                "Transaction Updated!",
                "update-transaction.ftl",
                buildTransactionModel(transaction));
    }

    private Map<String, Object> buildTransactionModel(Transaction transaction) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String createdAt = transaction.getCreatedAt().format(formatter);

        Map<String, Object> model = new HashMap<>();

        model.put("customerName", transaction.getCustomer().getFullName());
        model.put("code", transaction.getCode());
        model.put("productName", transaction.getProduct().getName());
        model.put("paymentGatewayName", transaction.getPaymentGateway().getName());
        model.put("virtualAccountNumber", transaction.getVirtualAccountNumber());
        model.put("transactionStatusName", transaction.getTransactionStatus().getName());
        model.put("adminRate", transaction.getPaymentGateway().getRate());
        model.put("totalPrice", transaction.getTotalPrice());
        model.put("createdAt", createdAt);

        return model;
    }

    private void sendEmail(String to, String subject, String template, Map<String, Object> model) {
        String htmlContent = emailTemplateService.getHtmlTemplate(template, model);
        try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom(fromMail);
        helper.setText(htmlContent, true);


            mailSender.send(message);
        } catch (MessagingException e) {
            System.out.println("Failed to send email:" + e.getMessage());
        }
    }
}