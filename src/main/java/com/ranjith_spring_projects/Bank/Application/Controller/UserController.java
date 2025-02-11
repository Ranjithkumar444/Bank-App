package com.ranjith_spring_projects.Bank.Application.Controller;

import com.ranjith_spring_projects.Bank.Application.Dto.*;
import com.ranjith_spring_projects.Bank.Application.Entity.Users;
import com.ranjith_spring_projects.Bank.Application.Service.UserService;
import com.ranjith_spring_projects.Bank.Application.Service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/create-user")
    public BankResponse createAccount(@RequestBody UserRequest userRequest,
                                      @RequestHeader("Authorization") String token) {
        token = token.substring(7);
        return userService.createAccount(userRequest, token);
    }
}