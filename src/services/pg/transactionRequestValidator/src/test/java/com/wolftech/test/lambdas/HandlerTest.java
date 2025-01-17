package com.wolftech.test.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.wolftech.common.dto.ApiResponse;
import com.wolftech.lambdas.Handler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class HandlerTest {

    private Handler handler;
    private Context context;

    private static final Logger logger = LogManager.getLogger(HandlerTest.class);

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
        Map<String, Object> input = new HashMap<>();
        input.put("operator", "test-operator");
        input.put("accountNumber", "123456789");
        input.put("currency", "USD");
        input.put("amount", 100);
        input.put("thirdPartyId", "test-id");
        input.put("reason", "test-reason");

        ApiResponse response = handler.handleRequest(input, context);
        logger.info("Apiresponse: "+response);
        assertEquals(200, response.getStatusCode());
        assertEquals("Transaction processed successfully!", response.getBody().toString());
    }

    @Test
    void testHandleRequest_MissingOperator() {
        Map<String, Object> input = new HashMap<>();
        input.put("accountNumber", "123456789");
        input.put("currency", "USD");
        input.put("amount", 100);
        input.put("thirdPartyId", "test-id");
        input.put("reason", "test-reason");

        ApiResponse response = handler.handleRequest(input, context);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("'operator' is required"));
    }

    @Test
    void testHandleRequest_MissingAccountNumber() {
        Map<String, Object> input = new HashMap<>();
        input.put("operator", "test-operator");
        input.put("currency", "USD");
        input.put("amount", 100);
        input.put("thirdPartyId", "test-id");
        input.put("reason", "test-reason");

        ApiResponse response = handler.handleRequest(input, context);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("'accountNumber' is required"));
    }

    @Test
    void testHandleRequest_MissingCurrency() {
        Map<String, Object> input = new HashMap<>();
        input.put("operator", "test-operator");
        input.put("accountNumber", "123456789");
        input.put("amount", 100);
        input.put("thirdPartyId", "test-id");
        input.put("reason", "test-reason");

        ApiResponse response = handler.handleRequest(input, context);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("'currency' is required"));
    }

    @Test
    void testHandleRequest_MissingAmount() {
        Map<String, Object> input = new HashMap<>();
        input.put("operator", "test-operator");
        input.put("accountNumber", "123456789");
        input.put("currency", "USD");
        input.put("thirdPartyId", "test-id");
        input.put("reason", "test-reason");

        ApiResponse response = handler.handleRequest(input, context);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("'amount' is required"));
    }

    @Test
    void testHandleRequest_InvalidAmount() {
        Map<String, Object> input = new HashMap<>();
        input.put("operator", "test-operator");
        input.put("accountNumber", "123456789");
        input.put("currency", "USD");
        input.put("amount", -10);
        input.put("thirdPartyId", "test-id");
        input.put("reason", "test-reason");

        ApiResponse response = handler.handleRequest(input, context);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("'amount' must be greater than zero"));
    }

    @Test
    void testHandleRequest_MissingThirdPartyId() {
        Map<String, Object> input = new HashMap<>();
        input.put("operator", "test-operator");
        input.put("accountNumber", "123456789");
        input.put("currency", "USD");
        input.put("amount", 100);
        input.put("reason", "test-reason");

        ApiResponse response = handler.handleRequest(input, context);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("'thirdPartyId' is required"));
    }

    @Test
    void testHandleRequest_MissingReason() {
        Map<String, Object> input = new HashMap<>();
        input.put("operator", "test-operator");
        input.put("accountNumber", "123456789");
        input.put("currency", "USD");
        input.put("amount", 100);
        input.put("thirdPartyId", "test-id");

        ApiResponse response = handler.handleRequest(input, context);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("'reason' is required"));
    }

    @Test
    void testHandleRequest_InvalidRequest() {
        Object input = new Object();

        ApiResponse response = handler.handleRequest(input, context);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Invalid request"));
    }
    
    @Test
    void testHandleRequest_InternalServerError() {
        ApiResponse response = handler.handleRequest(null, null);

        assertEquals(500, response.getStatusCode());
    }
}
