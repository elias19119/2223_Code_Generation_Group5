package io.swagger.tests.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.Controller.AccountController;
import io.swagger.Controller.UserController;
import io.swagger.Repository.AccountRepository;
import io.swagger.Repository.UserRepository;
import io.swagger.Service.AccountService;
import io.swagger.Service.UserService;
import io.swagger.model.Account;
import io.swagger.model.DTOs.UpdateAccountDTO;
import io.swagger.model.DTOs.UpdateUserDTO;
import io.swagger.model.Enums.AccountStatus;
import io.swagger.model.Enums.AccountType;
import io.swagger.model.Enums.UserRole;
import io.swagger.model.Enums.UserStatus;
import io.swagger.model.User;
import org.hamcrest.Matchers;
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

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AccountController.class)
@Import(AccountController.class)
@ContextConfiguration(classes= WebApplicationContext.class)
class AccountControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    private UserService userService;
    @MockBean
    private AccountService accountService;

    @MockBean
    public User user;
    @MockBean
    public Account account;

    List<User> users = new ArrayList<>();
    List<Account> accounts = new ArrayList<>();
    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    @Test
    public void setup(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();

        createMockAccounts();
        createMockUsers(accounts);
    }
    @WithMockUser(username = "employee", roles = {"EMPLOYEE", "CUSTOMERS"})
    @Test
    public void getAllAccountsShouldReturnJsonArray() throws Exception{
        Mockito.when(accountService.findAccountsByFilter(null,null))
                .thenReturn(accounts);

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andReturn();

        Mockito.verify(accountService, Mockito.times(1)).findAccountsByFilter(null,null);
    }

    @WithMockUser(username = "employee", roles = {"EMPLOYEE", "CUSTOMERS"})
    @Test
    public void getAllAccountsShouldReturnOk() throws Exception{
        Mockito.when(accountService.findAccountsByFilter(null,null))
                .thenReturn(accounts);

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts"))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "employee", roles = {"EMPLOYEE", "CUSTOMERS"})
    @Test
    public void getAccountByAccountIdReturnsAnAccount() throws Exception{
        Account mockAccount = accounts.get(0);
        Mockito.when(accountService.findAccountById(mockAccount.getId()))
                .thenReturn(Optional.ofNullable(mockAccount));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/accounts/" + mockAccount.getId()).with(csrf()))
                .andExpect(status().isOk());
    }
    @WithMockUser(username = "employee", roles = {"EMPLOYEE", "CUSTOMERS"})
    @Test
    public void creatingAnAccountReturnsCreated() throws Exception {
        Account account = Account.builder().id(UUID.randomUUID()).IBANNo(accountService.generateRandomIBAN()).accountType(AccountType.CURRENT).balance(500).dateOfOpening(LocalDate.now()).accountStatus(AccountStatus.ACTIVE).transactionLimit(20).dayLimit(2000).absoluteLimit(100).build();
        ObjectMapper objectMapper = new ObjectMapper();
        //register the jackson-datatype-jsr310 module
        objectMapper.registerModule(new JavaTimeModule());

        String accountJson = objectMapper.writeValueAsString(account);

        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accountJson))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @WithMockUser(username = "employee", roles = {"EMPLOYEE", "CUSTOMERS"})
    @Test
    public void updateAccountByIdReturnsOk() throws Exception{
        Account account = accounts.get(0);
        account.setAbsoluteLimit(20);
        // convert the account object to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        //register the jackson-jsr310 module
        objectMapper.registerModule(new JavaTimeModule());

        String accountJson = objectMapper.writeValueAsString(account);

        mockMvc.perform(MockMvcRequestBuilders.put("/accounts/" + account.getId())
                        .content(accountJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username = "employee", roles = {"EMPLOYEE", "CUSTOMERS"})
    @Test
    public void updateAccountShouldUpdateAccount() throws Exception{
        Account account = accounts.get(0);
        UpdateAccountDTO updateAccountDTO = new UpdateAccountDTO();
        updateAccountDTO.setAbsoluteLimit(20);

        Mockito.doNothing().when(accountService).updateAccount(account.getId(), updateAccountDTO);
    }

    @WithMockUser(username = "employee", roles = {"EMPLOYEE", "CUSTOMERS"})
    @Test
    public void deleteAccountByIdShouldDeactivateAccount() throws Exception{
        Account account = accounts.get(0);
        UpdateAccountDTO updateAccountDTO = new UpdateAccountDTO();
        updateAccountDTO.setAccountStatus(AccountStatus.CLOSED);
        Mockito.doNothing().when(accountService).updateAccount(account.getId(), updateAccountDTO);
    }
    private void createMockAccounts(){
        List<Account> a = new ArrayList<>();
        //Bank account NL01INHO0000000001

        Account account1 = Account.builder().id(UUID.randomUUID()).IBANNo("NL01INHO0000000001").accountType(AccountType.CURRENT).balance(2500).dateOfOpening(LocalDate.now()).accountStatus(AccountStatus.ACTIVE).transactionLimit(20).dayLimit(2000).absoluteLimit(100).build();
        //Accounts generated with random IBAN
        Account account2 = Account.builder().id(UUID.randomUUID()).IBANNo(accountService.generateRandomIBAN()).accountType(AccountType.CURRENT).balance(500).dateOfOpening(LocalDate.now()).accountStatus(AccountStatus.ACTIVE).transactionLimit(20).dayLimit(2000).absoluteLimit(100).build();
        Account account3 = Account.builder().id(UUID.randomUUID()).IBANNo(accountService.generateRandomIBAN()).accountType(AccountType.CURRENT).balance(500).dateOfOpening(LocalDate.now()).accountStatus(AccountStatus.ACTIVE).transactionLimit(20).dayLimit(2000).absoluteLimit(100).build();

        accounts.add(account1);
        accounts.add(account2);
        accounts.add(account3);
    }
    private void createMockUsers(List<Account> a){
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

        // Add accounts to users
        bankUser.addAccount(a.get(0));
        customerUser.addAccount(a.get(1));
        employeeUser.addAccount(a.get(2));

        users.add(employeeUser);
        users.add(bankUser);
        users.add(customerUser);

    }
}