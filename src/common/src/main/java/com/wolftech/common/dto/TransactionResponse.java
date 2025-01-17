package com.wolftech.common.dto;

public class TransactionResponse {

    private String message;
    private String transactionId;
    private String thirdPartyId;
    private String qr;
    private String redirectUrl;

    public TransactionResponse(String message) {
        this.message = message;
    }

    // Constructor
    public TransactionResponse(String message, String transactionId, String thirdPartyId, String qr, String redirectUrl) {
        this.message = message;
        this.transactionId = transactionId;
        this.thirdPartyId = thirdPartyId;
        this.qr = qr;
        this.redirectUrl = redirectUrl;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    @Override
    public String toString() {
        return "TransactionResponse{" +
                "message='" + message + '\'' +
                ", transactionId=" + transactionId +
                ", thirdPartyId='" + thirdPartyId + '\'' +
                ", qr='" + qr + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                '}';
    }
}
