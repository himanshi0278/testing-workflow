package com.wolftech.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


import com.wolftech.common.utils.LambdaClientUtil;
import com.wolftech.common.utils.TransactionUtil;
import com.wolftech.common.utils.HelperUtils;
import com.wolftech.common.utils.Constants;

import com.wolftech.common.dto.ApiResponse;
import com.wolftech.common.dto.TransactionRequest;
import com.wolftech.common.dto.OperatorResponse;
import com.wolftech.common.dto.FailureFormatterRequest;
import com.wolftech.common.dto.SuccessResponseFormatterRequest;
import com.wolftech.common.dto.UpdateTransactionRequest;

import java.util.UUID;

import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper; 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

public class Handler implements RequestHandler<Object, ApiResponse> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final HelperUtils helperUtils = new HelperUtils();
    private static final Logger logger = LogManager.getLogger(Handler.class);
    
    @Override
    public ApiResponse handleRequest(Object input, Context context) {
        String traceId = UUID.randomUUID().toString();
        ThreadContext.put("traceId", traceId);

        String transactionId = UUID.randomUUID().toString();
        String accountNumber = null;
        TransactionUtil.Status transactionStatus = TransactionUtil.Status.VALIDATING;
        
        logger.info("Inside FrontCtobRequest function invoked with input: " + input);

        try {
            logger.info("Processing request...");

            // Invoke transactionRequestValidator Lambda
            ApiResponse validatorResponse = LambdaClientUtil.invokeLambda(input, helperUtils.getLambdaNameWithEnv(Constants.TRANSACTION_VALIDATOR_LAMBDA), traceId);
            logger.info("Validator Response: " + validatorResponse);

            if (validatorResponse.getStatusCode() == 200) {
                // Step 3: If successful, update Transaction table and call Router Lambda
                logger.info("Validation successful. Calling Router Lambda...");
                transactionStatus = TransactionUtil.Status.PROCESSING;
                TransactionRequest transactionRequest = objectMapper.convertValue(input, TransactionRequest.class);
                transactionRequest.setTransactionId(transactionId);
                accountNumber = transactionRequest.getAccountNumber();
                createTransaction(transactionRequest, traceId);
                ApiResponse routerResponse = LambdaClientUtil.invokeLambda(transactionRequest, helperUtils.getLambdaNameWithEnv(Constants.OPERATOR_ROUTER_LAMBDA), traceId);
                logger.info("Router Response: " + routerResponse);

                // Check if Router Lambda response indicates failure
                if (routerResponse.getStatusCode() != 200) {
                    logger.info("Router Lambda failed. Calling ctobFailedResponseFormatter Lambda...");
                    transactionStatus = TransactionUtil.Status.FAILED;
                    updateTransaction(transactionId, accountNumber, transactionStatus, traceId, true);
                    FailureFormatterRequest failureFormatterRequest = new FailureFormatterRequest(routerResponse.getBody().toString(), routerResponse.getStatusCode(), input);
                    ApiResponse failedResponse = LambdaClientUtil.invokeLambda(failureFormatterRequest, helperUtils.getLambdaNameWithEnv(Constants.FAILURE_RESPONSE_FORMATTER_LAMBDA), traceId);
                    logger.info("Failed Formatter Response: " + failedResponse);
                    return failedResponse;
                }

                // If Router Lambda response is successful, call CtobSuccessResponseFormatter Lambda
                logger.info("Router Lambda successful. Calling CtobSuccessResponseFormatter Lambda...");
                transactionStatus = TransactionUtil.Status.SUCCESS;
                updateTransaction(transactionId, accountNumber, transactionStatus, traceId, true);
                SuccessResponseFormatterRequest successResponseFormatterRequest = new SuccessResponseFormatterRequest();
                successResponseFormatterRequest.setTransactionRequest(transactionRequest);
                successResponseFormatterRequest.setOperatorResponse((OperatorResponse)routerResponse.getBody());
                ApiResponse successResponse = LambdaClientUtil.invokeLambda(successResponseFormatterRequest, helperUtils.getLambdaNameWithEnv(Constants.SUCCESS_RESPONSE_FORMATTER_LAMBDA), traceId);
                logger.info("Success Formatter Response: " + successResponse);
                return successResponse;
            } else {
                // Step 4: If validation failed, call ctobFailedResponseFormatter Lambda
                logger.info("Validation failed. Calling ctobFailedResponseFormatter Lambda...");
                FailureFormatterRequest failureFormatterRequest = new FailureFormatterRequest(validatorResponse.getBody().toString(), validatorResponse.getStatusCode(), input);
                ApiResponse failedResponse = LambdaClientUtil.invokeLambda(failureFormatterRequest, helperUtils.getLambdaNameWithEnv(Constants.FAILURE_RESPONSE_FORMATTER_LAMBDA), traceId);
                logger.info("Failed Formatter Response: " + failedResponse);
                return failedResponse;
            }
        } catch (Exception e) {
            logger.info("Error occurred: " + e.getMessage());
            logger.info("Exception caught. Calling ctobFailedResponseFormatter Lambda...");
            return handleError(transactionId, accountNumber, transactionStatus, input, traceId, e);
            
        } finally {
            // Clear MDC after processing
            ThreadContext.clearAll();
        }
    }

    private ApiResponse updateTransaction(String transactionId, String accountNumber, TransactionUtil.Status status, String traceId, boolean throwError) throws Exception {
        UpdateTransactionRequest updateTransactionRequest = new UpdateTransactionRequest(transactionId, accountNumber, status);
        ApiResponse updateTransactionResponse = LambdaClientUtil.invokeLambda(updateTransactionRequest, helperUtils.getLambdaNameWithEnv(Constants.UPDATE_TRANSACTION_LAMBDA), traceId);
        if(updateTransactionResponse.getStatusCode() != 200 && throwError) {
            throw new Exception(updateTransactionResponse.getBody().toString());
        }
        return updateTransactionResponse;
    }

    private void createTransaction(TransactionRequest transactionRequest, String traceId) throws Exception {
        ApiResponse createTransactionResponse = LambdaClientUtil.invokeLambda(transactionRequest, helperUtils.getLambdaNameWithEnv(Constants.CREATE_TRANSACTION_LAMBDA), traceId);
        if(createTransactionResponse.getStatusCode() != 200) {
            throw new Exception(createTransactionResponse.getBody().toString()
            );
        }
    }

    private ApiResponse handleError(String transactionId, String accountNumber, TransactionUtil.Status transactionStatus, Object input, String traceId, Exception exception) {
        try {
            if(!transactionStatus.equals(TransactionUtil.Status.VALIDATING)) {
                updateTransaction(transactionId, accountNumber, transactionStatus, traceId, false);
            }
            FailureFormatterRequest failureFormatterRequest = new FailureFormatterRequest(exception.getMessage(), 500, input);
            ApiResponse failedResponse = LambdaClientUtil.invokeLambda(failureFormatterRequest, helperUtils.getLambdaNameWithEnv(Constants.FAILURE_RESPONSE_FORMATTER_LAMBDA), traceId);
            logger.info("Failed Formatter Response: " + failedResponse);
            return failedResponse;       
        } catch (Exception e) {
            return new ApiResponse(500, e.getMessage());
        }
    }
}
