package com.ranjith_spring_projects.Bank.Application.Service;

import com.ranjith_spring_projects.Bank.Application.Dto.*;
import com.ranjith_spring_projects.Bank.Application.Entity.Transaction;
import com.ranjith_spring_projects.Bank.Application.Entity.User;
import com.ranjith_spring_projects.Bank.Application.Repository.TransactionRepository;
import com.ranjith_spring_projects.Bank.Application.Repository.UserRepository;
import com.ranjith_spring_projects.Bank.Application.Utils.AccountNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    JWTService jwtService;

    @Autowired
    EmailService emailService;

    @Autowired
    TransactionRepository transactionRepository;

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
        User fromUser = userRepository.findByAccountNumber(transferRequest.getFromAccountNumber())
                .orElseThrow(() -> new RuntimeException("Invalid Account from"));

        User toUser = userRepository.findByAccountNumber(transferRequest.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("Invalid Account to"));

        // Validate passcode and check balance
        if (fromUser.getPasscode().equals(transferRequest.getPasscode())) {
            if (fromUser.getAccountBalance().compareTo(transferRequest.getAmount()) >= 0) {
                // Debit the from user's account
                BigDecimal debitFrom = fromUser.getAccountBalance().subtract(transferRequest.getAmount());
                fromUser.setAccountBalance(debitFrom);

                // Credit the to user's account
                BigDecimal creditTo = toUser.getAccountBalance().add(transferRequest.getAmount());
                toUser.setAccountBalance(creditTo);

                // Save changes to the database
                userRepository.save(fromUser);
                userRepository.save(toUser);

                // Create and save transaction records
                createTransaction(fromUser, transferRequest.getFromAccountNumber(), transferRequest.getAmount().negate(), "TRANSFER", "SUCCESS", "Transfer to " + transferRequest.getToAccountNumber());
                createTransaction(toUser, transferRequest.getToAccountNumber(), transferRequest.getAmount(), "TRANSFER", "SUCCESS", "Transfer from " + transferRequest.getFromAccountNumber());

                return "The transaction was successful";
            } else {
                return "The balance from the account is not sufficient";
            }
        } else {
            return "The passcode entered is wrong";
        }
    }

    @Override
    public String withDrawMoney(DebitRequest debitRequest, String token) {
        User user = userRepository.findByAccountNumber(debitRequest.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Invalid Account Number"));

        if (!user.getPasscode().equals(debitRequest.getPasscode())) {
            return "Invalid Passcode";
        }

        if (user.getAccountBalance().compareTo(debitRequest.getAmount()) < 0) {
            return "Insufficient Balance";
        }

        // Debit the user's account
        user.setAccountBalance(user.getAccountBalance().subtract(debitRequest.getAmount()));
        userRepository.save(user);

        // Create and save transaction record
        createTransaction(user, debitRequest.getAccountNumber(), debitRequest.getAmount().negate(), "WITHDRAWAL", "SUCCESS", "Withdrawal from account");

        return String.format("The withdrawal of amount %.2f was successful from Account Number %s. Current balance: %.2f",
                debitRequest.getAmount(), debitRequest.getAccountNumber(), user.getAccountBalance());
    }

    @Override
    public String creditMoney(CreditRequest creditRequest, String token) {
        User user = userRepository.findByAccountNumber(creditRequest.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Invalid Account Number"));

        // Credit the user's account
        user.setAccountBalance(user.getAccountBalance().add(creditRequest.getAmount()));
        userRepository.save(user);

        // Create and save transaction record
        createTransaction(user, creditRequest.getAccountNumber(), creditRequest.getAmount(), "DEPOSIT", "SUCCESS", "Deposit to account");

        return "The Amount " + creditRequest.getAmount() + " is credited to your AccountNumber " + creditRequest.getAccountNumber();
    }


    private void createTransaction(User user, String accountNumber, BigDecimal amount, String transactionType, String transactionStatus, String description) {
        Transaction transaction = Transaction.builder()
                .accountNumber(accountNumber)
                .amount(amount)
                .transactionType(transactionType)
                .transactionStatus(transactionStatus)
                .transactionDate(LocalDateTime.now())
                .description(description)
                .user(user)
                .build();

        transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getTransactionHistory(TransactionHistory transactionHistory, String token) {
        String username = jwtService.extractUserName(token);

        if (username == null) {
            throw new RuntimeException("User not authenticated.");
        }

        // Extract account details from the DTO
        String accountNumber = transactionHistory.getAccountNumber();
        String passcode = transactionHistory.getPasscode();

        // Verify account and passcode
        User user = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Invalid account number."));

        if (!user.getPasscode().equals(passcode)) {
            throw new RuntimeException("Invalid passcode.");
        }

        // Fetch transaction history for the given account number
        return transactionRepository.findByAccountNumber(accountNumber);
    }

}
