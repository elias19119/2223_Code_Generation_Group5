package io.swagger.Controller;

import io.swagger.Service.AccountService;
import io.swagger.model.Account;
import io.swagger.model.DTOs.CreateAccountDTO;
import io.swagger.model.DTOs.UpdateAccountDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class AccountController {

    private AccountService accountService;
    private final HttpServletRequest request;

    @GetMapping("/accounts")
    @PreAuthorize("hasAnyRole('ROLE_BANK','ROLE_EMPLOYEE')")
    public ResponseEntity<Iterable<Account>> fetchAllAccounts(@RequestParam(value = "offset", required = false) Integer offset,
                                                         @RequestParam(value = "limit", required = false) Integer limit)  {
        if (request.getParameter("offset") != null && request.getParameter("limit") != null) {
            return new ResponseEntity<>(HttpStatus.OK).status(200).body(accountService.getAccountsByLimitAndOffset(offset, limit));
        } else {
            return new ResponseEntity<>(HttpStatus.OK).status(200).body(accountService.getAllAccounts());
        }
    }

    @GetMapping("/accounts/{id}")
    @PreAuthorize("hasAnyRole('ROLE_BANK','ROLE_EMPLOYEE')")
    public ResponseEntity<Account> getUserById(@PathVariable("id") UUID id) throws Exception {
        try {
           return new ResponseEntity<Account>(HttpStatus.OK).status(200).body(accountService.findAccountById(id).get());

        }catch (Exception e){
            return new ResponseEntity<Account>(HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/accounts")
    // @PreAuthorize("hasAnyRole('ROLE_BANK','ROLE_EMPLOYEE')")
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountDTO account) throws Exception {
        try{
            return new ResponseEntity<>(HttpStatus.CREATED).status(201).body(accountService.AddAccount(account));
        }
        catch (Exception e){
            return new ResponseEntity<Account>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/accounts/{id}")
    @PreAuthorize("hasAnyRole('ROLE_BANK','ROLE_EMPLOYEE')")
    public void updateAccount(@PathVariable UUID id, @RequestBody UpdateAccountDTO account) throws Exception {
        accountService.updateAccount(id, account);
    }

    @DeleteMapping("/accounts/{id}")
    @PreAuthorize("hasAnyRole('ROLE_BANK','ROLE_EMPLOYEE')")
    public void deleteAccount(@PathVariable("id") UUID id) throws Exception {
        accountService.deleteAccount(id);
    }


    @GetMapping("/accounts/findIbans")
    public List<String> findIbansByFirstNameAndPhoneNumber(@RequestParam("firstName")String firstName,@RequestParam("phoneNumber") String phoneNumber) throws Exception {
        return accountService.findIbansByFirstNameAndPhoneNumber(firstName, phoneNumber);
    }

    @GetMapping("/accounts/balance")
    public String balanceCheck(@RequestParam("userId")UUID userId,@RequestParam("IBAN") String IBAN) throws Exception {
        return accountService.balanceCheck(userId, IBAN);
    }

}


