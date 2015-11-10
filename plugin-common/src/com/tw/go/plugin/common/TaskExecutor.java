package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.util.Map;

/**
 * Created by MarkusW on 10.11.2015.
 */
public abstract class TaskExecutor {

    protected JobConsoleLogger console;
    protected Context context;
    protected Map config;

    public TaskExecutor(JobConsoleLogger console, Context context, Map config){
        this.console = console;
        this.context = context;
        this.config = config;

        // Log context
        Log(context.getEnvironmentVariables());
    }

    protected abstract String getPluginLogPrefix();

    protected void Log(String message) {
        this.console.printLine(getPluginLogPrefix() + message);
    }

    protected void Log(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet())
        {
            this.console.printLine(getPluginLogPrefix() + entry.getKey() + " : " + entry.getValue());
        }
    }
    protected Map ReplaceEnvVarsInConfig(){
        Map<Object, String> map = config;
        for (Map.Entry<Object, String> entry : map.entrySet())
        {
            EnvVarParamParser parser = new EnvVarParamParser(context.getEnvironmentVariables(), console);
            entry.setValue(parser.Parse(entry.getValue()));
        }
        return config;
    }
}
