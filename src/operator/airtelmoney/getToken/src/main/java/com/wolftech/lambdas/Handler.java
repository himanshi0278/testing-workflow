package com.wolftech.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.wolftech.common.dto.TransactionRequest;
import com.wolftech.common.dto.ApiResponse;
import com.wolftech.common.dto.operator.airtelmoney.AirtelMoneyTransactionRequest;
import com.wolftech.common.dto.operator.airtelmoney.AirtelMoneyAuthToken;

import com.fasterxml.jackson.databind.ObjectMapper; 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

public class Handler implements RequestHandler<Object, ApiResponse> {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LogManager.getLogger(Handler.class);
    
    @Override
    public ApiResponse handleRequest(Object input, Context context) {
        logger.info("Inside Airtel Money operator Get Token function invoked with input: " + input);
        try {
            String traceId = context.getClientContext().getCustom().get("traceId");
            ThreadContext.put("traceId", traceId);
            
            AirtelMoneyTransactionRequest airtelMoneyTransactionRequest = objectMapper.convertValue(input, AirtelMoneyTransactionRequest.class);
            logger.info("Airtel Money operator Parsed Request: " + airtelMoneyTransactionRequest);
            //TODO: Make call to Airtel Money getToken API to get token
            AirtelMoneyAuthToken airtelMoneyAuthToken = new AirtelMoneyAuthToken("AUTH-TOKEN");
            ApiResponse getTokenResponse = new ApiResponse(200, airtelMoneyAuthToken);
            logger.info("Airtel Money operator Get Token Response: " + getTokenResponse);
            return getTokenResponse;
        } catch (Exception e) {
            logger.info("Error occurred: " + e.getMessage());
            return new ApiResponse(500, e.getMessage());
        } finally {
            // Clear MDC after processing
            ThreadContext.clearAll();
        }
    }
}
