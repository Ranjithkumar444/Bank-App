package com.ranjith_spring_projects.Bank.Application.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpRequest {
    private String email;
    private String otp;
}