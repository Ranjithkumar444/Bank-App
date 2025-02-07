package com.ranjith_spring_projects.Bank.Application.Repository;

import com.ranjith_spring_projects.Bank.Application.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    List<Transaction> findByAccountNumber(String accountNumber);

}

