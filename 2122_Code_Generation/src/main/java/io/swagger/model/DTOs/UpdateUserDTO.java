package io.swagger.model.DTOs;


import io.swagger.model.Enums.UserRole;
import io.swagger.model.Enums.UserStatus;

public class UpdateUserDTO {

    private String userName;


    private String mobileNumber;


    private String firstName;


    private String lastName;


    private String DateOfBirth;

    private String password;

    private UserRole roles;

    private UserStatus status;

    public UpdateUserDTO(String userName, String mobileNumber, String firstName, String lastName, String dateOfBirth, String password, UserRole roles, UserStatus status) {
        this.userName = userName;
        this.mobileNumber = mobileNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        DateOfBirth = dateOfBirth;
        this.password = password;
        this.roles = roles;
        this.status = status;
    }

    public UpdateUserDTO() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRoles() {
        return roles;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setRoles(UserRole roles) {
        this.roles = roles;
    }

}