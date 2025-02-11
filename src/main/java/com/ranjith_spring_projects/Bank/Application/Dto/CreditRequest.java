package com.ranjith_spring_projects.Bank.Application.Dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class CreditRequest {

    private BigDecimal amount;
    private String passcode;
}
