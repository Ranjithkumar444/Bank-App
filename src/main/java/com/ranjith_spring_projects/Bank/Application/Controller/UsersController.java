package com.ranjith_spring_projects.Bank.Application.Controller;

import com.ranjith_spring_projects.Bank.Application.Dto.OtpRequest;
import com.ranjith_spring_projects.Bank.Application.Entity.Users;
import com.ranjith_spring_projects.Bank.Application.Service.JWTService;
import com.ranjith_spring_projects.Bank.Application.Service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @Autowired
    JWTService jwtService;

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
    public String login(@RequestBody Users user) {
        return usersService.verify(user);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpRequest otpRequest) {
        try {
            usersService.verifyOtp(otpRequest.getUsername(), otpRequest.getOtp());
            String token = jwtService.generateToken(otpRequest.getUsername());
            return ResponseEntity.ok("OTP Verified. Token: " + token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(@RequestBody OtpRequest otpRequest) {
        try {
            String username = otpRequest.getUsername();
            String otp = usersService.generateOtp(username);
            return ResponseEntity.ok("OTP sent to the user successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}


