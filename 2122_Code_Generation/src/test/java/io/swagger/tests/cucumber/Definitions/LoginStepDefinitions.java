package io.swagger.tests.cucumber.Definitions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.swagger.model.DTOs.AuthenticationDTO;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class LoginStepDefinitions {

    String port = "8080";
    String loginUsername;
    String loginPassword;
    String token;

    DefinitionStepHelper stepHelper = new DefinitionStepHelper();

    String url = "http://localhost:8080/api/login";


    private ObjectMapper mapper = new ObjectMapper();
    private HttpHeaders headers = new HttpHeaders();
    private RestTemplate template = new RestTemplate();
    private HttpEntity<String> entity;
    private ResponseEntity<String> response;

    @Given("Credentials are {string} as a username and {string} as a password")
    public void credentials(String username, String password){
        loginUsername = username;
        loginPassword = password;
    }

    @When("Login")
    public void loginStep() throws JSONException, URISyntaxException, JsonProcessingException {
        try {
            response = stepHelper.validateLogin(loginUsername, loginPassword);
        } catch (HttpClientErrorException.Unauthorized exception) {
            response = new ResponseEntity<>(exception.getStatusCode());
        }
    }

    @Then("show login http status {int}")
    public void showLoginHttpStatus(int statusCode) {
        Assert.assertEquals(statusCode, response.getStatusCodeValue());
    }
}
