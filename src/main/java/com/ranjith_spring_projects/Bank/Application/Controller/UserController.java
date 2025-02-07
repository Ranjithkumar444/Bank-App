package com.ranjith_spring_projects.Bank.Application.Controller;

import com.ranjith_spring_projects.Bank.Application.Dto.*;
import com.ranjith_spring_projects.Bank.Application.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/transfer")
    public String transferAmount(@RequestBody TransferRequest transferRequest, @RequestHeader("Authorization") String token){
        token = token.substring(7);
        return userService.TransferMoney(transferRequest,token);
    }

    @PostMapping("/withdraw")
    public String withDrawAmount(@RequestBody DebitRequest debitRequest,@RequestHeader("Authorization") String token){
        token = token.substring(7);
        return userService.withDrawMoney(debitRequest,token);
    }

    @PostMapping("/credit")
    public String creditMoney(@RequestBody CreditRequest creditRequest,@RequestHeader("Authorization") String token){
        token = token.substring(7);
        return userService.creditMoney(creditRequest,token);
    }

}
