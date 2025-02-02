package com.ranjith_spring_projects.Bank.Application.Service;

import com.ranjith_spring_projects.Bank.Application.Dto.BalanceRequest;
import com.ranjith_spring_projects.Bank.Application.Dto.BankResponse;
import com.ranjith_spring_projects.Bank.Application.Dto.CreditRequest;
import com.ranjith_spring_projects.Bank.Application.Dto.UserRequest;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

public interface UserService {

    //BankResponse createAccount(UserRequest userRequest);

    BankResponse createAccount(UserRequest userRequest, String token);


    BigDecimal balanceCheck(BalanceRequest balanceRequest, String token);

    String creditMoney(CreditRequest creditRequest, String token);
}
