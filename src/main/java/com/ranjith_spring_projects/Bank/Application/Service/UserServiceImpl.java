package com.ranjith_spring_projects.Bank.Application.Service;

import com.ranjith_spring_projects.Bank.Application.Dto.AccountInfo;
import com.ranjith_spring_projects.Bank.Application.Dto.BankResponse;
import com.ranjith_spring_projects.Bank.Application.Dto.EmailDetails;
import com.ranjith_spring_projects.Bank.Application.Dto.UserRequest;
import com.ranjith_spring_projects.Bank.Application.Entity.User;
import com.ranjith_spring_projects.Bank.Application.Repository.UserRepository;
import com.ranjith_spring_projects.Bank.Application.Utils.AccountNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    JWTService jwtService;

    @Autowired
    EmailService emailService;

    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        return null;
    }

    @Override
    public BankResponse createAccount(UserRequest userRequest, String token) {
        // First, ensure the user is authenticated via JWT
        if (jwtService.extractUserName(token) == null) {
            throw new RuntimeException("User not authenticated.");
        }

        if(userRepository.existsByEmail(userRequest.getEmail())){
            BankResponse response = BankResponse.builder()
                    .responseCode(AccountNumber.ACCOUNT_CODE)
                    .responseMessage(AccountNumber.ACCOUNT_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        // Proceed with account creation if user is authenticated
        User user = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountNumber.createAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .passcode(userRequest.getPasscode())
                .build();

        User saveduser = userRepository.save(user);
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(saveduser.getEmail())
                .subject("Account creation")
                .messageBody("congratulations ! your account has been created successfully.\nYour Account Details: \n" +
                        "Account Name : " + saveduser.getFirstName() + " " + saveduser.getLastName() + "\n Account number : " + saveduser.getAccountNumber() + "\n Passcode : " + saveduser.getPasscode())
                .build();
        emailService.sendEmailAlert(emailDetails);
        return BankResponse.builder()
                .responseCode(AccountNumber.ACCOUNT_CODE_CREATION)
                .responseMessage(AccountNumber.ACCOUNT_MESSAGE_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(saveduser.getAccountNumber())
                        .accountBalance(saveduser.getAccountBalance())
                        .accountName(saveduser.getFirstName() + " " + saveduser.getLastName() + " " + saveduser.getOtherName())
                        .build())
                .build();
    }

}
