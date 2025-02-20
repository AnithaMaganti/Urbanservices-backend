package com.urbanservices.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpValidationRequest {
    private String email;
    private String otp;
}

