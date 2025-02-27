package com.urbanservices.service;

import com.urbanservices.model.PasswordResetToken;
import com.urbanservices.model.User;
import com.urbanservices.repo.PasswordResetTokenRepository;
import com.urbanservices.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class ForgotPasswordService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService; //  Inject EmailService

    //  Generates Reset Token & Sends Email
    public String generateResetToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString(); // Generate unique token

        //  Delete existing token to prevent duplicates
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        //  Save new token
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(new Date(System.currentTimeMillis() + 30 * 60 * 1000)); // 30 min expiry
        tokenRepository.save(resetToken);

        //  Send reset email
        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        emailService.sendResetPasswordEmail(user.getEmail(), resetLink);

        return token;
    }

    //  Resets Password if Token is Valid
    public void resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(token);

        if (tokenOptional.isEmpty()) {
            throw new RuntimeException("Invalid password reset token");
        }

        PasswordResetToken resetToken = tokenOptional.get();

        //  Check if token is expired
        if (resetToken.getExpiryDate().before(new Date())) {
            throw new RuntimeException("Password reset token has expired");
        }

        //  Update user's password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        //  Remove the token after successful reset
        tokenRepository.delete(resetToken);
    }
}
