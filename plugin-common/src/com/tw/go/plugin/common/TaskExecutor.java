package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by MarkusW on 10.11.2015.
 */
public abstract class TaskExecutor {

    protected JobConsoleLogger console;
    protected Context context;
    protected Map config; // contains <Key, Value> with value = MAP  {secure=boolean, value=string, required=boolean}


    public TaskExecutor(JobConsoleLogger console, Context context, Map config){
        this.console = console;
        this.context = context;
        this.config = config;

        // Replace Parameters
        ReplaceEnvVarsInConfig(config);

        // Log context
        Log(context.getEnvironmentVariables());
    }

    protected abstract String getPluginLogPrefix();

    protected void Log(String message) {
        this.console.printLine(getPluginLogPrefix() + message);
    }

    protected void Log(Map map) {
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            this.console.printLine(getPluginLogPrefix() + entry.getKey() + " : " + entry.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }
/*    protected void Log(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet())
        {
            this.console.printLine(getPluginLogPrefix() + entry.getKey() + " : " + entry.getValue());
        }
    }
*/
    protected Map ReplaceEnvVarsInConfig(Map config){
        Iterator it = config.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            EnvVarParamParser parser = new EnvVarParamParser(context.getEnvironmentVariables(), console);

            Log ( "Value: " + entry.getValue().toString());
            Log ( "Key: " + entry.getKey().toString());

            Map valueMap = ((Map) entry.getValue());

            String value = parser.Parse((String)valueMap.get("value"));
            Log("config value replaced: " + value);
            valueMap.put("value", value);

            entry.setValue(valueMap);
        }

        return config;

        /*
        Map<String, String> map = config;
        for (Map.Entry<String, String> entry : map.entrySet())
        {
            EnvVarParamParser parser = new EnvVarParamParser(context.getEnvironmentVariables(), console);
            entry.setValue(parser.Parse(entry.getValue()));
        }
        return config;
        */
    }
}
