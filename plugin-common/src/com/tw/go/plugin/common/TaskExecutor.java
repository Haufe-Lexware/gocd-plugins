package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by MarkusW on 10.11.2015.
 */
public abstract class TaskExecutor {

    public static final String REGEX_URL_CREDS = "(?<=://)[^:@]+(:[^@]+)?(?=@)";

    protected JobConsoleLogger console;
    protected Context context;
    protected Map config; // contains a key value pair <Key, Value> where Value is a map with content  {secure=boolean, value=string, required=boolean}
    protected ConfigVars configVars;

    public TaskExecutor(JobConsoleLogger console, Context context, Map config) {

        Map<String, String> envVars = context.getEnvironmentVariables();
        envVars.put(GoApiConstants.ENVVAR_NAME_GO_WORKING_DIR, context.getWorkingDir());


        HashSet<String> maskingList = new HashSet<>();
        maskingList.add(REGEX_URL_CREDS);

        configVars = expandConfigProperties(config, envVars, maskingList);

        // yes, we ignore the "standard" console (for now) and just use the masking one ...
        this.console = new MaskingJobConsoleLogger()
                .withPrefix(getPluginLogPrefix())
                .withMapFilter(envVars)
                .withRegexFilter(REGEX_URL_CREDS);

        this.context = context;
        this.config = config;

        for (String s : configVars.configKeys()) {
            this.console.printLine("configuration setting '" + s + "' is '" + configVars.getValue(s) + "'");
        }
    }

    protected ConfigVars expandConfigProperties(Map<String, Map> config, Map<String, String> envVars, HashSet<String> maskingList) {
        VarsExpander varsExpander = new VarsExpander(envVars);
        for (Map.Entry<String, Map> e : config.entrySet()) {
            String secure = e.getValue().get("secure").toString();
            String value = e.getValue().get("value").toString();
            String newValue = varsExpander.expand(value);
            e.getValue().put("value", newValue);
            if ("true".equals(secure) && (newValue.length() > 0)) {
                maskingList.add("\\b" + Pattern.quote(newValue) + "\\b");
            }
        }
        return new ConfigVars(config, envVars);
    }

    public abstract Result execute() throws Exception;

    protected abstract String getPluginLogPrefix();

    protected void log(String message) {
        this.console.printLine(message);
    }
}