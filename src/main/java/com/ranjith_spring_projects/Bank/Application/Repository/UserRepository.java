package com.ranjith_spring_projects.Bank.Application.Repository;

import com.ranjith_spring_projects.Bank.Application.Dto.BalanceRequest;
import com.ranjith_spring_projects.Bank.Application.Entity.User;
import com.ranjith_spring_projects.Bank.Application.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmail(String email);
    Optional<User> findByAccountNumber(String accountNumber);
    Optional<User> findByEmail(String email);
}
