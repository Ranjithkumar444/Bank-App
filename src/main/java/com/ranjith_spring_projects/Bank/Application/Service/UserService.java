package com.ranjith_spring_projects.Bank.Application.Service;

import com.ranjith_spring_projects.Bank.Application.Dto.*;
import com.ranjith_spring_projects.Bank.Application.Entity.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest, String token);

    BigDecimal balanceCheck(BalanceRequest balanceRequest, String token);

    String TransferMoney(TransferRequest transferRequest, String token);

    String withDrawMoney(DebitRequest debitRequest, String token);

    String creditMoney(CreditRequest creditRequest, String token);

    List<Transaction> getTransactionHistory(TransactionHistory transactionHistory, String token);
}
