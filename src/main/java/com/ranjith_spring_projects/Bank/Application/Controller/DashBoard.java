package com.ranjith_spring_projects.Bank.Application.Controller;

import com.ranjith_spring_projects.Bank.Application.Entity.User;
import com.ranjith_spring_projects.Bank.Application.Entity.Users;
import com.ranjith_spring_projects.Bank.Application.Repository.UserRepository;
import com.ranjith_spring_projects.Bank.Application.Repository.UsersRepo;
import com.ranjith_spring_projects.Bank.Application.Service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class DashBoard {
    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTService jwtService;

    @GetMapping("/dashboard")
    public ResponseEntity<User> getDashboardDetails(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtService.extractEmail(token.substring(7)); // Remove Bearer
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}