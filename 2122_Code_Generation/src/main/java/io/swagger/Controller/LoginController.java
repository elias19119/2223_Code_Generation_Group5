package io.swagger.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.Service.UserService;
import io.swagger.exceptions.ApiRequestException;
import io.swagger.model.Account;
import io.swagger.model.DTOs.AuthenticationDTO;
import io.swagger.model.DTOs.BearerTokenDTO;
import io.swagger.model.DTOs.CreateAccountDTO;
import io.swagger.model.DTOs.CreateUserDTO;
import io.swagger.model.User;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private  ObjectMapper objectMapper;
    @Autowired
    private  HttpServletRequest request;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<BearerTokenDTO> generateToken(@RequestBody AuthenticationDTO authRequest) {
        try{
            String token = userService.login(authRequest);
            BearerTokenDTO tokenDTO =new BearerTokenDTO();
            tokenDTO.setBearerToken(token);
            return new ResponseEntity<BearerTokenDTO>(tokenDTO,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiRequestException("please insert a vlaid Username and password ",HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody CreateUserDTO userDTO) throws Exception {
        if(userDTO.getUserName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        try{
            return new ResponseEntity<>(HttpStatus.CREATED).status(201).body(userService.creatUser(userDTO));
        }
        catch (Exception e){
            return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
        }
    }
}
