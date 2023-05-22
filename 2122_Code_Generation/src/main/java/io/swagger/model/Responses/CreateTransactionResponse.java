package io.swagger.model.Responses;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreateTransactionResponse {

    private UUID id;
    private long transferAmount;

    private String userName;

    private LocalDateTime dateOfTransaction;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public long getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(long transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getDateOfTransaction() {
        return dateOfTransaction;
    }

    public void setDateOfTransaction(LocalDateTime dateOfTransaction) {
        this.dateOfTransaction = dateOfTransaction;
    }
}
