package com.ranjith_spring_projects.Bank.Application.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String otherName;
    private String gender;
    private String address;
    private String stateOfOrigin;
    private String accountNumber;
    private BigDecimal accountBalance;

    @Column(nullable = false, unique = true)
    private String email;

    private String phoneNumber;
    private String alternativePhoneNumber;
    private String status;
    private String passcode;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Transaction> transactions;


    @OneToOne
    @JoinColumn(name = "users_id", nullable = false)
    @JsonManagedReference
    private Users users;

    @CreationTimestamp
    private String createdAt;

    @UpdateTimestamp
    private String modifiedAt;
}