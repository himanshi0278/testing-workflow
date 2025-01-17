package com.wolftech.common.dto;

import java.math.BigDecimal;

public class TransactionRequest extends BaseDTO {

    private String operator;
    private String accountNumber;
    private String currency;
    private BigDecimal amount;
    private String thirdPartyId;
    private String reason;
    private String transactionId;

    public TransactionRequest() {
    }

    public TransactionRequest(String operator, String accountNumber, String currency, BigDecimal amount, String thirdPartyId, String reason) {
        this.operator = operator;
        this.accountNumber = accountNumber;
        this.currency = currency;
        this.amount = amount;
        this.thirdPartyId = thirdPartyId;
        this.reason = reason;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "TransactionRequest{" +
                "operator='" + operator + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", currency='" + currency + '\'' +
                ", amount=" + amount +
                ", thirdPartyId='" + thirdPartyId + '\'' +
                ", reason='" + reason + '\'' +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }
}
