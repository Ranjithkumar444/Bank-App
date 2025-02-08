package com.ranjith_spring_projects.Bank.Application.Service;

import com.ranjith_spring_projects.Bank.Application.Dto.EmailDetails;
import com.ranjith_spring_projects.Bank.Application.Entity.Users;
import com.ranjith_spring_projects.Bank.Application.Repository.UsersRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    UsersRepo usersRepo;

    @Autowired
    AuthenticationManager authManager;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public Users register(Users user) {
        // Check if the username already exists
        if (repo.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("The username is already taken. Try another username.");
        }

        // Check if the email already exists
        if (repo.existsByEmail(user.getEmail())) {
            throw new RuntimeException("The user email is already taken.");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    public String verify(Users user) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(user.getUsername());
        } else {
            throw new RuntimeException("Authentication failed");
        }
    }

    public String generateOtp(String username) {
        Users user = usersRepo.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found for the given username.");
        }

        String otp = otpService.generateOTP(username);

        // Optionally store the OTP in memory/database with expiry logic if needed.

        // Send email notification with the generated OTP
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("Your OTP Code")
                .messageBody("Your OTP is: " + otp + ". It will expire in 5 minutes.")
                .build();
        emailService.sendEmailAlert(emailDetails);

        return otp;
    }

    private final SecureRandom random = new SecureRandom();

    public String verifyOtp(String username, String otp) {
        // Validate the OTP
        if (otpService.validateOTP(username, otp)) {
            // Generate token after successful OTP verification
            return jwtService.generateToken(username);
        } else {
            throw new RuntimeException("Invalid or expired OTP.");
        }
    }
}

