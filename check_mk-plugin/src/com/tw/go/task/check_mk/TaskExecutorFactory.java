package com.tw.go.task.check_mk;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.Context;

import java.util.Map;

public class TaskExecutorFactory
{
    public static CheckMkTaskExecutor Create(JobConsoleLogger console, Context context, Map config) throws JobNotSupportedException {
        String action = (String) ((Map) config.get(CheckMkTask.ACTION)).get("value");
        switch (action)
        {
            case "add":return new AddHostTaskExecutor(console,context,config);
            case "remove":return new RemoveHostTaskExecutor(console,context,config);
        }
        throw new JobNotSupportedException("Action "+action+" not supported!");
    }
}
