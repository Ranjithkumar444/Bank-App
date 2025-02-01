package com.ranjith_spring_projects.Bank.Application.Repository;

import com.ranjith_spring_projects.Bank.Application.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmail(String email);
}
