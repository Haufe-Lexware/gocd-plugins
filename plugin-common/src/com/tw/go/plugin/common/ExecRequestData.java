package com.tw.go.plugin.common;

import java.util.Map;

public class ExecRequestData extends RequestData {
    public ExecRequestData(String payload) {
        super(payload);
    }

    public Map config() {
        return executionRequest.get("config");
    }

    public Map context() {
        return executionRequest.get("context");
    }

    public Map<String, String> envVars() {
        return (Map<String, String>) context().get("environmentVariables");
    }

    public String workDir() {
        return (String) context().get("workingDirectory");
    }
}