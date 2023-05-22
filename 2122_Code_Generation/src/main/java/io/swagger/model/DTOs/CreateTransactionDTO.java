package io.swagger.model.DTOs;

public class CreateTransactionDTO {
    private String receiverIban;
    private  String SenderIban;
    private long transferAmount;

    public String getReceiverIban() {
        return receiverIban;
    }

    public void setReceiverIban(String receiverIban) {
        this.receiverIban = receiverIban;
    }

    public String getSenderIban() {
        return SenderIban;
    }

    public void setSenderIban(String senderIban) {
        SenderIban = senderIban;
    }

    public long getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(long transferAmount) {
        this.transferAmount = transferAmount;
    }
}
