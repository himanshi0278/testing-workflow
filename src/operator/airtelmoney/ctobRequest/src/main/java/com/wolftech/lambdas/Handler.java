package com.wolftech.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.wolftech.common.dto.TransactionRequest;
import com.wolftech.common.dto.OperatorResponse;
import com.wolftech.common.utils.LambdaClientUtil;
import com.wolftech.common.utils.Constants;

import com.wolftech.common.dto.ApiResponse;
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

        logger.info("Inside Airtel Money CtobRequestHandler function invoked with input: " + input);
        try {
            String traceId = context.getClientContext().getCustom().get("traceId");
            ThreadContext.put("traceId", traceId);
            
            TransactionRequest transactionRequest = objectMapper.convertValue(input, TransactionRequest.class);
            logger.info("Parsed Request: " + transactionRequest);
            
            logger.info("Routing to Request formatter Lambda Function: " + Constants.OPERATOR_AIRTEL_MONEY_FORMATREQUEST_LAMBDA);
            ApiResponse requestFormatterResponse = LambdaClientUtil.invokeLambda(transactionRequest, Constants.OPERATOR_AIRTEL_MONEY_FORMATREQUEST_LAMBDA, traceId);
            logger.info("Request formatter lambda Response: " + requestFormatterResponse);
            if(requestFormatterResponse.getStatusCode() != 200) {
                return requestFormatterResponse;
            }

            logger.info("Routing to get Token Lambda Function: " + Constants.OPERATOR_AIRTEL_MONEY_GETTOKEN_LAMBDA);
            AirtelMoneyTransactionRequest airtelMoneyTransactionRequest = objectMapper.convertValue(requestFormatterResponse.getBody(), AirtelMoneyTransactionRequest.class);
            logger.info("Airtel Money formatted request: " + airtelMoneyTransactionRequest);
            ApiResponse getTokenResponse = LambdaClientUtil.invokeLambda(airtelMoneyTransactionRequest, Constants.OPERATOR_AIRTEL_MONEY_GETTOKEN_LAMBDA, traceId);
            logger.info("Airtel Money Get Token lambda Response: " + getTokenResponse);
            if(getTokenResponse.getStatusCode() != 200) {
                return getTokenResponse;
            }
            AirtelMoneyAuthToken airtelMoneyToken = objectMapper.convertValue(getTokenResponse.getBody(), AirtelMoneyAuthToken.class);
            logger.info("Airtel Money Token response: " + airtelMoneyToken);
            
            logger.info("Routing to Request processor Lambda Function: " + Constants.OPERATOR_AIRTEL_MONEY_CTOBPROCESS_LAMBDA);
            AirtelMoneyTransactionProcessingRequest airtelMoneyTransactionProcessingRequest = new AirtelMoneyTransactionProcessingRequest(airtelMoneyTransactionRequest, airtelMoneyToken);
            logger.info("Airtel Money Request processor request: " + airtelMoneyTransactionProcessingRequest);
            ApiResponse requestProcessorResponse = LambdaClientUtil.invokeLambda(airtelMoneyTransactionProcessingRequest, Constants.OPERATOR_AIRTEL_MONEY_CTOBPROCESS_LAMBDA, traceId);
            logger.info("Airtel Money request processor Lambda Response: " + requestProcessorResponse);
            if(getTokenResponse.getStatusCode() != 200) {
                return getTokenResponse;
            }
            AirtelMoneyTransactionProcessingResponse airtelMoneyTransactionProcessingResponse = objectMapper.convertValue(requestProcessorResponse.getBody(), AirtelMoneyTransactionProcessingResponse.class);
            OperatorResponse operatorResponse = new OperatorResponse();
            // TODO: Copy airtelMoneyTransactionProcessingResponse data into generic operator response object
            return new ApiResponse(500, operatorResponse);
        } catch (Exception e) {
            logger.info("Error occurred: " + e.getMessage());
            return new ApiResponse(500, e.getMessage());
        } finally {
            // Clear MDC after processing
            ThreadContext.clearAll();
        }
    }
}
