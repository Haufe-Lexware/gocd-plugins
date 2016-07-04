package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by MarkusW on 10.11.2015.
 */
public abstract class TaskExecutor {

    public static final String REGEX_URL_CREDS = "(?<=://)[^:@]+(:[^@]+)?(?=@)";
    public static final String GO_WORKING_DIRECTORY = "GO_WORKING_DIRECTORY";

    protected JobConsoleLogger console;
    protected Context context;
    protected Map config; // contains a key value pair <Key, Value> where Value is a map with content  {secure=boolean, value=string, required=boolean}
    protected ConfigVars configVars;

    public TaskExecutor(JobConsoleLogger console, Context context, Map config) {

        Map<String, String> envVars = context.getEnvironmentVariables();

        // So external commands work "in" the pipeline directory and not in some parent directory
        AbstractCommand.setWorkingDir(context.getWorkingDir());

        envVars.put(GO_WORKING_DIRECTORY,AbstractCommand.getAbsoluteWorkingDir());

        HashSet<String> maskingList = new HashSet<>();
        maskingList.add(REGEX_URL_CREDS);

        configVars = expandConfigProperties(config, envVars, maskingList);

        // yes, we ignore the "standard" console (for now) and just use the masking one ...
        this.console = new MaskingJobConsoleLogger()
                .withPrefix(getPluginLogPrefix())
                .withMapFilter(envVars)
                .withRegexFilter(REGEX_URL_CREDS);

        // tbd: integrate "better". the following loop is only here because
        // it was forgotten to be done "otherwise" :-(
        for (String s : maskingList) {
            ((MaskingJobConsoleLogger)this.console).withRegexFilter(s);
        }

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

    protected void logException(Logger logger, Throwable throwable) throws UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        throwable.printStackTrace(new PrintStream(baos));
        String output = baos.toString(StandardCharsets.UTF_8.name());
        logger.error(throwable + "\n" + output);
    }
}
