package com.ranjith_spring_projects.Bank.Application.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo {

    private String accountName;

    private String accountNumber;

    private BigDecimal accountBalance;
}
