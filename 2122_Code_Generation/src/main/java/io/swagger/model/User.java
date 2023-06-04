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

    // @NotEmpty(message = "UserName is Required")
    @Column(unique = true)
    private String userName;

    // @NotEmpty(message = "Mobile Number is required")
    private String mobileNumber;

    // @NotEmpty(message = "FirstName is required")
    private String firstName;

    //@NotEmpty(message = "LastName is required")
    private String lastName;

    //@NotEmpty(message = "Date of birth is required")
    private String DateOfBirth;

    // @NotEmpty(message = "Password is required")
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

