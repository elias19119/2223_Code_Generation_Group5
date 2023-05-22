package io.swagger.Controller;

import io.swagger.Service.AccountService;
import io.swagger.model.Account;
import io.swagger.model.DTOs.CreateAccountDTO;
import io.swagger.model.DTOs.UpdateAccountDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private AccountService accountService;


    @GetMapping("/accounts")
    @PreAuthorize("hasAnyRole('ROLE_BANK','ROLE_EMPLOYEE')")
    public List<Account> fetchAllAccounts(@RequestParam(value = "offset", required = false) Integer offset,
                                          @RequestParam(value = "limit", required = false) Integer limit)  {
        if (offset != null && limit != null) {
            return accountService.getAccountsByLimitAndOffset(offset, limit);
        } else {
            return accountService.getAllAccounts();
        }
    }

    @GetMapping("/accounts/{id}")
    @PreAuthorize("hasAnyRole('ROLE_BANK','ROLE_EMPLOYEE')")
    public Optional<Account> getUserById(@PathVariable("id") UUID id) throws Exception {
        return accountService.findAccountById(id);
    }


    @PostMapping("/accounts")
    // @PreAuthorize("hasAnyRole('ROLE_BANK','ROLE_EMPLOYEE')")
    public Account createAccount(@RequestBody CreateAccountDTO account) throws Exception {
        return accountService.AddAccount(account);
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

    @GetMapping("/balance")
    public String balanceCheck(@RequestParam("userId")UUID userId,@RequestParam("IBAN") String IBAN) throws Exception {
        return accountService.balanceCheck(userId, IBAN);
    }

}


