package io.swagger.Service;


import io.swagger.Repository.AccountRepository;
import io.swagger.Repository.UserRepository;
import io.swagger.model.Account;
import io.swagger.model.DTOs.CreateAccountDTO;
import io.swagger.model.DTOs.UpdateAccountDTO;
import io.swagger.model.Enums.AccountStatus;
import io.swagger.model.Enums.AccountType;
import io.swagger.model.Enums.UserStatus;
import io.swagger.model.Responses.GetUserResponseDTO;
import io.swagger.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public Account AddAccount(CreateAccountDTO accountDto) throws Exception {
        Optional<User> user = userRepository.findById(accountDto.getUserId());

        if (!user.isPresent()) {
            throw new Exception("User was not found");
        }else {
            AccountType account_Type = accountDto.getAccountType();
            if (account_Type == null) {
                throw new Exception("account type can not be null");
            }
                Account account = new Account();
                account.setIBANNo(generateRandomIBAN());
                account.setBalance(0);
                account.setAbsoluteLimit(5000);
                account.setTransactionLimit(3000);
                account.setDateOfOpening(LocalDate.now());
                account.setAccountType(accountDto.getAccountType());
                account.setAccountStatus(AccountStatus.ACTIVE);
                account.setDayLimit(1000);
                accountRepository.save(account);

                user.get().setStatus(UserStatus.ACTIVE);
                user.get().getAccounts().add(account);
                userRepository.save(user.get());
                return account;
        }

    }

    public void updateAccount(UUID id, UpdateAccountDTO accountDto) throws Exception {
        Optional<Account> a = accountRepository.findById(id);
        if (!a.isPresent()) {
            throw new Exception("account was not found");
        } else
            a.get().setAccountStatus(accountDto.getAccountStatus());
            a.get().setAccountType(accountDto.getAccountType());
            a.get().setAbsoluteLimit(accountDto.getAbsoluteLimit());
            a.get().setTransactionLimit(accountDto.getTransactionLimit());
            a.get().setDayLimit(accountDto.getDayLimit());
            accountRepository.save(a.get());
    }

    public Optional<Account> findAccountById(UUID id) throws Exception {

        Optional<Account> account =  accountRepository.findById(id);
        if(!account.isPresent()){
            throw new Exception("account was not found");
        }
        return  account;
    }

    public void deleteAccount(UUID id) throws Exception {
        Optional<Account> a = accountRepository.findById(id);
        if (!a.isPresent()) {
            throw new Exception("account was not found");
        } else
            a.get().setAccountStatus(AccountStatus.CLOSED);
            accountRepository.save(a.get());
    }

    public String balanceCheck(UUID userId,String IBAN) throws Exception{

        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()){
            for (Account account : user.get().getAccounts()){
                if(account.getIBANNo().equals(IBAN)){
                    return "Your Account Balance is: " + account.getBalance();
                }
            }
        }
        throw new Exception("Account was not found");
    }
    public String generateRandomIBAN() {
        StringBuilder iban = new StringBuilder("NL");

        Random random = new Random();

        // Generate random digits for the next four digits
        for (int i = 0; i < 2; i++) {
            iban.append(random.nextInt(10));
        }

        // Add bank name
        iban.append("INGB");

        // Generate random digits for the rest of the IBAN
        iban.append(String.format("%010d", random.nextInt(1000000000)));
        return iban.toString();
    }

    public List<Account> findAccountsByFilter(Integer offset, Integer limit) {
        List<Account> accountList;
        accountList = accountRepository.findAll();

        if(offset != null  && limit !=null ){
            int endIndex = Math.min(offset + limit, accountList.size());
            if (offset < endIndex) {
                accountList = accountList.subList(offset, endIndex);
            }
        }
        return accountList;
    }
}
