package com.dxc.iotbackend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetEmail(String to, String token) throws MessagingException {
        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        String subject = "Password Reset Request";
        String content = "<p>Hello,</p>"
                + "<p>You requested a password reset. Click the link below to reset it:</p>"
                + "<p><a href=\"" + resetLink + "\">Reset My Password</a></p>"
                + "<br>"
                + "<p>If you didn’t request this, you can safely ignore this email.</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true); // true enables HTML

        mailSender.send(message);
    }
}