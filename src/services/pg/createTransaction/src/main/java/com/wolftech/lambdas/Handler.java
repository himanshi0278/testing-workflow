package com.wolftech.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.wolftech.common.utils.TransactionUtil;
import com.wolftech.common.utils.Constants;

import com.wolftech.common.dto.ApiResponse;
import com.wolftech.common.dto.TransactionRequest;

import com.fasterxml.jackson.databind.ObjectMapper; 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;


public class Handler implements RequestHandler<Object, ApiResponse> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final TransactionUtil transactionUtil = new TransactionUtil();
    private static final Logger logger = LogManager.getLogger(Handler.class);
    
    @Override
    public ApiResponse handleRequest(Object input, Context context) {
        logger.info("Inside CreateTransaction function invoked with input: " + input);
        try {
            String traceId = context.getClientContext().getCustom().get("traceId");
            ThreadContext.put("traceId", traceId);
        
            TransactionRequest transactionRequest = objectMapper.convertValue(input, TransactionRequest.class);
            logger.info("Parsed Request: " + transactionRequest);
            addTransaction(transactionRequest, traceId);
            logger.info("Transaction created");
            return new ApiResponse(200, "Transaction created");
        } catch (Exception e) {
            logger.info("Error occurred: " + e.getMessage());
            logger.info("Exception caught. Calling CreateTransaction Lambda...");
            return new ApiResponse(500, "Transaction creation failed. Reason: "+e.getMessage());
        } finally {
            // Clear MDC after processing
            ThreadContext.clearAll();
        }
    }

    void addTransaction(TransactionRequest transactionRequest, String traceId) {
        transactionUtil.putTransaction(transactionRequest, traceId, TransactionUtil.Status.PROCESSING);
    }
}
