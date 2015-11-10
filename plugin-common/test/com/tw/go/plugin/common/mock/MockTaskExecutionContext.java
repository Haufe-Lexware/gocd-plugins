package com.tw.go.plugin.common.mock;

import com.thoughtworks.go.plugin.api.task.Console;
import com.thoughtworks.go.plugin.api.task.EnvironmentVariables;
import com.thoughtworks.go.plugin.api.task.TaskExecutionContext;

import java.io.InputStream;
import java.util.Map;

public class MockTaskExecutionContext implements TaskExecutionContext {
    private Map<String, String> mockEnvironmentVariables;

    public MockTaskExecutionContext(Map<String, String> mockEnvironmentVariables) {
        this.mockEnvironmentVariables = mockEnvironmentVariables;
    }

    @Override
    public EnvironmentVariables environment() {
        return new EnvironmentVariables() {
            @Override
            public Map<String, String> asMap() {
                return mockEnvironmentVariables;
            }

            @Override
            public void writeTo(Console console) {
            }

            @Override
            public Console.SecureEnvVarSpecifier secureEnvSpecifier() {
                return new Console.SecureEnvVarSpecifier() {
                    @Override
                    public boolean isSecure(String s) {
                        return false;
                    }
                };
            }
        };
    }

    @Override
    public Console console() {
        return new Console() {
            @Override
            public void printLine(String s) {
            }

            @Override
            public void readErrorOf(InputStream inputStream) {
            }

            @Override
            public void readOutputOf(InputStream inputStream) {
            }

            @Override
            public void printEnvironment(Map<String, String> stringStringMap, SecureEnvVarSpecifier secureEnvVarSpecifier) {
            }
        };
    }

    @Override
    public String workingDir() {
        return ".";
    }
}
