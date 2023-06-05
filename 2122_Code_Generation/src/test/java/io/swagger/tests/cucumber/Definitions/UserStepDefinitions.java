package io.swagger.tests.cucumber.Definitions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.messages.internal.com.google.gson.JsonArray;
import io.swagger.Repository.UserRepository;
import io.swagger.Service.UserService;
import io.swagger.model.DTOs.AuthenticationDTO;
import io.swagger.model.DTOs.CreateUserDTO;
import io.swagger.model.Enums.UserRole;
import io.swagger.model.Enums.UserStatus;
import io.swagger.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserStepDefinitions {

    String port = "8080";
    String employeeUsername = "employee@g.com";
    String employeePassword = "user";
    String token;

    String url = "http://localhost:" + port + "/api/users";

    private ObjectMapper mapper = new ObjectMapper();
    private HttpHeaders headers = new HttpHeaders();
    private RestTemplate template = new RestTemplate();
    private HttpEntity<String> entity;
    private ResponseEntity<String> response;

    private String responseBody;

    @Given("the user is an employee")
    public void givenTheUserIsEmployee() throws JSONException, URISyntaxException, JsonProcessingException {
        AuthenticationDTO loginDto = new AuthenticationDTO(employeeUsername, employeePassword);
        validateLogin(loginDto);
    }

    @When("creating a new user {string}")
    public void registerANewUser(String userValidity) throws URISyntaxException, JsonProcessingException {
        CreateUserDTO user;
        if(userValidity.equals("invalid")){
            user = new CreateUserDTO();
        }else{
            user = new CreateUserDTO("user4@g.com", "2266", "Rami", "Nam", "1996-04-04", "user",
                    String.valueOf(UserRole.CUSTOMER), UserStatus.ACTIVE);
        }

        URI uri = new URI("http://localhost:8080/api/register");

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        HttpEntity<String> entity = null;

        if(user.getUserName() != null){
            entity = new HttpEntity<>(mapper.writeValueAsString(user), headers);
        }else{
            entity = new HttpEntity<>(null,headers);
        }

        try {
            if(entity.getBody() != null){
                response = template.postForEntity(uri, entity, String.class);
            } else {
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        } catch (HttpClientErrorException.Unauthorized exception) {
            response = new ResponseEntity<>(exception.getStatusCode());
        }
    }

    @When("retrieving a user by id")
    public void retrievingAUserById() throws URISyntaxException, JSONException {
        whenGetAllUsers();

        JSONArray result = new JSONArray(responseBody);

        JSONObject user = result.getJSONObject(1);
        URI uri = new URI(url +"/"+ user.getString("id"));

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        entity = new HttpEntity<>("", headers);
        try{
            response = template.exchange(uri, HttpMethod.GET, entity, String.class);
        }catch (HttpClientErrorException.Unauthorized exception){

        }
    }

    @When("deleting a user by id")
    public void deleteAUserById() throws JSONException, URISyntaxException {
        whenGetAllUsers();
        JSONArray result = new JSONArray(responseBody);

        JSONObject user = result.getJSONObject(1);
        URI uri = new URI(url +"/"+ user.getString("id"));

        entity = new HttpEntity<>(headers);
        response = template.exchange(uri, HttpMethod.DELETE, entity, String.class);
    }

    @When("get all users")
    public void whenGetAllUsers() throws URISyntaxException, JSONException {
        URI uri = new URI(url);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        entity = new HttpEntity<>(headers);

        response = template.exchange(uri, HttpMethod.GET, entity, String.class);
        this.responseBody = response.getBody();
    }

    @Then("show http status {int}")
    public void thenShowHttpStatus200(int statusCode) {
        Assert.assertEquals(statusCode, response.getStatusCodeValue());
    }
    public void validateLogin(AuthenticationDTO loginDto) throws URISyntaxException, JsonProcessingException, JSONException {

        AuthenticationDTO login = new AuthenticationDTO(loginDto.getUserName(), loginDto.getPassword());

        URI uri = new URI("http://localhost:8080/api/login");
        headers.setContentType(MediaType.APPLICATION_JSON);

        entity = new HttpEntity<>(mapper.writeValueAsString(login), headers);
        response = template.postForEntity(uri, entity, String.class);

        JSONObject jsonObject = new JSONObject(response.getBody());
        token = jsonObject.getString("bearerToken");
    }
}