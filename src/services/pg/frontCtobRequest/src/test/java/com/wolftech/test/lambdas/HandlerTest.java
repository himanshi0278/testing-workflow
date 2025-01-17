package com.wolftech.test.lambdas;

import com.wolftech.lambdas.Handler;
import com.wolftech.common.utils.LambdaClientUtil;
import com.wolftech.common.utils.TransactionUtil;
import com.wolftech.common.dto.ApiResponse;
import com.wolftech.common.dto.TransactionRequest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.lambda.runtime.Context;
import org.apache.logging.log4j.ThreadContext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RunWith(PowerMockRunner.class)
class HandlerTest {

    private Context context;
    private Handler handler;

    @Before
    void setUp() {
        handler = spy(new Handler());
        context = mock(Context.class);
        PowerMockito.mockStatic(LambdaClientUtil.class);
    }

    @Test
    void testValidRequest() throws Exception {
        // Arrange
        String traceId = UUID.randomUUID().toString();
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setOperator("DRC_AIRTEL_MONEY");
        transactionRequest.setAccountNumber("243998568318");
        transactionRequest.setCurrency("USD/CDF");
        transactionRequest.setAmount(new BigDecimal(1.00));
        transactionRequest.setThirdPartyId("241128.004213.A001");
        transactionRequest.setReason("Collect payment");

        ApiResponse validatorResponse = new ApiResponse(200, "Success");
        ApiResponse routerResponse = new ApiResponse(200, "Router Success");
        ApiResponse successFormatterResponse = new ApiResponse(200, "Success Formatter");

        when(LambdaClientUtil.invokeLambda(any(), any(), eq(traceId))).thenReturn(validatorResponse)
            .thenReturn(routerResponse)
            .thenReturn(successFormatterResponse);

        // Act
        ApiResponse result = handler.handleRequest(transactionRequest, context);

        // Assert
        assertEquals(200, result.getStatusCode());
        assertEquals("Success Formatter", result.getBody());
    }


    @Test
    void testValidationFailure() throws Exception {
        // Arrange
        String traceId = UUID.randomUUID().toString();
        TransactionRequest transactionRequest = new TransactionRequest();
        // Missing or invalid parameters
        transactionRequest.setAccountNumber("243998568318");
        transactionRequest.setAmount(new BigDecimal(-1.00)); // Invalid amount (negative)

        ApiResponse validatorResponse = new ApiResponse(400, "Validation Failed");

        when(LambdaClientUtil.invokeLambda(any(), any(), eq(traceId))).thenReturn(validatorResponse);

        // Act
        ApiResponse result = handler.handleRequest(transactionRequest, context);

        // Assert
        assertEquals(400, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("Validation Failed"));
    }


    @Test
    void testRouterFailure() throws Exception {
        // Arrange
        String traceId = UUID.randomUUID().toString();
        String transactionId = UUID.randomUUID().toString();
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setOperator("DRC_AIRTEL_MONEY");
        transactionRequest.setAccountNumber("243998568318");
        transactionRequest.setCurrency("USD/CDF");
        transactionRequest.setAmount(new BigDecimal(1.00));
        transactionRequest.setThirdPartyId("241128.004213.A001");
        transactionRequest.setReason("Collect payment");

        ApiResponse validatorResponse = new ApiResponse(200, "Validation Success");
        ApiResponse routerResponse = new ApiResponse(500, "Router Failure");
        ApiResponse failureFormatterResponse = new ApiResponse(500, "Failure Formatter");

        when(LambdaClientUtil.invokeLambda(any(), any(), eq(traceId)))
            .thenReturn(validatorResponse)
            .thenReturn(routerResponse)
            .thenReturn(failureFormatterResponse);

        // Act
        ApiResponse result = handler.handleRequest(transactionRequest, context);

        // Assert
        assertEquals(500, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("Failure Formatter"));
    }


    @Test
    void testTransactionCreationFailure() throws Exception {
        // Arrange
        String traceId = UUID.randomUUID().toString();
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setOperator("DRC_AIRTEL_MONEY");
        transactionRequest.setAccountNumber("243998568318");
        transactionRequest.setCurrency("USD/CDF");
        transactionRequest.setAmount(new BigDecimal(1.00));
        transactionRequest.setThirdPartyId("241128.004213.A001");
        transactionRequest.setReason("Collect payment");

        ApiResponse validatorResponse = new ApiResponse(200, "Validation Success");
        ApiResponse routerResponse = new ApiResponse(200, "Router Success");
        ApiResponse failureFormatterResponse = new ApiResponse(500, "Failure Formatter");

        when(LambdaClientUtil.invokeLambda(any(), any(), eq(traceId)))
            .thenReturn(validatorResponse)
            .thenReturn(routerResponse)
            .thenThrow(new RuntimeException("Transaction creation failed"));

        // Act
        ApiResponse result = handler.handleRequest(transactionRequest, context);

        // Assert
        assertEquals(500, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("Transaction creation failed"));
    }

    @Test
    void testInvalidAccountNumberFormat() throws Exception {
        // Arrange
        String traceId = UUID.randomUUID().toString();
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setOperator("DRC_AIRTEL_MONEY");
        transactionRequest.setAccountNumber("INVALID_ACCOUNT"); // Invalid format
        transactionRequest.setCurrency("USD/CDF");
        transactionRequest.setAmount(new BigDecimal(1.00));
        transactionRequest.setThirdPartyId("241128.004213.A001");
        transactionRequest.setReason("Collect payment");

        ApiResponse validatorResponse = new ApiResponse(400, "Validation Failed");

        when(LambdaClientUtil.invokeLambda(any(), any(), eq(traceId))).thenReturn(validatorResponse);

        // Act
        ApiResponse result = handler.handleRequest(transactionRequest, context);

        // Assert
        assertEquals(400, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("Validation Failed"));
    }

    @Test
    void testMissingCurrency() throws Exception {
        // Arrange
        String traceId = UUID.randomUUID().toString();
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setOperator("DRC_AIRTEL_MONEY");
        transactionRequest.setAccountNumber("243998568318");
        // Missing currency field
        transactionRequest.setAmount(new BigDecimal(1.00));
        transactionRequest.setThirdPartyId("241128.004213.A001");
        transactionRequest.setReason("Collect payment");

        ApiResponse validatorResponse = new ApiResponse(400, "Validation Failed");

        when(LambdaClientUtil.invokeLambda(any(), any(), eq(traceId))).thenReturn(validatorResponse);

        // Act
        ApiResponse result = handler.handleRequest(transactionRequest, context);

        // Assert
        assertEquals(400, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("Validation Failed"));
    }

    @Test
    void testZeroOrNegativeAmount() throws Exception {
        // Arrange
        String traceId = UUID.randomUUID().toString();
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setOperator("DRC_AIRTEL_MONEY");
        transactionRequest.setAccountNumber("243998568318");
        transactionRequest.setCurrency("USD/CDF");
        transactionRequest.setAmount(new BigDecimal(0.00)); // Invalid amount
        transactionRequest.setThirdPartyId("241128.004213.A001");
        transactionRequest.setReason("Collect payment");

        ApiResponse validatorResponse = new ApiResponse(400, "Validation Failed");

        when(LambdaClientUtil.invokeLambda(any(), any(), eq(traceId))).thenReturn(validatorResponse);

        // Act
        ApiResponse result = handler.handleRequest(transactionRequest, context);

        // Assert
        assertEquals(400, result.getStatusCode());
        assertTrue(result.getBody().toString().contains("Validation Failed"));
    }


}
