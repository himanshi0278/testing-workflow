package com.wolftech.common.dto.operator.airtelmoney;

import com.wolftech.common.dto.BaseDTO;

public class AirtelMoneyTransactionProcessingRequest extends BaseDTO {

    private AirtelMoneyAuthToken airtelMoneyAuthToken;
    private AirtelMoneyTransactionRequest airtelMoneyTransactionRequest;
    
    public AirtelMoneyTransactionProcessingRequest() {
    }

    public AirtelMoneyTransactionProcessingRequest(AirtelMoneyTransactionRequest airtelMoneyTransactionRequest, AirtelMoneyAuthToken airtelMoneyAuthToken) {
        this.airtelMoneyTransactionRequest = airtelMoneyTransactionRequest;
        this.airtelMoneyAuthToken = airtelMoneyAuthToken;
    }

    public AirtelMoneyTransactionRequest getAirtelMoneyTransactionRequest() {
        return airtelMoneyTransactionRequest;
    }

    public void setAirtelMoneyTransactionRequest(AirtelMoneyTransactionRequest airtelMoneyTransactionRequest) {
        this.airtelMoneyTransactionRequest = airtelMoneyTransactionRequest;
    }

    public AirtelMoneyAuthToken getAirtelMoneyAuthToken() {
        return airtelMoneyAuthToken;
    }

    public void setAirtelMoneyAuthToken(AirtelMoneyAuthToken airtelMoneyAuthToken) {
        this.airtelMoneyAuthToken = airtelMoneyAuthToken;
    }

    @Override
    public String toString() {
        return "AirtelMoneyTransactionProcessingRequest{" +
                "airtelMoneyTransactionRequest='" + airtelMoneyTransactionRequest + '\'' +
                ", airtelMoneyAuthToken='" + airtelMoneyAuthToken + '\'' +
                '}';
    }
}
