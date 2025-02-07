package com.ranjith_spring_projects.Bank.Application.Dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TransactionHistory {
    private String accountNumber;
    private String passcode;
}
