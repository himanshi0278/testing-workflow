package com.wolftech.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.wolftech.common.utils.TransactionUtil;
import com.wolftech.common.utils.HelperUtils;
import com.wolftech.common.utils.Constants;

import com.wolftech.common.dto.ApiResponse;
import com.wolftech.common.dto.TransactionRequest;
import com.wolftech.common.dto.UpdateTransactionRequest;

import com.fasterxml.jackson.databind.ObjectMapper; 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

public class Handler implements RequestHandler<Object, ApiResponse> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final TransactionUtil transactionUtil = new TransactionUtil();
    private final HelperUtils helperUtils = new HelperUtils();
    private static final Logger logger = LogManager.getLogger(Handler.class);
    
    @Override
    public ApiResponse handleRequest(Object input, Context context) {
        logger.info("Inside UpdateTransaction function invoked with input: " + input);
        try {
            String traceId = context.getClientContext().getCustom().get("traceId");
            ThreadContext.put("traceId", traceId);
            UpdateTransactionRequest updateTransactionRequest = objectMapper.convertValue(input, UpdateTransactionRequest.class);
            logger.info("Update transaction request: " + updateTransactionRequest);
            updateTransaction(updateTransactionRequest.getTransactionId(), updateTransactionRequest.getAccountNumber(), updateTransactionRequest.getStatus());
            logger.info("Transaction updated");
            return new ApiResponse(200, "Transaction updated");
        } catch (Exception e) {
            logger.info("Error occurred: " + e.getMessage());
            logger.info("Exception caught. Calling UpdatedTransaction Lambda...");
            return new ApiResponse(500, "Transaction updation failed. Reason: "+e.getMessage());
        } finally {
            // Clear MDC after processing
            ThreadContext.clearAll();
        }
    }

    private void updateTransaction(String transactionId, String accountNumber, TransactionUtil.Status status) {
        transactionUtil.updateTransactionStatus(transactionId, accountNumber, status);
    }
}
