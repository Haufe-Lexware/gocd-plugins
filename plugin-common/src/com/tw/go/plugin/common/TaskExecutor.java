package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by MarkusW on 10.11.2015.
 */
public abstract class TaskExecutor {

    public static final String CONFIG_VALUE = "value";
    public static final String CONFIG_SECURE = "secure";
    public static final String CONFIG_REQUIRED = "required";

    protected JobConsoleLogger console;
    protected Context context;
    protected Map config; // contains a key value pair <Key, Value> where Value is a map with content  {secure=boolean, value=string, required=boolean}


    public TaskExecutor(JobConsoleLogger console, Context context, Map config){
        this.console = console;
        this.context = context;
        this.config = config;

        // Replace Parameters
        replaceEnvVarsAndPropertiesInConfig(config);

        // Log context
        log(context.getEnvironmentVariables());
    }

    protected abstract String getPluginLogPrefix();

    protected void log(String message) {
        this.console.printLine(getPluginLogPrefix() + message);
    }

    protected void log(Map map) {
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            this.console.printLine(getPluginLogPrefix() + entry.getKey() + " : " + entry.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    protected Map replaceEnvVarsAndPropertiesInConfig(Map config){
        Iterator it = config.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            EnvVarParamParser parser = new EnvVarParamParser(context.getEnvironmentVariables(), console);
            JobPropParamParser propParser = new JobPropParamParser(context.getEnvironmentVariables(), console);

            Map valueMap = (Map) entry.getValue();

            String value = propParser.parse(parser.parse((String) valueMap.get("value")));
            log("config value replaced: " + value);
            valueMap.put("value", value);

            entry.setValue(valueMap);
        }

        return config;

    }
}
