package com.ranjith_spring_projects.Bank.Application.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private String gender;

    @Column(nullable = true)
    private String otp;

    @Column(nullable = true)
    private LocalDateTime otpExpiry;

    @Column(nullable = false)
    private boolean twoFactorEnabled = false;


    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}

