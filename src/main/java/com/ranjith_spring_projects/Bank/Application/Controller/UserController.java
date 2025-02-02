package com.ranjith_spring_projects.Bank.Application.Controller;

import com.ranjith_spring_projects.Bank.Application.Dto.BalanceRequest;
import com.ranjith_spring_projects.Bank.Application.Dto.BankResponse;
import com.ranjith_spring_projects.Bank.Application.Dto.CreditRequest;
import com.ranjith_spring_projects.Bank.Application.Dto.UserRequest;
import com.ranjith_spring_projects.Bank.Application.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
        token = token.substring(7); // Remove "Bearer " prefix
        return userService.createAccount(userRequest, token); // Pass token to the service
    }

    @PostMapping("/balance")
    public BigDecimal balanceCheck(@RequestBody BalanceRequest balanceRequest,
                                   @RequestHeader("Authorization") String token) {
        token = token.substring(7); // Remove "Bearer " prefix
        return userService.balanceCheck(balanceRequest, token);
    }

    @PostMapping("/credit")
    public String creditAmount(@RequestBody CreditRequest creditRequest,@RequestHeader("Authorization") String token){
        token = token.substring(7);
        return userService.creditMoney(creditRequest,token);
    }

}
