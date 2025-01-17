package com.wolftech.common.dto;

import com.wolftech.common.utils.TransactionUtil;

public class UpdateTransactionRequest extends BaseDTO {
    
    private String transactionId;
    private String accountNumber;
    private TransactionUtil.Status status;

    public UpdateTransactionRequest() {
    }

    public UpdateTransactionRequest(String transactionId, String accountNumber, TransactionUtil.Status status) {
        super();
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.status = status;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionUtil.Status getStatus() {
        return status;
    }

    public void setStatus(TransactionUtil.Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UpdateTransactionRequest{" +
                ", transactionId='" + transactionId + '\'' +
                ", status=" + status.name() + '\'' +
                '}';
    }
}
