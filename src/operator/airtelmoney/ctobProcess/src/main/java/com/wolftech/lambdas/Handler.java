package com.wolftech.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.wolftech.common.dto.ApiResponse;
import com.wolftech.common.dto.TransactionRequest;
import com.wolftech.common.dto.operator.airtelmoney.AirtelMoneyTransactionRequest;
import com.wolftech.common.dto.operator.airtelmoney.AirtelMoneyAuthToken;
import com.wolftech.common.dto.operator.airtelmoney.AirtelMoneyTransactionProcessingRequest;
import com.wolftech.common.dto.operator.airtelmoney.AirtelMoneyTransactionProcessingResponse;

import com.fasterxml.jackson.databind.ObjectMapper; 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

public class Handler implements RequestHandler<Object, ApiResponse> {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LogManager.getLogger(Handler.class);

    @Override
    public ApiResponse handleRequest(Object input, Context context) {
        logger.info("Inside Airtel Money operator Request processor function invoked with input: " + input);
        try {
            String traceId = context.getClientContext().getCustom().get("traceId");
            ThreadContext.put("traceId", traceId);
            
            AirtelMoneyTransactionProcessingRequest airtelMoneyTransactionProcessingRequest = objectMapper.convertValue(input, AirtelMoneyTransactionProcessingRequest.class);
            logger.info("Airtel Money operator Parequest processor parsed Request: " + airtelMoneyTransactionProcessingRequest);
            //TODO: Make call to Airtel Money Transaction API 
            AirtelMoneyTransactionProcessingResponse airtelMoneyTransactionProcessingResponse = new AirtelMoneyTransactionProcessingResponse();
            ApiResponse getTokenResponse = new ApiResponse(200, airtelMoneyTransactionProcessingResponse);
            logger.info("Airtel Money operator  Request processor Response: " + getTokenResponse);
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
