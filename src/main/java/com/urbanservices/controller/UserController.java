package com.urbanservices.controller;

import com.urbanservices.dto.LoginRequest;
import com.urbanservices.dto.OtpValidationRequest;
import com.urbanservices.dto.RegisterRequest;
import com.urbanservices.response.ApiResponse;
import com.urbanservices.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        ApiResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
