package com.wolftech.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.wolftech.common.dto.ApiResponse;
import com.wolftech.common.dto.TransactionRequest;
import com.wolftech.common.dto.SuccessResponseFormatterRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper; 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext; 


public class Handler implements RequestHandler<Map<String, Object>, ApiResponse> {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LogManager.getLogger(Handler.class);

    @Override
    public ApiResponse handleRequest(Map<String, Object> input, Context context) {
        logger.info("Inside CtobSuccessResponseFormatter function invoked with input: " + input);

        try {
            String traceId = context.getClientContext().getCustom().get("traceId");
            ThreadContext.put("traceId", traceId);
           
            SuccessResponseFormatterRequest request = objectMapper.convertValue(input, SuccessResponseFormatterRequest.class);
            logger.info("SuccessResponseFormatter Request: " + request);

            if(request.getTransactionRequest().getTransactionId() == null) {
                throw new Exception("Missing transaction id");
            }
            // Prepare response
            Map<String, Object> responseBody = new HashMap<>();
            String message = request.getOperatorResponse().getMessage();
            responseBody.put("message", message != null? message: "Success");
            responseBody.put("transactionId", request.getTransactionRequest().getTransactionId());
            responseBody.put("thirdPartyId", request.getTransactionRequest().getThirdPartyId());
            responseBody.put("qr", request.getOperatorResponse().getQr());
            responseBody.put("redirectUrl", request.getOperatorResponse().getRedirectUrl());

            logger.info("Formatted Success Response: " + responseBody);

            return new ApiResponse(200, responseBody);
        } catch (Exception e) {
            logger.info("Error occurred: " + e.getMessage());
            return new ApiResponse(500, "Transaction failed. Reason: " + e.getMessage());
        } finally {
            // Clear MDC after processing
            ThreadContext.clearAll();
        }

    }
}
