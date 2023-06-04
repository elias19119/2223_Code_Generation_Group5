package io.swagger.tests.cucumber.Definitions;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.model.DTOs.AuthenticationDTO;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
}
