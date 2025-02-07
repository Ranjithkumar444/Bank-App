package com.ranjith_spring_projects.Bank.Application.Controller;

import com.ranjith_spring_projects.Bank.Application.Dto.TransactionHistory;
import com.ranjith_spring_projects.Bank.Application.Entity.Transaction;
import com.ranjith_spring_projects.Bank.Application.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
