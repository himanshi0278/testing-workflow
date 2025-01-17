package com.wolftech.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.wolftech.common.dto.ApiResponse;
import com.wolftech.common.dto.TransactionRequest;
import com.wolftech.common.dto.FailureFormatterRequest;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext; 
import com.fasterxml.jackson.databind.ObjectMapper;

public class Handler implements RequestHandler<Map<String, Object>, ApiResponse> {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LogManager.getLogger(Handler.class);

    @Override
    public ApiResponse handleRequest(Map<String, Object> input, Context context) {
        logger.info("Inside CtobFailedResponseFormatter function invoked with input: " + input);

        try {
            String traceId = context.getClientContext().getCustom().get("traceId");
            ThreadContext.put("traceId", traceId);

            FailureFormatterRequest failureFormatterRequest = objectMapper.convertValue(input, FailureFormatterRequest.class);
            logger.info("Processing request...");
            
            logger.info("Parsed Request: " + failureFormatterRequest);
                
            // Create error message
            String message = failureFormatterRequest.getFailureMessage();
            logger.info("Formatted Error Message: " + message);

            // Return formatted response
            return new ApiResponse(failureFormatterRequest.getErrorCode(), message);
        } catch (Exception e) {
            logger.info("Error occurred: " + e.getMessage());
            return new ApiResponse(500, "Transaction failed. Reason: " + e.getMessage());
        } finally {
            // Clear MDC after processing
            ThreadContext.clearAll();
        }
    }
}
