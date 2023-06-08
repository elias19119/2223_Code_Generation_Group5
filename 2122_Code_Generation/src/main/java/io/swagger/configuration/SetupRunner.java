package io.swagger.configuration;

import io.swagger.Repository.AccountRepository;
import io.swagger.Repository.TransactionRepository;
import io.swagger.Repository.UserRepository;
import io.swagger.model.Account;
import io.swagger.model.Enums.AccountStatus;
import io.swagger.model.Enums.AccountType;
import io.swagger.model.Enums.UserRole;
import io.swagger.model.Enums.UserStatus;
import io.swagger.model.Transaction;
import io.swagger.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.logging.Logger;

@Component
public class SetupRunner implements ApplicationRunner {

    AccountRepository accountRepository;
    UserRepository userRepository;
    @Autowired
    TransactionRepository transactionRepository;

    private final static Logger logger = Logger.getLogger(SetupRunner.class.getName());
    @Override
    public void run(ApplicationArguments args) throws Exception {
        CreateDummyData();
    }

    public SetupRunner(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public void CreateDummyData(){
        Account bankAccount = Account.builder().IBANNo("NL82INGB4787393871").accountType(AccountType.CURRENT)
                .balance(0).dateOfOpening(LocalDateTime.now()).accountStatus(AccountStatus.ACTIVE).transactionLimit(1000).absoluteLimit(1000).dayLimit(1000).balance(500).build();

        Account userAccount = Account.builder().IBANNo("NL21INGB3487393870").accountType(AccountType.CURRENT)
                .balance(0).dateOfOpening(LocalDateTime.now()).accountStatus(AccountStatus.ACTIVE).transactionLimit(1000).absoluteLimit(1000).dayLimit(1000).balance(500).build();

        Account employeeAccount = Account.builder().IBANNo("NL21INGB3487394444").accountType(AccountType.CURRENT)
                .balance(0).dateOfOpening(LocalDateTime.now()).accountStatus(AccountStatus.ACTIVE).transactionLimit(1000).absoluteLimit(1000).dayLimit(1000).balance(500).build();


        accountRepository.save(bankAccount);
        accountRepository.save(userAccount);
        accountRepository.save(employeeAccount);

        User bankUser = User.builder().userName("Bank@g.com").mobileNumber("2266")
                .firstName("Bank").lastName("bestbank").DateOfBirth("25-12-2002").password("bankpass")
                .roles(UserRole.BANK).accounts(Collections.singleton(bankAccount))
                .status(UserStatus.ACTIVE).build();

        User customerUser = User.builder().userName("customer@g.com").mobileNumber("06-45281368 ")
                .firstName("john").lastName("doe").DateOfBirth("01-05-1999").password("user")
                .roles(UserRole.CUSTOMER).accounts(Collections.singleton(userAccount))
                .status(UserStatus.ACTIVE).build();

        User employeeUser = User.builder().userName("employee@g.com").mobileNumber("06-38116147 ")
                .firstName("Man").lastName("jan").DateOfBirth("02-10-1997").password("user")
                .roles(UserRole.EMPLOYEE).accounts(Collections.singleton(employeeAccount))
                .status(UserStatus.ACTIVE).build();

        User emptyAccountUser = User.builder().userName("empty@g.com").mobileNumber("06-80331782 ")
                .firstName("test").lastName("user").DateOfBirth("06-07-1998").password("emptyuser")
                .roles(UserRole.EMPLOYEE).accounts(null)
                .status(UserStatus.ACTIVE).build();

        userRepository.save(bankUser);
        userRepository.save(customerUser);
        userRepository.save(employeeUser);
        userRepository.save(emptyAccountUser);

        Transaction transaction = Transaction.builder().dateOfTransaction(LocalDate.now())
                .userId(bankUser.getId()).fromIban("NL82INGB4787393871").toIban("NL82INGB3487393870")
                .transferAmount(100).build();
        transactionRepository.save(transaction);
    }

}
