package com.wolftech.test.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.wolftech.common.dto.ApiResponse;
import com.wolftech.common.dto.OperatorResponse;
import com.wolftech.common.dto.TransactionRequest;
import com.wolftech.lambdas.Handler;
import com.wolftech.common.utils.LambdaClientUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

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
public class HandlerTest {

    private Context context;
    private Handler handler;

    @Before
    void setUp() {
        handler = spy(new Handler());
        context = mock(Context.class);
        PowerMockito.mockStatic(LambdaClientUtil.class);

        Map<String, String> custom = new HashMap<>();
        custom.put("traceId", "test-trace-id");
        when(context.getClientContext()).thenReturn(mock(com.amazonaws.services.lambda.runtime.ClientContext.class));
        when(context.getClientContext().getCustom()).thenReturn(custom);
    }

    @Test
    public void testHandleRequest_Success_AirtelMoney() {
        String traceId = UUID.randomUUID().toString();
        TransactionRequest request = new TransactionRequest();
        request.setOperator("DRC_AIRTEL_MONEY");
        ApiResponse validatorResponse = new ApiResponse(200, "Success");
        when(LambdaClientUtil.invokeLambda(any(), any(), eq(traceId))).thenReturn(validatorResponse);
        ApiResponse response = handler.handleRequest(request, context);

        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBody().toString());
    }

    @Test
    public void testHandleRequest_UnsupportedOperator() {
        TransactionRequest request = new TransactionRequest();
        request.setOperator("UNKNOWN_OPERATOR");

        ApiResponse response = handler.handleRequest(request, context);

        assertEquals(500, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Unsupported operator"));
    }

    @Test
    public void testHandleRequest_ExceptionHandling() {
        Object invalidRequest = new Object();
        ApiResponse response = handler.handleRequest(invalidRequest, context);

        assertEquals(500, response.getStatusCode());
        assertNotNull(response.getBody().toString());
    }

}
