package io.swagger.tests.cucumber.Definitions;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.Repository.UserRepository;
import io.swagger.model.Account;
import io.swagger.model.DTOs.AuthenticationDTO;
import io.swagger.model.DTOs.CreateUserDTO;
import io.swagger.model.Enums.AccountStatus;
import io.swagger.model.Enums.AccountType;
import io.swagger.model.Enums.UserRole;
import io.swagger.model.Enums.UserStatus;
import io.swagger.model.User;
import org.json.JSONException;
import org.json.JSONObject;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Component
public class DefinitionStepHelper {

    private ObjectMapper mapper = new ObjectMapper();
    private HttpHeaders headers = new HttpHeaders();
    private RestTemplate template = new RestTemplate();
    private HttpEntity<String> entity;
    private ResponseEntity<String> response;
    private String token;
    private String responseBody;
    User createdUser;

    @Mock
    private UserRepository userRepository;


    public ResponseEntity<String> validateLogin(String username, String password) throws URISyntaxException, JsonProcessingException, JSONException {

        AuthenticationDTO login = new AuthenticationDTO(username, password);

        URI uri = new URI("http://localhost:8080/api/login");
        headers.setContentType(MediaType.APPLICATION_JSON);

        entity = new HttpEntity<>(mapper.writeValueAsString(login), headers);
        response= template.postForEntity(uri, entity, String.class);

        JSONObject jsonObject = new JSONObject(response.getBody());
        token = jsonObject.getString("bearerToken");
        return response;
    }
    public User registerRandomUser(String token) throws URISyntaxException, JsonProcessingException {
        CreateUserDTO user = new CreateUserDTO("user9@g.com", "2266", "Rafi", "cj", "1996-06-04", "user",
                String.valueOf(UserRole.CUSTOMER), UserStatus.ACTIVE);

        URI uri = new URI("http://localhost:8080/api/register");

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        HttpEntity<String> entity = null;

        entity = new HttpEntity<>(mapper.writeValueAsString(user), headers);

        ResponseEntity<User> response = template.postForEntity(uri, entity, User.class);

        if(response.getStatusCode() == HttpStatus.CREATED){
            createdUser = response.getBody();
        }

        return createdUser;
    }
    public String getUserDetailsAsJson(String token) throws URISyntaxException {
        URI uri = new URI("http://localhost:8080/api/users");

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        entity = new HttpEntity<>(headers);

        response = template.exchange(uri, HttpMethod.GET, entity, String.class);
        responseBody = response.getBody();
        return responseBody;
    }
    public void getUserByUsername(String responseBody, String username){
        Account account1 = setAccount("NL82INGB4787396659");
        Account account2 = setAccount("NL82INGB4787398853");
        User user1 = setUser("customer12@g.com", UserRole.CUSTOMER, Collections.singleton(account1));
        User user2 = setUser("customer13@g.com", UserRole.CUSTOMER, Collections.singleton(account2));

        //MockitoAnnotations.openMocks(this);
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user1));
        Optional<User> result = userRepository.findByUserName(username);
        String s = "";
    }

    private User setUser(String username, UserRole role, Set<Account> account){
        User user1 = new User();
        user1.setPassword("user");
        user1.setUserName(username);
        user1.setLastName("mar");
        user1.setFirstName("lehm");
        user1.setStatus(UserStatus.ACTIVE);
        user1.setDateOfBirth("01-05-1999");
        user1.setRoles(role);
        user1.setMobileNumber("06-45281368");
        user1.setAccounts(account);
        return user1;
    }
    private Account setAccount(String IBAN){
        Account account = new Account();
        account.setIBANNo(IBAN);
        account.setAccountType(AccountType.CURRENT);
        account.setBalance(200);
        account.setDateOfOpening(LocalDate.now());
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setTransactionLimit(1000);
        account.setAbsoluteLimit(1000);
        account.setDayLimit(1000);
        return account;
    }
}
