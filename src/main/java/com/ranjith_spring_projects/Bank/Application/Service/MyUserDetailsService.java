package com.ranjith_spring_projects.Bank.Application.Service;

import com.ranjith_spring_projects.Bank.Application.Entity.UserPrincipal;
import com.ranjith_spring_projects.Bank.Application.Entity.Users;
import com.ranjith_spring_projects.Bank.Application.Repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UsersRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userRepo.findByEmail(email);
        if (user == null) {
            System.out.println("User Not Found");
            throw new UsernameNotFoundException("user not found");
        }
        System.out.println("User found: " + user.getEmail());
        return new UserPrincipal(user);
    }
}