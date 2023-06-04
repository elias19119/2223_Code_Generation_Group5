package io.swagger.tests.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.Controller.UserController;
import io.swagger.Repository.UserRepository;
import io.swagger.Security.JwtTokenProvider;
import io.swagger.Service.AccountService;
import io.swagger.Service.TransactionService;
import io.swagger.Service.UserService;
import io.swagger.model.Account;
import io.swagger.model.DTOs.UpdateUserDTO;
import io.swagger.model.Enums.AccountStatus;
import io.swagger.model.Enums.AccountType;
import io.swagger.model.Enums.UserRole;
import io.swagger.model.Enums.UserStatus;
import io.swagger.model.User;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@Import(UserController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes= WebApplicationContext.class)
public class UserControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserService userService;

    @MockBean
    public User user;

    ObjectMapper mapper = new ObjectMapper();

    List<User> users = new ArrayList<>();


    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();

        User bankUser = User.builder().userName("Bank@g.com").mobileNumber("2266")
                .firstName("Bank").lastName("bestbank").DateOfBirth("00-00-00").password("bankpass")
                .roles(UserRole.BANK).accounts(null)
                .status(UserStatus.ACTIVE).build();

        User customerUser = User.builder().userName("customer@g.com").mobileNumber("5541")
                .firstName("john").lastName("doe").DateOfBirth("00-00-00").password("user")
                .roles(UserRole.CUSTOMER).accounts(null)
                .status(UserStatus.ACTIVE).build();

        User employeeUser = User.builder().userName("employee@g.com").mobileNumber("9423")
                .firstName("Man").lastName("jan").DateOfBirth("00-00-00").password("user")
                .roles(UserRole.EMPLOYEE).accounts(null)
                .status(UserStatus.ACTIVE).build();

        users.add(employeeUser);
        users.add(bankUser);
        users.add(customerUser);
    }



    @WithMockUser(username = "employee", roles = {"EMPLOYEE", "CUSTOMERS"})
    @Test
    public void getAllUsersShouldReturnJsonArray() throws Exception {
        Mockito.when(userService.getAllUsers())
                .thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @WithMockUser(username = "employee", roles = {"EMPLOYEE", "CUSTOMERS"})
    @Test
    public void getAllUsersShouldReturnOk() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk());
    }


    @WithMockUser(username = "employee", roles = {"EMPLOYEE", "CUSTOMERS"})
    @Test
    public void getUserShouldReturnAUser() throws Exception {
        user = User.builder().id(UUID.randomUUID()).userName("Bank@g.com").mobileNumber("2266")
                .firstName("Bank").lastName("bestbank").DateOfBirth("00-00-00").password("bankpass")
                .roles(UserRole.BANK).accounts(null)
                .status(UserStatus.ACTIVE).build();

        Mockito.when(userService.findUserById(user.getId()))
                .thenReturn(Optional.ofNullable(user));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/" + user.getId()).with(csrf()))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "employee", roles = {"EMPLOYEE", "CUSTOMERS"})
    @Test
    public void getUserWithWrongIdReturnsNotFound() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/users" + 25).with(csrf()))
                .andExpect(status().isNotFound());
    }


    @WithMockUser(username = "employee", roles = {"EMPLOYEE", "CUSTOMERS"})
    @Test
    public void deleteUserShouldDeactivateAUser() throws Exception {

        user = User.builder().id(UUID.randomUUID()).userName("Bank@g.com").mobileNumber("2266")
                .firstName("Bank").lastName("bestbank").DateOfBirth("00-00-00").password("bankpass")
                .roles(UserRole.BANK).accounts(null)
                .status(UserStatus.ACTIVE).build();

        Mockito.doNothing().when(userService).deleteUser(user.getId());
    }

    @WithMockUser(username = "employee", roles = {"EMPLOYEE", "CUSTOMERS"})
    @Test
    public void updateUserShouldReturnOK() throws Exception {
        user = User.builder().id(UUID.randomUUID()).userName("Bank@g.com").mobileNumber("2266")
                .firstName("Bank").lastName("bestbank").DateOfBirth("00-00-00").password("bankpass")
                .roles(UserRole.BANK).accounts(null)
                .status(UserStatus.ACTIVE).build();

        this.mockMvc.perform(MockMvcRequestBuilders.put("/users/" + user.getId()).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON_VALUE).
                                content(mapper.writeValueAsString(this.user)))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "employee", roles = {"EMPLOYEE", "CUSTOMERS"})
    @Test
    public void updateUserShouldUpdateAUser() throws Exception {
        user = User.builder().id(UUID.randomUUID()).userName("Bank@g.com").mobileNumber("2266")
                .firstName("Bank").lastName("bestbank").DateOfBirth("00-00-00").password("bankpass")
                .roles(UserRole.BANK).accounts(null)
                .status(UserStatus.ACTIVE).build();

        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setFirstName("Mark");

        Mockito.doNothing().when(userService).updateUser(user.getId(),updateUserDTO);
    }
}
