package com.ranjith_spring_projects.Bank.Application.Dto;

import com.ranjith_spring_projects.Bank.Application.Entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Users user;
}
