package com.wolftech.common.dto;

public class FailureFormatterRequest extends BaseDTO {
    
    private String failureMessage;
    private int errorCode;
    private Object input;

    public FailureFormatterRequest() {
    }

    public FailureFormatterRequest(String failureMessage, int errorCode, Object input) {
        this.failureMessage = failureMessage;
        this.input = input;
        this.errorCode = errorCode;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public Object getInput() {
        return input;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "FailureFormatterRequest{" +
                ", failureMessage='" + failureMessage + '\'' +
                ", errorCode='" + errorCode + '\'' +                
                ", input='" + input + '\'' +
                '}';
    }
}
