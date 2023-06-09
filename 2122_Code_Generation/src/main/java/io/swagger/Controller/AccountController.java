package io.swagger.Controller;

import io.swagger.Service.AccountService;
import io.swagger.exceptions.ApiRequestException;
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
       try {
           return new ResponseEntity<>(HttpStatus.OK).status(200).body(accountService.findAccountsByFilter(offset,limit));

       }catch (Exception e){
           throw new ApiRequestException(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @GetMapping("/accounts/{id}")
    @PreAuthorize("hasAnyRole('ROLE_BANK','ROLE_EMPLOYEE')")
    public ResponseEntity<Account> getUserById(@PathVariable("id") UUID id) throws Exception {
        try {
           return new ResponseEntity<Account>(HttpStatus.OK).status(200).body(accountService.findAccountById(id).get());

        }catch (Exception e){
            throw new ApiRequestException(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/accounts")
     @PreAuthorize("hasAnyRole('ROLE_BANK','ROLE_EMPLOYEE')")
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountDTO account) throws Exception {
        try{
            return new ResponseEntity<>(HttpStatus.CREATED).status(201).body(accountService.AddAccount(account));
        }
        catch (Exception e){
            throw new ApiRequestException(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/accounts/{id}")
    @PreAuthorize("hasAnyRole('ROLE_BANK','ROLE_EMPLOYEE')")
    public ResponseEntity<Void> updateAccount(@PathVariable UUID id, @RequestBody UpdateAccountDTO account) throws Exception {
        try {
            accountService.updateAccount(id, account);
            return new ResponseEntity<Void>(HttpStatus.OK);

        }catch (Exception e){
            throw new ApiRequestException(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/accounts/{id}")
    @PreAuthorize("hasAnyRole('ROLE_BANK','ROLE_EMPLOYEE')")
    public ResponseEntity<Void> deleteAccount(@PathVariable("id") UUID id) throws Exception {
        try {
            accountService.deleteAccount(id);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }catch (Exception e){
            throw new ApiRequestException(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/accounts/balance")
    @PreAuthorize("hasAnyRole('ROLE_BANK','ROLE_EMPLOYEE')")
    public ResponseEntity<String> balanceCheck(@RequestParam("userId")UUID userId,@RequestParam("IBAN") String IBAN) throws Exception {
        try {
            return new ResponseEntity<String>(HttpStatus.CREATED).status(200).body(accountService.balanceCheck(userId,IBAN));

        }catch (Exception e){
            throw new ApiRequestException(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }

}


