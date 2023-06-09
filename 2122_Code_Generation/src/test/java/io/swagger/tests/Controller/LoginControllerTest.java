package io.swagger.tests.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.Controller.LoginController;
import io.swagger.Controller.UserController;
import io.swagger.Repository.UserRepository;
import io.swagger.Service.UserService;
import io.swagger.model.DTOs.AuthenticationDTO;
import io.swagger.model.Enums.UserRole;
import io.swagger.model.Enums.UserStatus;
import io.swagger.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LoginController.class)
@Import(LoginController.class)
@ContextConfiguration(classes= WebApplicationContext.class)
public class LoginControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    User user;

    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }
    @WithMockUser(username = "employee", roles = {"EMPLOYEE", "CUSTOMERS"})
    @Test
    public void loginReturnsOk() throws Exception {

        AuthenticationDTO login = new AuthenticationDTO("Bank@g.com","bankpass");
        ObjectMapper mapper=  new ObjectMapper();
        this.mockMvc.perform(MockMvcRequestBuilders.post("/login").with(csrf())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(login)))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "rbanks@gmail.com", roles = {"EMPLOYEE"})
    @Test
    public void registerUserShouldReturnCreated() throws Exception {

        user = User.builder().userName("Bank@g.com").mobileNumber("2266")
                .firstName("Bank").lastName("bestbank").DateOfBirth("00-00-00").password("bankpass")
                .roles(UserRole.BANK).accounts(null)
                .status(UserStatus.ACTIVE).build();

        this.mockMvc.perform(MockMvcRequestBuilders.post("/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isCreated());
    }

    @WithMockUser(username = "rbanks@gmail.com", roles = {"EMPLOYEE"})
    @Test
    public void registerUserShouldReturnBadRequest() throws Exception {

        user = null;

        this.mockMvc.perform(MockMvcRequestBuilders.post("/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

}
