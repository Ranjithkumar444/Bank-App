package com.ranjith_spring_projects.Bank.Application.Repository;

import com.ranjith_spring_projects.Bank.Application.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepo extends JpaRepository<Users, Integer> {
    Users findByEmail(String email);
    boolean existsByEmail(String email);
}