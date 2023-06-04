package io.swagger.tests.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URISyntaxException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.swagger.model.DTOs.UpdateUserDTO;
import org.json.JSONObject;

import io.swagger.Service.UserService;
import io.swagger.model.DTOs.AuthenticationDTO;
import io.swagger.model.DTOs.CreateUserDTO;
import io.swagger.model.Enums.UserRole;
import io.swagger.model.Enums.UserStatus;
import org.json.JSONException;
import org.junit.Assert;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;


public class UserStepDefinitions {

    UserService userService;

    private String token;
    private ObjectMapper mapper = new ObjectMapper();
    private HttpHeaders headers = new HttpHeaders();
    private RestTemplate template = new RestTemplate();
    private String baseUrl = "http://localhost:8080/api/users";
    private HttpEntity<String> entity;
    private ResponseEntity<String> responseEntity;

    @Given("the user is an employee")
    public void theUserIsAnEmployee() throws URISyntaxException, JsonProcessingException, JSONException {

        AuthenticationDTO loginDto = new AuthenticationDTO("Bank@g.com", "bankpass");
        validateLogin(loginDto);

       validateLogin(loginDto);

    }

    @When("retrieving all users")
    public void retrievingAllUsers() throws URISyntaxException {
        URI uri = new URI(baseUrl);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        entity = new HttpEntity<>(headers);
        responseEntity = template.exchange(uri, HttpMethod.GET, entity, String.class);
    }

    @Then("show http status {int}")
    public void showHttpStatus(int statusCode) {
        Assert.assertEquals(statusCode, responseEntity.getStatusCodeValue());
    }

    @When("registering a new user")
    public void registerANewUser() throws URISyntaxException, JsonProcessingException {
       CreateUserDTO user = new CreateUserDTO("Bank@g.com", "2266", "bank", "bestbank", "00-00-00", "bankpass",
                                                String.valueOf(UserRole.EMPLOYEE), UserStatus.ACTIVE);
        URI uri = new URI(baseUrl);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(user), headers);
        responseEntity = template.postForEntity(uri, entity, String.class);

    }

    @When("retrieving a user with id {string}")
    public void retrievingAUserWithId(String id) throws URISyntaxException {
        URI uri = new URI(baseUrl + "/" + id);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        entity = new HttpEntity<>("", headers);
        responseEntity = template.exchange(uri, HttpMethod.GET, entity, String.class);
    }


    public void validateLogin(AuthenticationDTO loginDto) throws URISyntaxException, JsonProcessingException, JSONException {

        AuthenticationDTO login = new AuthenticationDTO(loginDto.getUserName(), loginDto.getPassword());

        URI uri = new URI("http://localhost:8080/api/login");
        headers.setContentType(MediaType.APPLICATION_JSON);

        entity = new HttpEntity<>(mapper.writeValueAsString(login), headers);
        responseEntity = template.postForEntity(uri, entity, String.class);

        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        token = jsonObject.getString("Token");
    }

    @Given("the user is a customer")
    public void theUserIsACustomer() throws URISyntaxException, JsonProcessingException, JSONException {
        AuthenticationDTO loginDto = new AuthenticationDTO("willliamSmith@gmail.com", "william123");
        validateLogin(loginDto);
    }


    @When("updating a user with id {Long}")
    public void updatingAUserWithId(UpdateUserDTO body, Long id) throws URISyntaxException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        UpdateUserDTO user = new UpdateUserDTO(body.getUserName(), body.getMobileNumber(), body.getFirstName(), body.getLastName(), body.getDateOfBirth(), body.getPassword(), body.getRoles(), body.getStatus());        URI uri = new URI(baseUrl + "/" + id);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(user), headers);
        responseEntity = template.exchange(uri, HttpMethod.PUT, entity, String.class);

    }
}
