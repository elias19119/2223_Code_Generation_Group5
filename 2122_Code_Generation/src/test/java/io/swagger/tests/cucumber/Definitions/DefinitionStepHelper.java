package io.swagger.tests.cucumber.Definitions;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.model.DTOs.AuthenticationDTO;
import io.swagger.model.DTOs.CreateUserDTO;
import io.swagger.model.Enums.UserRole;
import io.swagger.model.Enums.UserStatus;
import io.swagger.model.User;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class DefinitionStepHelper {

    private ObjectMapper mapper = new ObjectMapper();
    private HttpHeaders headers = new HttpHeaders();
    private RestTemplate template = new RestTemplate();
    private HttpEntity<String> entity;
    private ResponseEntity<String> response;
    private String token;
    private String responseBody;
    User createdUser;

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
}
