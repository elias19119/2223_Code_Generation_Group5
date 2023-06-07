package io.swagger.Repository;


import io.swagger.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,UUID> {

    List<Transaction> findAllByFromIbanAndToIban(String fromIBAN, String toIBAN);

    List<Transaction> findByFromIbanAndDateOfTransactionBetween(String Iban, LocalDate to, LocalDate from);

    List<Transaction> findAllByFromIban(String IBAN);

    List<Transaction> findAllByDateOfTransaction(LocalDate dateOfTransactions);

}