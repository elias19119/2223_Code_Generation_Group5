package io.swagger.model;


import io.swagger.model.Enums.UserRole;
import io.swagger.model.Enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@Table(name = "tbl_user")
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue
    private UUID id;


    @Column(unique = true)
    private String userName;

    private String mobileNumber;


    private String firstName;

    private String lastName;


    private String DateOfBirth;


    private String password;

    private UserRole roles;

    private UserStatus status;

    @OneToMany
    private Set<Account> accounts = new HashSet<>();

    public void addAccount(Account account){
        if (accounts == null) {
            accounts = new HashSet<>();
        }
        accounts.add(account);
    }
}

