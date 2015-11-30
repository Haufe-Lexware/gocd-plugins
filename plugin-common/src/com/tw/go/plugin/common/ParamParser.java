package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MarkusW on 09.11.2015.
 */
public abstract class ParamParser {


    protected JobConsoleLogger console;

    // has to return the value of the parameter
    protected abstract String getParamVarValue(String paramVar);

    // has to return the regex pattern to search for
    protected abstract Pattern getPattern();

    public ParamParser(JobConsoleLogger console){
        this.console = console;
    }

    public String parse(String parameter)
    {
        String resulParam = parameter;
        while (containsParamVarDefinition(resulParam))
        {
            resulParam = replaceParamVar(resulParam);
        }
        return resulParam;
    }

    private boolean containsParamVarDefinition(String parameter){
        Matcher m = getMatcher(parameter);
        return m.find();
    }

    private String replaceParamVar(String parameter)
    {
        String envVar = getFirstParam(parameter);
        String envVarValue = getParamVarValue(envVar);
        return parameter.replace(envVar, envVarValue);

    }

    private String getFirstParam(String parameter) {
        Matcher m = getMatcher(parameter);
        if(m.find())
        {
            return m.group(0);
        }

        throw new InvalidParameterException("Parameter does not contain an environment variable");
    }

    private Matcher getMatcher(String parameter){
        Pattern p  = getPattern();
        return p.matcher(parameter);
    }

}
