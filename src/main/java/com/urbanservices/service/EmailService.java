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


    //  Sends Reset Password Email
    public void sendResetPasswordEmail(String to, String resetLink) {
        String subject = "Reset Your Password - Urban Services";
        String text = "<p>Hello,</p>"
                + "<p>We received a request to reset your password.</p>"
                + "<p>Click the link below to reset your password:</p>"
                + "<p><a href=\"" + resetLink + "\">Reset Password</a></p>"
                + "<p>This link will expire in 30 minutes.</p>"
                + "<p>If you did not request this, please ignore this email.</p>"
                + "<p>Thank you, <br>Urban Services Team</p>";

        sendEmail(to, subject, text);
    }

    //  Generic method to send emails
    private void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // Enable HTML content
            mailSender.send(message);
            System.out.println("Email sent to: " + to);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
