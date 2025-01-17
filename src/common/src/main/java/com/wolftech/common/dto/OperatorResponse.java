package com.wolftech.common.dto;

public class OperatorResponse extends BaseDTO {

    private String redirectUrl;
    private String qr;
    private String message;

    public OperatorResponse() {
    }

    public OperatorResponse(String message, String qr, String redirectUrl) {
        super();
        this.redirectUrl = redirectUrl;
        this.qr = qr;
        this.message = message;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getQr() {
        return qr;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "OperatorResponse{" +
                ", redirectUrl=" + redirectUrl + '\'' +
                ", qr=" + qr + '\'' +
                ", message=" + message + '\'' +
                '}';
    }
    
}
