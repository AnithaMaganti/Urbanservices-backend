package com.urbanservices.service;

import com.urbanservices.dto.LoginRequest;
import com.urbanservices.dto.OtpValidationRequest;
import com.urbanservices.dto.RegisterRequest;
import com.urbanservices.model.Otp;
import com.urbanservices.model.User;
import com.urbanservices.repo.OtpRepository;
import com.urbanservices.repo.UserRepository;
import com.urbanservices.response.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // Register a user
    @Transactional
    public ApiResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse("Email already exists", false);
        }

        // Save user in user table
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepository.save(user); // User saved

        // Generate OTP after user registration
        return generateOtp(request.getEmail());
    }

    // Generate OTP
    @Transactional
    public ApiResponse generateOtp(String email) {
        if (!userRepository.existsByEmail(email)) {
            return new ApiResponse("Email not found", false);
        }

        String otp = String.format("%06d", new Random().nextInt(1000000)); // 6-digit OTP
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        Otp otpEntry = new Otp();
        otpEntry.setEmail(email);
        otpEntry.setOtp(otp);
        otpEntry.setCreatedAt(LocalDateTime.now());
        otpEntry.setExpiresAt(expiryTime);

        otpRepository.save(otpEntry); // Storing OTP in the database

        System.out.println("OTP stored: " + otpEntry); // Debugging statement

        emailService.sendOtpEmail(email, "Your OTP Code", "Your OTP is: " + otp);
        return new ApiResponse("OTP sent successfully", true);
    }


    // Validate OTP
    public ApiResponse validateOtp(OtpValidationRequest request) {
        Optional<Otp> otpEntry = otpRepository.findByEmailAndExpiresAtAfter(request.getEmail(), LocalDateTime.now());

        if (otpEntry.isPresent() && otpEntry.get().getOtp().equals(request.getOtp())) {
            otpRepository.delete(otpEntry.get()); // Remove OTP after successful validation
            return new ApiResponse("OTP verified successfully", true);
        } else {
            return new ApiResponse("Invalid or expired OTP", false);
        }
    }

    // User Login
    public ApiResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new ApiResponse("Invalid credentials", false);
        }

        return new ApiResponse("Login successful", true);
    }
}
