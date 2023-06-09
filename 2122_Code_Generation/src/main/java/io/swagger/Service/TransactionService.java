package io.swagger.Service;

import io.swagger.Repository.AccountRepository;
import io.swagger.Repository.TransactionRepository;
import io.swagger.Repository.UserRepository;
import io.swagger.Security.JwtTokenProvider;
import io.swagger.model.Account;
import io.swagger.model.DTOs.CreateTransactionDTO;
import io.swagger.model.DTOs.DepositToCheckingAccountDTO;
import io.swagger.model.DTOs.WithdrawMoneyDTO;
import io.swagger.model.Enums.AccountStatus;
import io.swagger.model.Enums.UserRole;
import io.swagger.model.Enums.UserStatus;
import io.swagger.model.Responses.CreateTransactionResponseDTO;
import io.swagger.model.Responses.WithdrawMoneyResponseDTO;
import io.swagger.model.Transaction;
import io.swagger.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final JwtTokenProvider jwtUtil;
    private final UserRepository userRepository;
    private EntityManager em;


    public CreateTransactionResponseDTO makeTransaction(CreateTransactionDTO transaction) throws Exception {
        if (transaction.getTransferAmount() <= 0) {
            throw new Exception("Amount should be greater than zero");
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = null;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        Optional<User> user = userRepository.findByUserName(username);
        if (!user.isPresent() || !user.get().getStatus().equals(UserStatus.ACTIVE)) {
            throw new Exception("User is not active");
        }
        Account senderAccount = null;
        for (Account account : user.get().getAccounts()) {
            if(user.get().getRoles() != UserRole.EMPLOYEE){
                if (account.getIBANNo().equals(transaction.getSenderIban())) {
                    if (account.getAccountStatus().equals(AccountStatus.ACTIVE)) {
                        senderAccount = account;
                        break;
                    } else {
                        throw new Exception("Sender account is not active");
                    }
                }
            }
            else
            senderAccount = account;
        }

        if (senderAccount == null) {
            throw new Exception("Sender account not found");
        }

        String receiverIban = transaction.getReceiverIban();
        System.out.println("Receiver IBAN: " + receiverIban);

        Optional<Account> receiverAccount = accountRepository.findByIBANNo(receiverIban);
        System.out.println("Receiver Account: " + receiverAccount.orElse(null));
        if (!receiverAccount.isPresent() || !receiverAccount.get().getAccountStatus().equals(AccountStatus.ACTIVE)) {
            throw new Exception("Receiver account not found or not active");
        }

        // Transfer amount validation
        if (transaction.getTransferAmount() > senderAccount.getTransactionLimit()) {
            throw new Exception("Transfer amount exceeds transaction limit");
        }

        // Balance and limit validations
        long totalTransactionsOfTheDay = transactionRepository.findAllByDateOfTransaction(LocalDate.now()).stream()
                .mapToLong(Transaction::getTransferAmount)
                .sum();
        long newTotalTransactions = totalTransactionsOfTheDay + transaction.getTransferAmount();

        if (senderAccount.getBalance() - transaction.getTransferAmount() > senderAccount.getAbsoluteLimit()) {
            throw new Exception("Transfer amount exceeds absolute limit");
        }

        if (newTotalTransactions > senderAccount.getDayLimit()) {
            throw new Exception("Transfer amount exceeds daily limit");
        }

        // Update sender and receiver account balances
        receiverAccount.get().setBalance(receiverAccount.get().getBalance() + transaction.getTransferAmount());
        senderAccount.setBalance(senderAccount.getBalance() - transaction.getTransferAmount());

        // Save the updated accounts and transaction
        accountRepository.save(receiverAccount.get());
        accountRepository.save(senderAccount);

        Transaction savedTransaction = transactionRepository.save(Transaction.builder()
                .userId(senderAccount.getId())
                .fromIban(senderAccount.getIBANNo())
                .toIban(transaction.getReceiverIban())
                .transferAmount(transaction.getTransferAmount())
                .dateOfTransaction(LocalDate.now())
                .build());

        // Create and return the TransactionResponse object
        CreateTransactionResponseDTO transactionResponse = new CreateTransactionResponseDTO();
        transactionResponse.setId(savedTransaction.getId());
        transactionResponse.setTransferAmount(savedTransaction.getTransferAmount());
        transactionResponse.setUserName(user.get().getUserName());
        transactionResponse.setDateOfTransaction(savedTransaction.getDateOfTransaction());

        return transactionResponse;
    }
    public List<Transaction> findAllTransactions(){
       return transactionRepository.findAll();
    }

    public List<Transaction> findTransactionsByFilters(String fromIBAN, String toIBAN, Long amount, LocalDate to, LocalDate from , Integer offset, Integer limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);

        Root<Transaction> transaction = cq.from(Transaction.class);
        List<Predicate> predicates = new ArrayList<>();

        if (fromIBAN != null) {
            predicates.add(cb.equal(transaction.get("fromIban"), fromIBAN));
        }
        if (toIBAN != null) {
            predicates.add(cb.like(transaction.get("toIban"), toIBAN));
        }

        if (amount != null && amount > 0) {
            predicates.add(cb.equal(transaction.get("transferAmount"), amount));
        }
        if (from != null) {
            predicates.add(cb.equal(transaction.get("dateOfTransaction"), from));
        }
        if (to != null) {
            predicates.add(cb.equal(transaction.get("dateOfTransaction"), to));
        }

        if(offset != null && offset > 0 && limit !=null && limit >0){
            predicates.add(cb.greaterThan(transaction.get("id"),offset));
            predicates.add(cb.lessThan(transaction.get("id"),limit));

        }

        cq.where(predicates.toArray(new Predicate[0]));

        return em.createQuery(cq).getResultList();
    }
    public String depositMoney(DepositToCheckingAccountDTO depositToCheckingAccountDTO) throws Exception {

        Optional<Account> a = accountRepository.findByIBANNo(depositToCheckingAccountDTO.getIBAN());
        if(!a.isPresent()){
            throw  new Exception("account was not found");
        }
        else
        a.ifPresent(account -> account.setBalance(account.getBalance() + depositToCheckingAccountDTO.getDepositAmount()));
        accountRepository.save(a.get());
        return "Amount" +depositToCheckingAccountDTO.getDepositAmount()+"has been deposited to your account";
    }

    public WithdrawMoneyResponseDTO withdrawMoney(WithdrawMoneyDTO withdrawMoneyDTO) throws Exception {

        Optional<Account> a = accountRepository.findByIBANNo(withdrawMoneyDTO.getIBAN());
        WithdrawMoneyResponseDTO transaction = new WithdrawMoneyResponseDTO();
        if(a.isPresent()){
            if(a.get().getBalance() < withdrawMoneyDTO.getAmount()){
                throw new Exception("Insufficient Amount");
            }
            a.ifPresent(account -> account.setBalance(account.getBalance() - withdrawMoneyDTO.getAmount()));
            transaction.setDateOfTransaction(LocalDateTime.now());
            transaction.setTransactionId(UUID.randomUUID());
            transaction.setAmount(withdrawMoneyDTO.getAmount());
            accountRepository.save(a.get());
            return transaction;
        }
        else{
            throw  new Exception("account was not found");
        }
    }
}