package com.ranjith_spring_projects.Bank.Application.Dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceRequest {
    private String passcode; // Only passcode is required
}
