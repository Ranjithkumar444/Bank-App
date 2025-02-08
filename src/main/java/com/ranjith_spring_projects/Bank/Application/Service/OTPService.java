package com.ranjith_spring_projects.Bank.Application.Service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OTPService {

    private final int OTP_VALIDITY_DURATION = 5; // 5 minutes

    private Map<String, OtpDetails> otpStorage = new HashMap<>();

    public String generateOTP(String username) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        otpStorage.put(username, new OtpDetails(otp, LocalDateTime.now().plusMinutes(OTP_VALIDITY_DURATION)));
        return otp;
    }

    public boolean validateOTP(String username, String otp) {
        OtpDetails otpDetails = otpStorage.get(username);

        if (otpDetails == null || LocalDateTime.now().isAfter(otpDetails.getExpiryTime())) {
            throw new RuntimeException("Invalid or expired OTP.");
        }

        if (!otpDetails.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP.");
        }

        // Remove the OTP once validated
        otpStorage.remove(username);
        return true;
    }

    @Getter
    @AllArgsConstructor
    public static class OtpDetails {
        private String otp;
        private LocalDateTime expiryTime;
    }
}

