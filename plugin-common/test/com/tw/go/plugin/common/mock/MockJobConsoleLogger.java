package com.tw.go.plugin.common.mock;


import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.thoughtworks.go.plugin.api.task.TaskExecutionContext;

/**
 * Created by MarkusW on 10.11.2015.
 */
public class MockJobConsoleLogger extends JobConsoleLogger {

    public MockJobConsoleLogger(TaskExecutionContext context) {
        this.context = context;
    }
}
