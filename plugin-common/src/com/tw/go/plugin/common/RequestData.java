package com.tw.go.plugin.common;

import com.google.gson.GsonBuilder;

import java.util.Map;

public class RequestData {
    protected Map<String,Map> executionRequest;

    public RequestData(String payload) {
        executionRequest = (Map) new GsonBuilder().create().fromJson(payload, Object.class);
    }
}
