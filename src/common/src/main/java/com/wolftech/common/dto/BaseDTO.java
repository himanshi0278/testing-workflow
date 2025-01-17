package com.wolftech.common.dto;

import java.io.Serializable;

public class BaseDTO implements Serializable {
    
    private String traceId;

    public BaseDTO() {
    }

    public BaseDTO(String traceId) {
        this.traceId = traceId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public String toString() {
        return ", traceId='" + traceId + '\'';
    }

}
