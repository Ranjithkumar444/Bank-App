package com.ranjith_spring_projects.Bank.Application.Dto;


import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DebitRequest {
    private String passcode;
    private BigDecimal amount;
}
