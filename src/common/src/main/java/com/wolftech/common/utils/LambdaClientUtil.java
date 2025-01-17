package com.wolftech.common.utils;

import com.wolftech.common.dto.ApiResponse;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LambdaClientUtil {

    private static final Logger logger = LogManager.getLogger(LambdaClientUtil.class);

    /**
     * Invokes another Lambda function
     */
    public static ApiResponse invokeLambda(Object input, String lambdaName, String traceId) {
        logger.info("Invoking " + lambdaName + " with input: " + input);
        ApiResponse apiResponse = new ApiResponse(); 
        // Create Lambda Client
        try (LambdaClient lambdaClient = LambdaClient.builder().build()) {

            // Prepare payload
            SdkBytes payload = SdkBytes.fromString(input.toString(), StandardCharsets.UTF_8);

            String clientContextJson = "{\"custom\":{\"traceId\":\"" + traceId + "\"}}";
            String encodedClientContext = java.util.Base64.getEncoder().encodeToString(clientContextJson.getBytes(StandardCharsets.UTF_8));

            // Build invoke request
            InvokeRequest invokeRequest = InvokeRequest.builder()
                    .functionName(lambdaName)
                    .payload(payload)
                    .invocationType(InvocationType.REQUEST_RESPONSE)
                    .clientContext(encodedClientContext)
                    .build();

            // Invoke Lambda function
            InvokeResponse invokeResponse = lambdaClient.invoke(invokeRequest);

            // Read response payload
            String responsePayload = invokeResponse.payload().asUtf8String();
            logger.info(lambdaName + " response: " + responsePayload);
            apiResponse.setBody(responsePayload);
            apiResponse.setStatusCode(invokeResponse.statusCode());    
        } catch (Exception e) {
            logger.info("Failed to invoke transactionRequestValidator: " + e.getMessage());
            apiResponse.setBody(e.getMessage());
            apiResponse.setStatusCode(500);    
        }
        return apiResponse;
    }
}
