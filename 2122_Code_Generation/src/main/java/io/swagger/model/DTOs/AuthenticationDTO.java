package io.swagger.model.DTOs;

public class AuthenticationDTO {

    private String username;
    private String password;

    public AuthenticationDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public AuthenticationDTO() {
    }

    public String getUserName() {
        return username;
    }
    public void setUserName(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
