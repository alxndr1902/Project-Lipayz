package com.zezame.lipayz.util;

import com.zezame.lipayz.model.Transaction;
import com.zezame.lipayz.model.User;
import com.zezame.lipayz.service.EmailTemplateService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailUtil {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromMail;

    private final EmailTemplateService emailTemplateService;

    public void sendWelcomeEmail(User user, String activationLink) throws MessagingException {
        Map<String, Object> model = new HashMap<>();
        model.put("fullName", user.getFullName());
        model.put("activationLink", activationLink);

        String htmlContent = emailTemplateService.getHtmlTemplate(
                "welcome.ftl",
                model
        );

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

    public void sendTransactionEmail(Transaction transaction) throws MessagingException {
        Map<String, Object> model = new HashMap<>();
        model.put("customerName", transaction.getCustomer().getFullName());
        model.put("code", transaction.getCode());
        model.put("productName", transaction.getProduct().getName());
        model.put("paymentGatewayName", transaction.getPaymentGateway().getName());
        model.put("virtualAccountNumber", transaction.getVirtualAccountNumber());
        model.put("transactionStatusName", transaction.getTransactionStatus().getName());
        model.put("adminRate", transaction.getPaymentGateway().getRate());
        model.put("totalPrice", transaction.getTotalPrice());
        model.put("createdAt", transaction.getCreatedAt());

        String htmlContent = emailTemplateService.getHtmlTemplate(
                "create-transaction.ftl",
                model
        );

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(transaction.getCustomer().getEmail());
        helper.setSubject("Transaction Created");
        helper.setFrom(fromMail);
        helper.setText(htmlContent, true);

        try {
            mailSender.send(message);
        } catch (MailException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendUpdateTransactionEmail(Transaction transaction) throws MessagingException {
        Map<String, Object> model = new HashMap<>();

        model.put("customerName", transaction.getCustomer().getFullName());
        model.put("code", transaction.getCode());
        model.put("productName", transaction.getProduct().getName());
        model.put("paymentGatewayName", transaction.getPaymentGateway().getName());
        model.put("virtualAccountNumber", transaction.getVirtualAccountNumber());
        model.put("transactionStatusName", transaction.getTransactionStatus().getName());
        model.put("adminRate", transaction.getPaymentGateway().getRate());
        model.put("totalPrice", transaction.getTotalPrice());
        model.put("createdAt", transaction.getCreatedAt());

        String htmlContent = emailTemplateService.getHtmlTemplate(
                "update-transaction.ftl",
                model
        );

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(transaction.getCustomer().getEmail());
        helper.setSubject("Transaction Updated");
        helper.setFrom(fromMail);
        helper.setText(htmlContent, true);

        try {
            mailSender.send(message);
        } catch (MailException e) {
            System.out.println(e.getMessage());
        }
    }


}
