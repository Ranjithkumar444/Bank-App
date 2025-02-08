package com.ranjith_spring_projects.Bank.Application.Controller;

import com.ranjith_spring_projects.Bank.Application.Dto.*;
import com.ranjith_spring_projects.Bank.Application.Entity.Transaction;
import com.ranjith_spring_projects.Bank.Application.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/bank")
public class TransactionController {

    @Autowired
    private UserService userService;

    @PostMapping("/transaction-history")
    public List<Transaction> getTransactionHistory(
            @RequestBody TransactionHistory transactionHistory,
            @RequestHeader("Authorization") String token) {

        token = token.substring(7); // Removing "Bearer " prefix if present
        return userService.getTransactionHistory(transactionHistory, token);
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
    public String withDrawAmount(@RequestBody DebitRequest debitRequest, @RequestHeader("Authorization") String token){
        token = token.substring(7);
        return userService.withDrawMoney(debitRequest,token);
    }

    @PostMapping("/credit")
    public String creditMoney(@RequestBody CreditRequest creditRequest, @RequestHeader("Authorization") String token){
        token = token.substring(7);
        return userService.creditMoney(creditRequest,token);
    }
}
