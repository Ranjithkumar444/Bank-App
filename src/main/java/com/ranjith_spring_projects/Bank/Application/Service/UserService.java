package com.ranjith_spring_projects.Bank.Application.Service;

import com.ranjith_spring_projects.Bank.Application.Dto.*;

import java.math.BigDecimal;

public interface UserService {

    //BankResponse createAccount(UserRequest userRequest);

    BankResponse createAccount(UserRequest userRequest, String token);


    BigDecimal balanceCheck(BalanceRequest balanceRequest, String token);

    String TransferMoney(TransferRequest transferRequest, String token);

    String withDrayMoney(DebitRequest debitRequest, String token);

    String creditMoney(CreditRequest creditRequest, String token);
}
