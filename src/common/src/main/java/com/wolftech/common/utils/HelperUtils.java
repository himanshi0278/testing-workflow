package com.wolftech.common.utils;

public class HelperUtils {
    
    public String getLambdaNameWithEnv(String lambda) {
        String environment = getEnvVariable("ENVIRONMENT", "dev");
        if (environment == null || environment.isEmpty()) {
            environment = "dev"; 
        }
        return lambda + '-'+ environment;
    }

    public String getEnvVariable(String variable, String defaultVal) {
        String value = System.getenv(variable);
        if (value == null || value.isEmpty()) {
            value = defaultVal; 
        }
        return value;
    }

    public static final String getOperatorLambdaFunctionName(String operator) {
        switch (operator) {
            case "DRC_AIRTEL_MONEY":
                return Constants.OPERATOR_AIRTEL_MONEY_CTOBREQUEST_LAMBDA;
            case "DRC_ORANGE_MONEY":
                return "OrangeMoneyLambda";
            case "DRC_MPESA_MONEY":
                return "MpesaLambda";
            default:
                throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
    }

}
