package com.wolftech.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.wolftech.common.dto.ApiResponse;
import com.wolftech.common.dto.FailureFormatterRequest;
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
        FailureFormatterRequest request = new FailureFormatterRequest("Invalid request data", 400, "");
        Map<String, Object> input = objectMapper.convertValue(request, Map.class);
        
        ApiResponse response = handler.handleRequest(input, context);

        assertEquals(400, response.getStatusCode());
        assertEquals("Invalid request data", response.getBody());
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
    void testHandleRequest_MissingBody() {

        ApiResponse response = handler.handleRequest(null, context);

        assertEquals(500, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Transaction failed."));
    }

    @Test
    void testHandleRequest_ExceptionHandling() {
        Map<String, Object> input = new HashMap<>();
        input.put("errorCode", "invalid-status-code");

        ApiResponse response = handler.handleRequest(input, context);

        assertEquals(500, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Transaction failed."));
    }
}
