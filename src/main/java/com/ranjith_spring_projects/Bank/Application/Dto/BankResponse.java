package com.ranjith_spring_projects.Bank.Application.Dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankResponse {

    private String responseCode;

    private String responseMessage;

    private AccountInfo accountInfo;
}
