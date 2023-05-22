package io.swagger.model.DTOs;

public class WithdrawMoneyDTO {
    private String  IBAN;
    private long amount;

    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
