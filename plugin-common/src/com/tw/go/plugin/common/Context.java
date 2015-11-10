

package com.tw.go.plugin.common;

import java.util.Map;

public class Context {
    private final Map environmentVariables;
    private final String workingDir;

    public Context(Map context) {
        environmentVariables = (Map) context.get("environmentVariables");
        workingDir = (String) context.get("workingDirectory");
    }

    public Map getEnvironmentVariables() {
        return environmentVariables;
    }

    public String getWorkingDir() {
        return workingDir;
    }
}