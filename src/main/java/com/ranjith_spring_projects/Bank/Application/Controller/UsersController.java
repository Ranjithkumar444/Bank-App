package com.ranjith_spring_projects.Bank.Application.Controller;

import com.ranjith_spring_projects.Bank.Application.Dto.LoginResponse;
import com.ranjith_spring_projects.Bank.Application.Dto.OtpRequest;
import com.ranjith_spring_projects.Bank.Application.Entity.Users;
import com.ranjith_spring_projects.Bank.Application.Repository.UsersRepo;
import com.ranjith_spring_projects.Bank.Application.Service.JWTService;
import com.ranjith_spring_projects.Bank.Application.Service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:1234")
public class UsersController {
    @Autowired
    private UsersService usersService;
    @Autowired
    private JWTService jwtService;

    @Autowired
    private UsersRepo repo;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Users user) {
        try {
            usersService.register(user);
            return ResponseEntity.ok("User registered successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody Users user) {
        try {
            String email = usersService.verify(user); // Verify user credentials (email and password)
            String token = jwtService.generateToken(email);
            Users existingUser = repo.findByEmail(email); // Fetch the user from the database

            if (existingUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            LoginResponse response = new LoginResponse(token, existingUser);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpRequest otpRequest) {
        try {
            String token = usersService.verifyOtp(otpRequest.getEmail(), otpRequest.getOtp());
            return ResponseEntity.ok("OTP Verified. Token: " + token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(@RequestBody OtpRequest otpRequest) {
        try {
            String email = otpRequest.getEmail();
            String otp = usersService.generateOtp(email);
            return ResponseEntity.ok("OTP sent to the user successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}