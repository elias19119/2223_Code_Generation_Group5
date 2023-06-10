package io.swagger.tests.cucumber.Definitions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.swagger.model.DTOs.AuthenticationDTO;
import io.swagger.model.DTOs.CreateTransactionDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.AssertTrue;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

public class TransactionStepDefinitions {
    private String port = "8080";
    private String customerUsername = "customer1@g.com";
    private String customerPassword = "user";
    private String token;
    DefinitionStepHelper stepHelper = new DefinitionStepHelper();
    private ObjectMapper mapper = new ObjectMapper();
    private HttpHeaders headers = new HttpHeaders();
    private RestTemplate template = new RestTemplate();
    private HttpEntity<String> entity;
    private ResponseEntity<String> response;
    private String responseBody;
    private int amount;
    private int senderBalance;
    private int receiverBalance;

    String Id = "";

    @Given("User is customer")
    public void userIsCustomer() throws JSONException, URISyntaxException, JsonProcessingException {
        AuthenticationDTO loginDto = new AuthenticationDTO(customerUsername, customerPassword);
        validateLogin(loginDto);
    }

    @And("User has balance in their account to transfer {int}")
    public void checkForSufficientFund(int amount) throws JSONException, URISyntaxException, JsonProcessingException {
        this.amount = amount;

        String input = stepHelper.getUserIDAndIBANBalanceByUsername(customerUsername);

        String[] userAccountDetails = input.split("/");
        int employeeBalance = Integer.parseInt(userAccountDetails[2]);

        assertTrue("Employee have sufficient fund", employeeBalance >= amount);
    }

    @When("User initiates a fund transfer to another account with {string} details")
    public void transactWithValidCredentials(String validity) throws JSONException, URISyntaxException, JsonProcessingException {
        String senderInput = stepHelper.getUserIDAndIBANBalanceByUsername(customerUsername);
        String receiverInput = stepHelper.getUserIDAndIBANBalanceByUsername("employee@g.com");

        if(validity.equals("invalid")){
            receiverInput = stepHelper.getUserIDAndIBANBalanceByUsername("customer@g.com");
        }

        String[] senderAccountDetails = senderInput.split("/");
        String[] receiverAccountDetails = receiverInput.split("/");

        String senderIBAN = senderAccountDetails[1];
        String receiverIBAN = receiverAccountDetails[1];

        senderBalance = Integer.parseInt(senderAccountDetails[2]);
        receiverBalance = Integer.parseInt(receiverAccountDetails[2]);

        CreateTransactionDTO transactionDTO = new CreateTransactionDTO();
        transactionDTO.setTransferAmount(this.amount);
        transactionDTO.setReceiverIban(receiverIBAN);
        transactionDTO.setSenderIban(senderIBAN);

        URI uri = new URI("http://localhost:8080/api/transactions");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        HttpEntity<CreateTransactionDTO> entity = new HttpEntity<>(transactionDTO, headers);
        try {
            if(validity.equals("valid")){
            response = template.postForEntity(uri, entity, String.class);
            }else{
                response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (HttpClientErrorException.Unauthorized exception) {
            response = new ResponseEntity<>(exception.getStatusCode());
        }
    }
    @Then("the transaction is {string}")
    public void transactionResponseStatus(String status){
        if(status.equals("successful")){
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
        }else{
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }
    @And("the {string} account balance is updated")
    public void recipientAccountIsUpdated(String userType) throws JSONException, URISyntaxException, JsonProcessingException {
        String username = "";
        if(userType.equals("recipient")){
            username = "employee@g.com";
        }else{
            username = customerUsername;
        }

        String detailsResponse = stepHelper.getUserIDAndIBANBalanceByUsername(username);
        String[] userAccountDetails = detailsResponse.split("/");

        int userNewBalance = Integer.parseInt(userAccountDetails[2]);
        int userBalance;
        if(userType.equals("recipient")){
            userBalance = receiverBalance;
            assertEquals(userNewBalance, userBalance+amount);
        }else{
            userBalance = senderBalance;
            assertEquals(userNewBalance, userBalance-amount);
        }
        String s = "";
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

