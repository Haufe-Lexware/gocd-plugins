
package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;

import java.util.HashMap;
import java.util.Map;

public class Result {
    private boolean success;
    private String message;
    private Exception exception;

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }


    public Result(boolean success, String message, Exception exception) {
        this(success, message);
        this.exception = exception;
    }

    public Map toMap() {
        final HashMap result = new HashMap();
        result.put("success", success);
        result.put("message", message);
        result.put("exception", exception);
        return result;
    }

    public int responseCode() {
        return success ? DefaultGoApiResponse.SUCCESS_RESPONSE_CODE : DefaultGoApiResponse.INTERNAL_ERROR;
    }
}