package com.ranjith_spring_projects.Bank.Application.Dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CreditRequest {

    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private String passcode;
}
