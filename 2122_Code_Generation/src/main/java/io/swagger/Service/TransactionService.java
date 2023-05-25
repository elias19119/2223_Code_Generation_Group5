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
import io.swagger.model.Enums.UserStatus;
import io.swagger.model.Responses.CreateTransactionResponse;
import io.swagger.model.Responses.WithdrawMoneyResponse;
import io.swagger.model.Transaction;
import io.swagger.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final JwtTokenProvider jwtUtil;


    public CreateTransactionResponse makeTransaction(CreateTransactionDTO transaction, String token) throws Exception {
        if (transaction.getTransferAmount() <= 0) {
            throw new Exception("Amount should be greater than zero");
        }

        Optional<User> user = Optional.ofNullable(jwtUtil.getUserFromToken(token));
        if (!user.isPresent() || !user.get().getStatus().equals(UserStatus.ACTIVE)) {
            throw new Exception("User is not active");
        }

        Account senderAccount = null;
        for (Account account : user.get().getAccounts()) {
            if (account.getIBANNo().equals(transaction.getSenderIban())) {
                if (account.getAccountStatus().equals(AccountStatus.ACTIVE)) {
                    senderAccount = account;
                    break;
                } else {
                    throw new Exception("Sender account is not active");
                }
            }
        }

        if (senderAccount == null) {
            throw new Exception("Sender account not found");
        }

        Optional<Account> receiverAccount = accountRepository.findByIBANNo(transaction.getReceiverIban());
        if (!receiverAccount.isPresent() || !receiverAccount.get().getAccountStatus().equals(AccountStatus.ACTIVE)) {
            throw new Exception("Receiver account not found or not active");
        }

        // Transfer amount validation
        if (transaction.getTransferAmount() > senderAccount.getTransactionLimit()) {
            throw new Exception("Transfer amount exceeds transaction limit");
        }

        // Balance and limit validations
        long totalTransactionsOfTheDay = transactionRepository.findAllByDateOfTransaction(LocalDateTime.now()).stream()
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
        CreateTransactionResponse transactionResponse = new CreateTransactionResponse();
        transactionResponse.setId(savedTransaction.getId());
        transactionResponse.setTransferAmount(savedTransaction.getTransferAmount());
        transactionResponse.setUserName(user.get().getUserName());
        transactionResponse.setDateOfTransaction(savedTransaction.getDateOfTransaction());

        return transactionResponse;
    }

    public List<Transaction> getAllTransaction() {

        return transactionRepository.findAll();
    }

    public List<Transaction> findTransactionByIban(String IBAN) throws Exception{
        List<Transaction> transactions = transactionRepository.findAllByFromIban(IBAN);
        if(transactions.isEmpty()){
            throw new Exception("no transactions were found");
        }
        return transactions;
    }
    public List<Transaction> findByIbanFromAndToDate(String IBAN, LocalDate to, LocalDate from) throws Exception{
        List<Transaction> transactions = transactionRepository.findByFromIbanAndDateOfTransactionBetween(IBAN, to, from);
        if(transactions.isEmpty()){
            throw new Exception("no transactions were between these dates");
        }
        return transactions;
    }

    public List<Transaction> findFromIbanToIban(String senderIBAN, String receiverIBAN) throws Exception {
        List<Transaction> transactions =  transactionRepository.findAllByFromIbanAndToIban(senderIBAN,receiverIBAN);
        if(transactions.isEmpty()){
            throw new Exception("no transactions were found between IBANS");
        }
        return transactions;
    }

    public List<Transaction> findTransferAmountFromIBAN(String IBAN, long amount) throws Exception {
        List<Transaction> transactions = transactionRepository.findAll();
        List<Transaction> filteredTransactions = new ArrayList<>();
        Set<UUID> uniqueTransactionIds = new HashSet<>();

        for (Transaction transaction : transactions) {
            if (transaction.getFromIban().equals(IBAN)
                    && transaction.getTransferAmount() == amount) {
                if (!uniqueTransactionIds.contains(transaction.getId())) {
                    filteredTransactions.add(transaction);
                    uniqueTransactionIds.add(transaction.getId());
                }
            }
        }

        if (filteredTransactions.isEmpty()) {
            throw new Exception("No transactions were found with the amount " + amount +
                    " from " + IBAN);
        }

        return filteredTransactions;
    }

    public List<Transaction> findTransferAmountFromIBANAndToIBAN(String fromIBAN, String toIBAN, long amount) throws Exception {
        List<Transaction> transactions = transactionRepository.findAll();
        List<Transaction> filteredTransactions = new ArrayList<>();

        for (Transaction transaction : transactions) {
            if (transaction.getFromIban().equals(fromIBAN)
                    && transaction.getToIban().equals(toIBAN)
                    && transaction.getTransferAmount() == amount) {
                    filteredTransactions.add(transaction);
            }
        }

        if (filteredTransactions.isEmpty()) {
            throw new Exception("No transactions were found with the amount " + amount +
                    " from " + fromIBAN + " to " + toIBAN);
        }

        return filteredTransactions;
    }

    public String depositMoney(DepositToCheckingAccountDTO depositToCheckingAccountDTO) throws Exception {

        Optional<Account> a = accountRepository.findByIBANNo(depositToCheckingAccountDTO.getIBAN());
        if(a.isPresent()){
            a.ifPresent(account -> account.setBalance(account.getBalance() + depositToCheckingAccountDTO.getDepositAmount()));
            accountRepository.save(a.get());
            return "Amount" +depositToCheckingAccountDTO.getDepositAmount()+"has been deposited to your account";
        }
        else{
            throw  new Exception("account was not found");
        }
    }

    public WithdrawMoneyResponse withdrawMoney(WithdrawMoneyDTO withdrawMoneyDTO) throws Exception {

        Optional<Account> a = accountRepository.findByIBANNo(withdrawMoneyDTO.getIBAN());
        WithdrawMoneyResponse transaction = new WithdrawMoneyResponse();
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

    public List<Transaction> getTransactionByLimitAndOffset(int offset, int limit) throws Exception {
        List<Transaction> allTransactions = transactionRepository.findAll(); // Fetch all users from the data source
        List<Transaction> transactions = new ArrayList<>();

        int endIndex = Math.min(offset + limit, allTransactions.size());

        if (offset < endIndex) {
            transactions = allTransactions.subList(offset, endIndex);
        }

        return transactions;
    }
}