package com.wolftech.common.dto.operator.airtelmoney;

import com.wolftech.common.dto.BaseDTO;

public class AirtelMoneyAuthToken extends BaseDTO {
    
    private String authToken;

    public AirtelMoneyAuthToken() {
    }

    public AirtelMoneyAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public String toString() {
        return "AirtelMoneyAuthToken{" +
                "authToken='" + authToken + '\'' +
                '}';
    }
}
