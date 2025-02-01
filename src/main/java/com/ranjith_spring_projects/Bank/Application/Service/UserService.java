package com.ranjith_spring_projects.Bank.Application.Service;

import com.ranjith_spring_projects.Bank.Application.Dto.BankResponse;
import com.ranjith_spring_projects.Bank.Application.Dto.UserRequest;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);

    BankResponse createAccount(UserRequest userRequest, String token);
}
