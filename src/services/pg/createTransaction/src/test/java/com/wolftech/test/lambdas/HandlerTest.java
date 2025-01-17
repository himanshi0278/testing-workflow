package com.wolftech.test.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.wolftech.common.dto.ApiResponse;
import com.wolftech.common.dto.TransactionRequest;
import com.wolftech.lambdas.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.ThreadContext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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
@PrepareForTest(fullyQualifiedNames = "com.wolftech.lambdas.Handler")
class HandlerTest {

    private Handler handler;
    private Context context;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger logger = LogManager.getLogger(HandlerTest.class);

    @Before
    void setUp() throws Exception {
        handler = spy(new Handler());
        context = mock(Context.class);
        Map<String, String> custom = new HashMap<>();
        custom.put("traceId", "test-trace-id");
        PowerMockito.doReturn(context).when(context.getClientContext());
        PowerMockito.doReturn(custom).when(context.getClientContext().getCustom());
    }

    @Test
    void testHandleRequest_success() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setTransactionId("12345");
        request.setAmount(new BigDecimal(100.0));
        Map<String, Object> input = objectMapper.convertValue(request, Map.class);
        PowerMockito.doNothing().when(handler, "addTransaction");

        ApiResponse response = handler.handleRequest(input, context);

        assertEquals(200, response.getStatusCode());
        assertEquals("Transaction created", response.getBody().toString());
    }

    @Test
    void testHandleRequest_failure() throws Exception{
        TransactionRequest request = new TransactionRequest();
        request.setTransactionId("12345");
        request.setAmount(new BigDecimal(100.0));
        
        Map<String, Object> input = objectMapper.convertValue(request, Map.class);
        
        PowerMockito.doThrow(new RuntimeException("Database error")).when(handler, "addTransaction");
        
        ApiResponse response = handler.handleRequest(input, context);
        
        assertEquals(500, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Transaction creation failed"));
    }

    @Test
    void testHandleRequest_missingTraceId() {
        TransactionRequest request = new TransactionRequest();
        request.setTransactionId("12345");
        request.setAmount(new BigDecimal(100.0));
        
        Map<String, Object> input = objectMapper.convertValue(request, Map.class);
        ApiResponse response = handler.handleRequest(input, context);
        
        assertEquals(500, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Transaction creation failed"));
    }
}
