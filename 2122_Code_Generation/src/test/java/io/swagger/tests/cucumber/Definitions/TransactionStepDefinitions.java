package io.swagger.tests.cucumber.Definitions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.swagger.model.DTOs.AuthenticationDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.AssertTrue;
import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.test.util.AssertionErrors.assertTrue;

public class TransactionStepDefinitions {
    private String port = "8080";
    private String customerUsername = "customer@g.com";
    private String customerPassword = "user";
    private String token;
    DefinitionStepHelper stepHelper = new DefinitionStepHelper();
    private ObjectMapper mapper = new ObjectMapper();
    private HttpHeaders headers = new HttpHeaders();
    private RestTemplate template = new RestTemplate();
    private HttpEntity<String> entity;
    private ResponseEntity<String> response;
    private String responseBody;
    String Id = "";

    @Given("User is customer")
    public void userIsCustomer() throws JSONException, URISyntaxException, JsonProcessingException {
        AuthenticationDTO loginDto = new AuthenticationDTO(customerUsername, customerPassword);
        validateLogin(loginDto);
    }

    @And("User has balance in their account to transfer {int}")
    public void checkForSufficientFund(int amount) throws JSONException, URISyntaxException, JsonProcessingException {
        String input = stepHelper.getUserIDAndIBANBalanceByUsername(customerUsername);
        String[] userAccountDetails = input.split("/");
        int employeeBalance = Integer.parseInt(userAccountDetails[2]);

        assertTrue("Employee have sufficient fund", employeeBalance >= amount);
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

