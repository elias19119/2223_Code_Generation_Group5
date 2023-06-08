package io.swagger.tests.cucumber.Definitions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.swagger.model.Account;
import io.swagger.model.DTOs.AuthenticationDTO;
import io.swagger.model.DTOs.CreateAccountDTO;
import io.swagger.model.DTOs.UpdateAccountDTO;
import io.swagger.model.Enums.AccountType;
import io.swagger.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AccountStepDefinitions {

    private String port = "8080";
    private String employeeUsername = "employee@g.com";
    private String employeePassword = "user";
    private String token;
    DefinitionStepHelper stepHelper = new DefinitionStepHelper();
    private ObjectMapper mapper = new ObjectMapper();
    private HttpHeaders headers = new HttpHeaders();
    private RestTemplate template = new RestTemplate();
    private HttpEntity<String> entity;
    private ResponseEntity<String> response;
    private String responseBody;
    String Id = "";

    private String url = "http://localhost:" + port + "/api/accounts";

    @Given("User is employee")
    public void givenTheUserIsEmployee() throws JSONException, URISyntaxException, JsonProcessingException {
        AuthenticationDTO loginDto = new AuthenticationDTO(employeeUsername, employeePassword);
        validateLogin(loginDto);
    }
    @When("an Employee request list of all accounts")
    public void whenEmployeeRequestAllAccounts() throws URISyntaxException {
        URI uri = new URI(url);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        entity = new HttpEntity<>(headers);

        response = template.exchange(uri, HttpMethod.GET, entity, String.class);
        this.responseBody = response.getBody();
    }
    @When("Create account with valid details")
    public void whenCreateAnAccount() throws URISyntaxException, JsonProcessingException {
        User user = stepHelper.registerRandomUser(token);

        URI uri = new URI(url);

        CreateAccountDTO account = new CreateAccountDTO(user.getId(), AccountType.CURRENT);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        HttpEntity<CreateAccountDTO> entity = new HttpEntity<>(account, headers);

        response = template.exchange(uri, HttpMethod.POST, entity, String.class);
    }
    @When("Retrieve an account by {string} user ID")
    public void WhenRetrieveAccountByUserId(String validity) throws URISyntaxException, JSONException {
        whenEmployeeRequestAllAccounts();

        JSONArray result = new JSONArray(responseBody);
        JSONObject account = result.getJSONObject(1);
        if(validity.equals("valid")){
            Id = account.getString("id");
        }else{
            Id = "notExist";
        }

        URI uri = new URI(url +"/"+ Id);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        entity = new HttpEntity<>("", headers);
        try{
            response = template.exchange(uri, HttpMethod.GET, entity, String.class);
        }catch (HttpClientErrorException.BadRequest exception){
            response = new ResponseEntity<>(exception.getStatusCode());
        }

    }
    @When("Update the details of an account with {string} ID")
    public void updateAccountByEmployeeShouldReturnOk(String validity) throws URISyntaxException, JSONException {
        whenEmployeeRequestAllAccounts();

        JSONArray result = new JSONArray(responseBody);
        JSONObject account = result.getJSONObject(1);

        UpdateAccountDTO updateAccountDTO = new UpdateAccountDTO();
        updateAccountDTO.setAbsoluteLimit(100);

        if(validity.equals("valid")){
            Id = account.getString("id");
        }
        else{
            Id = "notExist";
        }

        URI uri = new URI(url +"/"+ Id);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        HttpEntity<UpdateAccountDTO> updateEntity = new HttpEntity<>(updateAccountDTO, headers);

        try {
            response = template.exchange(uri, HttpMethod.PUT, updateEntity, String.class);
        } catch (HttpClientErrorException exception) {
            response = new ResponseEntity<>(exception.getStatusCode());
        }
    }
    @When("Delete an account")
    public void deleteAnAccountAsEmployee() throws URISyntaxException, JSONException {
        whenEmployeeRequestAllAccounts();

        JSONArray result = new JSONArray(responseBody);
        JSONObject account = result.getJSONObject(1);

        Id = account.getString("id");

        URI uri = new URI(url +"/"+ Id);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        entity = new HttpEntity<>("", headers);

        try {
            response = template.exchange(uri, HttpMethod.DELETE, entity, String.class);
        } catch (HttpClientErrorException exception) {
            response = new ResponseEntity<>(exception.getStatusCode());
        }
    }
    @Then("show http status code {int}")
    public void thenShowHttpStatus(int statusCode) {
        Assert.assertEquals(statusCode, response.getStatusCodeValue());
    }
    @Then("all accounts returned and show status {int}")
    public void allAccountsReturned(int statusCode){
        assertNotNull(responseBody);
        assertEquals(statusCode, response.getStatusCode().value());
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
