package com.wolftech.lambdas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.wolftech.common.dto.ApiResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext; 

public class Handler implements RequestHandler<Object, ApiResponse> {

    private static final Logger logger = LogManager.getLogger(Handler.class);

    @Override
    public ApiResponse handleRequest(Object input, Context context) {        
        logger.info("TransactionRequestValidator function invoked with input: " + input);
        
        try {
            String traceId = context.getClientContext().getCustom().get("traceId");
            ThreadContext.put("traceId", traceId);
            
            List<String> errors = new ArrayList<>();

            // Validate fields
            validateInput(input, errors);

            // If there are validation errors, return 400 response
            if (!errors.isEmpty()) {
                logger.info("Validation failed: " + errors);
                return new ApiResponse(400, String.join(", ", errors));
            }

            // Business logic if validation passes
            logger.info("Processing request...");
            String responseMessage = "Transaction processed successfully!";
            logger.info("Response: " + responseMessage);

            return new ApiResponse(200, responseMessage);
        } catch (Exception e) {
            logger.info("Error occurred: " + e.getMessage());
            return new ApiResponse(500, e.getMessage());
        } finally {
            // Clear MDC after processing
            ThreadContext.clearAll();
        }
    }

    private void validateInput(Object request, List<String> errors) {
        if(request == null || !(request instanceof Map<?, ?>)){
            errors.add("Invalid request. Request is not a valid JSON request");
            return;
        }
        Map<String, Object> input = (Map<String, Object>) request;
        // Required field checks
        if (!input.containsKey("operator") || input.get("operator") == null || input.get("operator").toString().isEmpty()) {
            errors.add("'operator' is required and cannot be empty.");
        }

        if (!input.containsKey("accountNumber") || input.get("accountNumber") == null || input.get("accountNumber").toString().isEmpty()) {
            errors.add("'accountNumber' is required and cannot be empty.");
        }

        if (!input.containsKey("currency") || input.get("currency") == null || input.get("currency").toString().isEmpty()) {
            errors.add("'currency' is required and cannot be empty.");
        }

        if (!input.containsKey("amount")) {
            errors.add("'amount' is required.");
        } else {
            try {
                double amount = Double.parseDouble(input.get("amount").toString());
                if (amount <= 0) {
                    errors.add("'amount' must be greater than zero.");
                }
            } catch (NumberFormatException e) {
                errors.add("'amount' must be a valid number.");
            }
        }

        if (!input.containsKey("thirdPartyId") || input.get("thirdPartyId") == null || input.get("thirdPartyId").toString().isEmpty()) {
            errors.add("'thirdPartyId' is required and cannot be empty.");
        }

        if (!input.containsKey("reason") || input.get("reason") == null || input.get("reason").toString().isEmpty()) {
            errors.add("'reason' is required and cannot be empty.");
        }
    }
}
