package com.ranjith_spring_projects.Bank.Application.Service;

import com.ranjith_spring_projects.Bank.Application.Dto.EmailDetails;
import com.ranjith_spring_projects.Bank.Application.Entity.Users;
import com.ranjith_spring_projects.Bank.Application.Repository.UsersRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class UsersService {
    @Autowired
    private UsersRepo repo;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private OTPService otpService;
    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public UsersService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Users register(Users user) {
        if (repo.existsByEmail(user.getEmail())) {
            throw new RuntimeException("The user email is already taken.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));  // Use the correct encoder
        return repo.save(user);
    }


    public String verify(Users user) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return user.getEmail();
    }


    public String generateOtp(String email) {
        Users user = repo.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found for the given email.");
        }
        String otp = otpService.generateOTP(email);
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("Your OTP Code")
                .messageBody("Your OTP is: " + otp + ". It will expire in 5 minutes.")
                .build();
        emailService.sendEmailAlert(emailDetails);
        return otp;
    }

    public String verifyOtp(String email, String otp) {
        if (otpService.validateOTP(email, otp)) {
            Users user = repo.findByEmail(email);
            return jwtService.generateToken(user.getEmail());
        } else {
            throw new RuntimeException("Invalid or expired OTP.");
        }
    }
}
