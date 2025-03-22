package com.ranjith_spring_projects.Bank.Application.Service;

import com.ranjith_spring_projects.Bank.Application.Dto.*;
import com.ranjith_spring_projects.Bank.Application.Entity.Transaction;
import com.ranjith_spring_projects.Bank.Application.Entity.User;
import com.ranjith_spring_projects.Bank.Application.Entity.Users;
import com.ranjith_spring_projects.Bank.Application.Repository.TransactionRepository;
import com.ranjith_spring_projects.Bank.Application.Repository.UserRepository;
import com.ranjith_spring_projects.Bank.Application.Repository.UsersRepo;
import com.ranjith_spring_projects.Bank.Application.Utils.AccountNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    JWTService jwtService;
    @Autowired
    EmailService emailService;
    @Autowired
    UsersRepo usersRepo;
    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public BankResponse createAccount(UserRequest userRequest, String token) {
        // Extract email from the token (if needed for additional validation)
        String emailFromToken = jwtService.extractEmail(token);
        if (emailFromToken == null) {
            throw new RuntimeException("User not authenticated.");
        }

        String emailFromRequest = userRequest.getEmail();
        Users registeredUser = usersRepo.findByEmail(emailFromRequest);
        if (registeredUser == null) {
            throw new RuntimeException("User with the provided email does not exist in the Users table.");
        }

        // Check if a bank account already exists for this user
        if (userRepository.existsByEmail(registeredUser.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountNumber.ACCOUNT_CODE)
                    .responseMessage("Account already exists for this user.")
                    .accountInfo(null)
                    .build();
        }

        User user = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountNumber.createAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(registeredUser.getEmail())
                .phoneNumber(registeredUser.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .passcode(userRequest.getPasscode())
                .users(registeredUser) // Link the fetched Users entity
                .build();

        // Save the User entity
        User savedUser = userRepository.save(user);

        // Send an email notification
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("Account Creation")
                .messageBody("Congratulations! Your account has been created successfully.\n" +
                        "Account Details:\n" +
                        "Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + "\n" +
                        "Account Number: " + savedUser.getAccountNumber() + "\n" +
                        "Passcode: " + savedUser.getPasscode())
                .build();
        emailService.sendEmailAlert(emailDetails);

        // Return the response
        return BankResponse.builder()
                .responseCode(AccountNumber.ACCOUNT_CODE_CREATION)
                .responseMessage(AccountNumber.ACCOUNT_MESSAGE_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(savedUser.getAccountNumber())
                        .accountBalance(savedUser.getAccountBalance())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public BigDecimal balanceCheck(BalanceRequest balanceRequest, String token) {
        String email = jwtService.extractEmail(token);
        if (email == null) {
            throw new RuntimeException("User not authenticated.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));


        if (!user.getPasscode().equals(balanceRequest.getPasscode())) {
            throw new RuntimeException("Invalid passcode.");
        }

        return user.getAccountBalance();
    }

    @Override
    public String TransferMoney(TransferRequest transferRequest, String token) {
        String email = jwtService.extractEmail(token);
        if (email == null) {
            throw new RuntimeException("User not authenticated.");
        }

        User fromUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        User toUser = userRepository.findByAccountNumber(transferRequest.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("Invalid recipient account number."));

        if (fromUser.getPasscode().equals(transferRequest.getPasscode())) {
            if (fromUser.getAccountBalance().compareTo(transferRequest.getAmount()) >= 0) {
                // Perform the transfer
                BigDecimal debitFrom = fromUser.getAccountBalance().subtract(transferRequest.getAmount());
                fromUser.setAccountBalance(debitFrom);

                BigDecimal creditTo = toUser.getAccountBalance().add(transferRequest.getAmount());
                toUser.setAccountBalance(creditTo);

                userRepository.save(fromUser);
                userRepository.save(toUser);

                // Create transaction records
                createTransaction(fromUser, fromUser.getAccountNumber(), transferRequest.getAmount().negate(), "TRANSFER", "SUCCESS", "Transfer to " + transferRequest.getToAccountNumber());
                createTransaction(toUser, transferRequest.getToAccountNumber(), transferRequest.getAmount(), "TRANSFER", "SUCCESS", "Transfer from " + fromUser.getAccountNumber());

                return "The transaction was successful.";
            } else {
                return "Insufficient balance.";
            }
        } else {
            return "Invalid passcode.";
        }
    }

    @Override
    public String withDrawMoney(DebitRequest debitRequest, String token) {
        String email = jwtService.extractEmail(token);

        if(email == null){
            throw new RuntimeException("User is not Authenticated");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if(!user.getPasscode().equals(debitRequest.getPasscode())){
            return "Invalid Passcode";
        }
        if(user.getAccountBalance().compareTo(debitRequest.getAmount()) < 0){
            return "Insufficient Balance";
        }
        user.setAccountBalance(user.getAccountBalance().subtract(debitRequest.getAmount()));
        userRepository.save(user);

        createTransaction(user, user.getAccountNumber(), debitRequest.getAmount().negate(), "WITHDRAWAL", "SUCCESS", "Withdrawal from account");
        return String.format("The withdrawal of amount %.2f was successful from Account Number %s. Current balance: %.2f",
                debitRequest.getAmount(), user.getAccountNumber(), user.getAccountBalance());
    }

    @Override
    public String creditMoney(CreditRequest creditRequest, String token) {

        String email = jwtService.extractEmail(token);
        if (email == null) {
            throw new RuntimeException("User not authenticated.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));
        user.setAccountBalance(user.getAccountBalance().add(creditRequest.getAmount()));
        userRepository.save(user);
        createTransaction(user,user.getAccountNumber(),creditRequest.getAmount(),"DEPOSIT","SUCCESS","Deposit to account");

        return "The Amount " + creditRequest.getAmount() + " is credited to your AccountNumber ";
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
        String email = jwtService.extractEmail(token);
        if (email == null) {
            throw new RuntimeException("User not authenticated.");
        }

        String accountNumber = transactionHistory.getAccountNumber();
        String passcode = transactionHistory.getPasscode();
        User user = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Invalid account number."));
        if (!user.getPasscode().equals(passcode)) {
            throw new RuntimeException("Invalid passcode.");
        }
        return transactionRepository.findByAccountNumber(accountNumber);
    }
}
