# Bank-App

Bank Application (Spring Boot + Spring Security + JWT + MySQL + Hibernate)

Project Overview

This Bank Application is a secure and feature-rich backend system developed using Spring Boot. It provides essential banking operations and user authentication with security measures such as JWT-based token authentication, Bcrypt password encryption, and an OTP-based two-factor login mechanism.

Features

User Authentication:

  -- User registration with unique email and phone number validation.
  -- Login using email and password.
  -- Secure JWT token generation for authenticated requests.
  -- Bcrypt password encryption for secure storage.
  -- Optional OTP-based two-factor authentication.
  
Banking Operations:

  -- Create Bank Account: Associate bank accounts with registered users.
  -- Credit Account: Deposit funds to a bank account.
  -- Debit Account: Withdraw funds from a bank account.
  -- Transaction History: View detailed transaction records for users.
  -- Balance Check: Retrieve the current balance for bank accounts.
  -- Technologies Used
  
Backend Framework: Spring Boot
  -- Security: Spring Security, JWT, Bcrypt
  -- Database: MySQL
  -- Data Persistence: JPA and Hibernate
  -- Build Tool: Maven
  -- Language: Java 17+
