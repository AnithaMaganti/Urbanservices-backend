package com.urbanservices.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String username, String otp) {
        String subject = "Your OTP for Urban Services Account Verification";
        String text = "<p>Hi <b>" + username + "</b>,</p>"
                + "<p>Your OTP for verification is: <b>" + otp + "</b></p>"
                + "<p>Please enter this OTP to complete your verification.</p>"
                + "<p>Thank you, <br>Urban Services Team</p>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // true enables HTML content
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
