package com.ranjith_spring_projects.Bank.Application.Service;


import com.ranjith_spring_projects.Bank.Application.Dto.EmailDetails;

public interface EmailService {

    public void sendEmailAlert(EmailDetails emailDetails);
}
