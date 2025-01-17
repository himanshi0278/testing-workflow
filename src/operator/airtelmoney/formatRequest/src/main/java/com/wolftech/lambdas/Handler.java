package com.wolftech.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.wolftech.common.dto.TransactionRequest;
import com.wolftech.common.dto.ApiResponse;
import com.wolftech.common.dto.operator.airtelmoney.AirtelMoneyTransactionRequest;

import com.fasterxml.jackson.databind.ObjectMapper; 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

public class Handler implements RequestHandler<Object, ApiResponse> {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LogManager.getLogger(Handler.class);
    
    @Override
    public ApiResponse handleRequest(Object input, Context context) {
        logger.info("Inside Airtel Money operator FormatRequest function invoked with input: " + input);
        try {
            String traceId = context.getClientContext().getCustom().get("traceId");
            ThreadContext.put("traceId", traceId);
            
            TransactionRequest transactionRequest = objectMapper.convertValue(input, TransactionRequest.class);
            logger.info("Parsed Request: " + transactionRequest);
            //TODO: Convert transactionrequest to AirtelMoneyTransactionRequest
            AirtelMoneyTransactionRequest airtelMoneyTransactionRequest = new AirtelMoneyTransactionRequest();
            ApiResponse requestFormatterResponse = new ApiResponse(200, airtelMoneyTransactionRequest);
            logger.info("Request Formatter Response: " + requestFormatterResponse);
            return requestFormatterResponse;
        } catch (Exception e) {
            logger.info("Error occurred: " + e.getMessage());
            return new ApiResponse(500, e.getMessage());
        } finally {
            // Clear MDC after processing
            ThreadContext.clearAll();
        }
    }
}
