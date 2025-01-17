package com.wolftech.common.dto;

public class SuccessResponseFormatterRequest extends BaseDTO {
    
    private TransactionRequest transactionRequest;
    private OperatorResponse operatorResponse;

    public SuccessResponseFormatterRequest() {
    }

    public SuccessResponseFormatterRequest(TransactionRequest transactionRequest, OperatorResponse operatorResponse) {
        super();
        this.transactionRequest = transactionRequest;
        this.operatorResponse = operatorResponse;
    }

    public TransactionRequest getTransactionRequest() {
        return transactionRequest;
    }

    public void setTransactionRequest(TransactionRequest transactionRequest) {
        this.transactionRequest = transactionRequest;
    }

    public OperatorResponse getOperatorResponse() {
        return operatorResponse;
    }

    public void setOperatorResponse(OperatorResponse operatorResponse) {
        this.operatorResponse = operatorResponse;
    }

    @Override
    public String toString() {
        return "SuccessResponseFormatter{" +
                ", transactionRequest=" + transactionRequest + '\'' +
                ", operatorResponse=" + operatorResponse + '\'' +
                '}';
    }
}
