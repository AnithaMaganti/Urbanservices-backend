package com.urbanservices.controller;


import com.urbanservices.dto.LoginRequest;
import com.urbanservices.dto.OtpValidationRequest;
import com.urbanservices.dto.RegisterRequest;
import com.urbanservices.model.User;
import com.urbanservices.repo.UserRepository;
import com.urbanservices.response.ApiResponse;
import com.urbanservices.response.LoginResponse;
import com.urbanservices.service.ForgotPasswordService;
import com.urbanservices.service.UserService;
import com.urbanservices.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ForgotPasswordService forgotPasswordService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody RegisterRequest request) {
        ApiResponse response = userService.registerUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/otp/generate")
    public ResponseEntity<ApiResponse> generateOtp(@RequestParam String email) {
        ApiResponse response = userService.generateOtp(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/otp/validate")
    public ResponseEntity<ApiResponse> validateOtp(@RequestBody OtpValidationRequest request) {
        ApiResponse response = userService.validateOtp(request);
        return ResponseEntity.ok(response);
    }

//    @PostMapping("/login")
//    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
//        LoginResponse response = userService.login(request);
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        // Authenticate user using email instead of username
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Get user details and generate JWT token
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails.getUsername());

        // Retrieve user from the database
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Return the response with userId, message, and success status
        return ResponseEntity.ok(new LoginResponse(user.getId().toString(), token, true));
    }

//    // forgot password
//    @PostMapping("/forgot-password")
//    public ResponseEntity<ApiResponse> forgotPassword(@RequestParam String email) {
//        String token = userService.generateResetToken(email);
//
//        // Send reset link via email (mocked for now)
//        String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;
//        System.out.println("Reset Password Link: " + resetLink);
//
//        return ResponseEntity.ok(new ApiResponse("Password reset link sent to your email", false));
//    }
//
//
//     // API to reset the password using the token
//    @PostMapping("/reset-password")
//    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
//        userService.resetPassword(token, newPassword);
//        return ResponseEntity.ok(new ApiResponse("Password has been reset successfully",false));
//    }
//

    @GetMapping("/{id}") // Correctly maps GET /api/user/{id}
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.status(404).body("User not found");
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching user: " + e.getMessage());
        }
    }


    //  Forgot Password API - Generates reset token
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestParam String email) {
        try {
            String token = forgotPasswordService.generateResetToken(email);

            //  Send reset link via email (mocked)
            String resetLink = "http://localhost:3000/reset-password?token=" + token; // Frontend route
            System.out.println("Reset Password Link: " + resetLink);

            return ResponseEntity.ok(new ApiResponse("Password reset link sent to your email", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }

    //  Reset Password API - Verifies token & updates password
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            forgotPasswordService.resetPassword(token, newPassword);
            return ResponseEntity.ok(new ApiResponse("Password has been reset successfully", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }
}
