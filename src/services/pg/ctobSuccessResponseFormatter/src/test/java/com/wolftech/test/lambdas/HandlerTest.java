package com.wolftech.test.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.wolftech.common.dto.ApiResponse;
import com.wolftech.common.dto.SuccessResponseFormatterRequest;
import com.wolftech.common.dto.TransactionRequest;
import com.wolftech.common.dto.OperatorResponse;
import com.wolftech.lambdas.Handler;

import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper; 

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HandlerTest {

    private Handler handler;
    private Context context;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        handler = new Handler();
        context = mock(Context.class);
        Map<String, String> custom = new HashMap<>();
        custom.put("traceId", "test-trace-id");
        when(context.getClientContext()).thenReturn(mock(com.amazonaws.services.lambda.runtime.ClientContext.class));
        when(context.getClientContext().getCustom()).thenReturn(custom);
    }

    @Test
    void testHandleRequest_ValidInput() {
        SuccessResponseFormatterRequest request = new SuccessResponseFormatterRequest();
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setTransactionId("txn-123");
        transactionRequest.setThirdPartyId("tp-123");
        request.setTransactionRequest(transactionRequest);

        OperatorResponse operatorResponse = new OperatorResponse("Success", "qr-code", "http://example.com");
        request.setOperatorResponse(operatorResponse);
        Map<String, Object> input = objectMapper.convertValue(request, Map.class);
        ApiResponse response = handler.handleRequest(input, context);

        assertEquals(200, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Success", responseBody.get("message"));
        assertEquals("txn-123", responseBody.get("transactionId"));
        assertEquals("tp-123", responseBody.get("thirdPartyId"));
        assertEquals("qr-code", responseBody.get("qr"));
        assertEquals("http://example.com", responseBody.get("redirectUrl"));
    }

    @Test
    void testHandleRequest_MissingTransactionId() {
        SuccessResponseFormatterRequest request = new SuccessResponseFormatterRequest();
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setThirdPartyId("tp-123");
        request.setTransactionRequest(transactionRequest);

        OperatorResponse operatorResponse = new OperatorResponse("Success", null, null);
        request.setOperatorResponse(operatorResponse);

        Map<String, Object> input = objectMapper.convertValue(request, Map.class);
        ApiResponse response = handler.handleRequest(input, context);

        assertEquals(500, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Transaction failed."));
    }

    @Test
    void testHandleRequest_InvalidInput() {
        Map<String, Object> input = new HashMap<>();
        input.put("invalidKey", "invalidValue");

        ApiResponse response = handler.handleRequest(input, context);

        assertEquals(500, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Transaction failed."));
    }

    @Test
    void testHandleRequest_MissingOperatorResponse() {
        SuccessResponseFormatterRequest request = new SuccessResponseFormatterRequest();
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setTransactionId("txn-123");
        transactionRequest.setThirdPartyId("tp-123");
        request.setTransactionRequest(transactionRequest);

        Map<String, Object> input = objectMapper.convertValue(request, Map.class);
        ApiResponse response = handler.handleRequest(input, context);

        assertEquals(500, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Transaction failed."));
    }

    @Test
    void testHandleRequest_InternalServerError() {
        Map<String, Object> input = new HashMap<>();
        input.put("statusCode", 500);

        ApiResponse response = handler.handleRequest(input, context);

        assertEquals(500, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Transaction failed."));
    }
}
