package com.italo.geradorboleto.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.admin.emails:admin@empresa.com}")
    private List<String> adminEmails;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public void sendAccessRequestNotification(String userName, String userEmail, String loginInfo, String tempPassword) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("userEmail", userEmail);
            context.setVariable("loginInfo", loginInfo);
            context.setVariable("tempPassword", tempPassword);
            context.setVariable("requestDate", java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            context.setVariable("frontendUrl", frontendUrl);

            String htmlContent = templateEngine.process("emails/access-request-admin", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(adminEmails.toArray(new String[0]));
            helper.setSubject("👤 Nova Solicitação de Usuário - Sistema Financeiro");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de notificação enviado para administradores sobre solicitação de acesso de: {}", userEmail);

        } catch (Exception e) {
            log.error("Erro ao enviar email de notificação para administradores", e);
        }
    }

    public void sendAccessRequestConfirmation(String userName, String userEmail) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("requestDate", java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            context.setVariable("frontendUrl", frontendUrl);

            String htmlContent = templateEngine.process("emails/access-request-confirmation", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(userEmail);
            helper.setSubject("✅ Solicitação de Acesso Recebida - Sistema Financeiro");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de confirmação enviado para usuário: {}", userEmail);

        } catch (Exception e) {
            log.error("Erro ao enviar email de confirmação para usuário", e);
        }
    }

    public void sendAccessApprovedNotification(String userName, String userEmail, String tempPassword) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("userEmail", userEmail);
            context.setVariable("tempPassword", tempPassword);
            context.setVariable("frontendUrl", frontendUrl);
            context.setVariable("loginUrl", frontendUrl + "/auth");

            String htmlContent = templateEngine.process("emails/access-approved", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(userEmail);
            helper.setSubject("🎉 Acesso Aprovado - Sistema Financeiro");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de aprovação enviado para usuário: {}", userEmail);

        } catch (Exception e) {
            log.error("Erro ao enviar email de aprovação para usuário", e);
        }
    }

    public void sendPasswordChangedNotification(String userName, String userEmail) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("changeDate", java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            context.setVariable("frontendUrl", frontendUrl);

            String htmlContent = templateEngine.process("emails/password-changed", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(userEmail);
            helper.setSubject("🔐 Senha Alterada com Sucesso - Sistema Financeiro");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de alteração de senha enviado para usuário: {}", userEmail);

        } catch (Exception e) {
            log.error("Erro ao enviar email de alteração de senha", e);
        }
    }

    public void sendTestEmail(String toEmail) {
        try {
            Context context = new Context();
            context.setVariable("testDate", java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            context.setVariable("frontendUrl", frontendUrl);

            String htmlContent = templateEngine.process("emails/test", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("🧪 Teste de Email - Sistema Financeiro");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de teste enviado para: {}", toEmail);

        } catch (Exception e) {
            log.error("Erro ao enviar email de teste", e);
        }
    }
}
