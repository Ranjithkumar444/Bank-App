package com.ranjith_spring_projects.Bank.Application.Service;

import com.ranjith_spring_projects.Bank.Application.Dto.*;
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

//    @Override
//    public BankResponse createAccount(UserRequest userRequest) {
//        return null;
//    }

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

    @Override
    public BigDecimal balanceCheck(BalanceRequest balanceRequest, String token) {
        // Authenticate using the JWT token
        String username = jwtService.extractUserName(token);
        if (username == null) {
            throw new RuntimeException("User not authenticated.");
        }

        // Find user by account number
        User user = userRepository.findByAccountNumber(balanceRequest.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Invalid account number or passcode."));

        // Validate passcode
        if (!user.getPasscode().equals(balanceRequest.getPasscode())) {
            throw new RuntimeException("Invalid account number or passcode.");
        }

        // Return the account balance
        return user.getAccountBalance();
    }


    @Override
    public String TransferMoney(TransferRequest transferRequest, String token) {
        String username = jwtService.extractUserName(token);
        if (username == null) {
            throw new RuntimeException("User not authenticated.");
        }

        // Fetch users by account number
        User fromuser = userRepository.findByAccountNumber(transferRequest.getFromAccountNumber())
                .orElseThrow(() -> new RuntimeException("Invalid Account from"));

        User touser = userRepository.findByAccountNumber(transferRequest.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("Invalid Account to"));

        // Validate passcode and check balance
        if (fromuser.getPasscode().equals(transferRequest.getPasscode())) {
            if (fromuser.getAccountBalance().compareTo(transferRequest.getAmount()) >= 0) {
                // Debit the from user's account
                BigDecimal debitfrom = fromuser.getAccountBalance().subtract(transferRequest.getAmount());
                fromuser.setAccountBalance(debitfrom);

                // Credit the to user's account
                BigDecimal creditto = touser.getAccountBalance().add(transferRequest.getAmount());
                touser.setAccountBalance(creditto);

                // Save changes to the database
                userRepository.save(fromuser);
                userRepository.save(touser);

                return "The transaction was successful";
            } else {
                return "The balance from the account is not sufficient";
            }
        } else {
            return "The passcode entered is wrong";
        }
    }

    @Override
    public String withDrayMoney(DebitRequest debitRequest, String token) {

        User user = userRepository.findByAccountNumber(debitRequest.getAccountNumber()).
                orElseThrow(() -> new RuntimeException("Invalid Account Number"));

        if (!user.getPasscode().equals(debitRequest.getPasscode())) {
            return "Invalid Passcode";
        }

        if(user == null){
            return "User Account not Found";
        }else if(user.getAccountBalance().compareTo(debitRequest.getAmount()) < 0){
            return "Insufficient Balance";
        }

        user.setAccountBalance(user.getAccountBalance().subtract(debitRequest.getAmount()));
        userRepository.save(user);

        return String.format("The withdrawal of amount %.2f was successful from Account Number %s. Current balance: %.2f",
                debitRequest.getAmount(), debitRequest.getAccountNumber(), user.getAccountBalance());
    }

    @Override
    public String creditMoney(CreditRequest creditRequest, String token) {
        User user = userRepository.findByAccountNumber(creditRequest.getAccountNumber()).
                orElseThrow(() -> new RuntimeException("Invalid Account Number"));

        user.setAccountBalance(user.getAccountBalance().add(creditRequest.getAmount()));
        userRepository.save(user);

        String temp = creditRequest.getAmount().toString();

        return "The Amount  " + temp + "  is credited to your AccountNumber  " + creditRequest.getAccountNumber();
    }


}
