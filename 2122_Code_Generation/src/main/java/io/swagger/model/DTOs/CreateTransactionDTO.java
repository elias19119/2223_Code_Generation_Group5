package io.swagger.model.DTOs;

import io.swagger.annotations.ApiModelProperty;

public class CreateTransactionDTO {

    @ApiModelProperty(value = "SenderIban", position = 1)
    private  String SenderIban;

    @ApiModelProperty(value = "receiverIban", position = 2)
    private String receiverIban;
    private long transferAmount;
    public String getSenderIban() {
        return SenderIban;
    }

    public void setSenderIban(String senderIban) {
        SenderIban = senderIban;
    }

    public String getReceiverIban() {
        return receiverIban;
    }

    public void setReceiverIban(String receiverIban) {
        this.receiverIban = receiverIban;
    }


    public long getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(long transferAmount) {
        this.transferAmount = transferAmount;
    }
}
