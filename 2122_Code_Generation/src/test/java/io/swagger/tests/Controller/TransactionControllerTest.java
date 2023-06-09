package io.swagger.tests.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.Controller.TransactionController;
import io.swagger.Repository.AccountRepository;
import io.swagger.Repository.TransactionRepository;
import io.swagger.Repository.UserRepository;
import io.swagger.Security.JwtTokenProvider;
import io.swagger.Service.AccountService;
import io.swagger.Service.TransactionService;
import io.swagger.Service.UserService;
import io.swagger.model.Account;
import io.swagger.model.DTOs.CreateTransactionDTO;
import io.swagger.model.Enums.AccountStatus;
import io.swagger.model.Enums.AccountType;
import io.swagger.model.Enums.UserRole;
import io.swagger.model.Enums.UserStatus;
import io.swagger.model.Responses.CreateTransactionResponseDTO;
import io.swagger.model.Transaction;
import io.swagger.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TransactionController.class)
@Import(TransactionController.class)
@ContextConfiguration(classes= WebApplicationContext.class)
public class TransactionControllerTest {
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    private TransactionRepository transactionRepository;
    @MockBean
    private UserService userService;
    @MockBean
    private TransactionService transactionService;
    @MockBean
    private AccountService accountService;

    @MockBean
    public User user;
    @MockBean
    public Account account;
    @MockBean
    public Transaction transaction;

    ObjectMapper mapper = new ObjectMapper();

    List<User> users = new ArrayList<>();
    List<Account> accounts = new ArrayList<>();
    List<Transaction> transactions = new ArrayList<>();


    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    CreateTransactionResponseDTO response;

    @BeforeEach
    @Test
    public void setup(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
        createMockAccounts();
        createMockUsers(accounts);
        jwtTokenProvider = new JwtTokenProvider();
    }

    @WithMockUser(username = "employee", roles = {"EMPLOYEE", "CUSTOMERS"})
    @Test
    public void makeTransactionShouldReturnCreated() throws Exception{
        Transaction transaction = new Transaction();
        CreateTransactionDTO txDTO = new CreateTransactionDTO();
        txDTO.setReceiverIban(accounts.get(1).getIBANNo());
        txDTO.setSenderIban(accounts.get(2).getIBANNo());
        txDTO.setTransferAmount(100);

        ObjectMapper objectMapper = new ObjectMapper();

        //need correction
        String username = users.get(0).getUserName();
        UserRole role = users.get(0).getRoles();

        String jwtUserToken = createToken(username, role);

        TransactionService transactionServiceSpy = Mockito.spy(transactionService);
        CreateTransactionResponseDTO expectedResponse = new CreateTransactionResponseDTO();

        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class)))
                .thenReturn(new Transaction());

        Mockito.doReturn(expectedResponse)
                .when(transactionServiceSpy)
                .makeTransaction(txDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/transactions")
                        .header("Authorization", "Bearer " + jwtUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(txDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @WithMockUser(username = "employee", roles = {"EMPLOYEE", "CUSTOMERS"})
    @Test
    public void getTransactionByIBANShouldReturnsJSON() throws Exception{
        CreateTransactionDTO transactionDTO = new CreateTransactionDTO();
        transactionDTO.setSenderIban(accounts.get(1).getIBANNo());
        transactionDTO.setReceiverIban(accounts.get(2).getIBANNo());
        transactionDTO.setTransferAmount(100);

        CreateTransactionResponseDTO transactionResponse = transactionService.makeTransaction(transactionDTO);

        when(transactionService.makeTransaction(any(CreateTransactionDTO.class)))
                .thenReturn(transactionResponse);

        ResultActions resultActions = mockMvc.perform(get("/transactions/{IBAN}", accounts.get(1).getIBANNo())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
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
    private String createToken(String username, UserRole role){
        String secretKey = "secret-key";
        long validityInMilliseconds = 3600000;

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("auth", new SimpleGrantedAuthority(role.name()));

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return  Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}
