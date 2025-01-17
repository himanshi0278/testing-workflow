package com.wolftech.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.wolftech.common.dto.TransactionRequest;
import com.wolftech.common.dto.OperatorResponse;
import com.wolftech.common.utils.LambdaClientUtil;
import com.wolftech.common.utils.HelperUtils;
import com.wolftech.common.dto.ApiResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext; 

public class Handler implements RequestHandler<Object, ApiResponse> {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LogManager.getLogger(Handler.class);

    @Override
    public ApiResponse handleRequest(Object input, Context context) {
        String traceId = context.getClientContext().getCustom().get("traceId");
        ThreadContext.put("traceId", traceId);

        try {
            // Convert input to TransactionRequest
            TransactionRequest transactionRequest = objectMapper.convertValue(input, TransactionRequest.class);
            logger.info("Inside OperatorRouter function invoked with input: " + input);
            logger.info("Parsed Request: " + transactionRequest);

            // Determine the operator and route accordingly
            String operator = transactionRequest.getOperator();
            logger.info("Operator: " + operator);

            String lambdaFunctionName = HelperUtils.getOperatorLambdaFunctionName(operator);
            logger.info("Routing to Lambda Function: " + lambdaFunctionName);
            ApiResponse operatorResponse = LambdaClientUtil.invokeLambda(transactionRequest, lambdaFunctionName, traceId);
            logger.info("Operator Response: " + operatorResponse);

            return operatorResponse;
        } catch (Exception e) {
            logger.info("Error occurred: " + e.getMessage());
            return new ApiResponse(500, e.getMessage());
        } finally {
            // Clear MDC after processing
            ThreadContext.clearAll();
        }
    }

}
