package com.wolftech.common.dto;

import java.io.Serializable;

public class ApiResponse implements Serializable {
    private int statusCode;
    private Object body;

    public ApiResponse() {
    }

    public ApiResponse(int statusCode, Object body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Object getBody() {
        return body;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}