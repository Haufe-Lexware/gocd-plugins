package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MarkusW on 09.11.2015.
 */
public abstract class ParamParser {

    // has to return the value of the parameter
    protected abstract String getParamVarValue(String paramVar);

    // has to return the regex pattern to search for
    protected abstract Pattern getPattern();

    protected JobConsoleLogger console;

    public ParamParser(JobConsoleLogger console){
        this.console = console;
    }

    public String Parse(String parameter)
    {
        String resulParam = parameter;
        while (ContainsParamVarDefinition(resulParam))
        {
            resulParam = ReplaceParamVar(resulParam);
        }
        return resulParam;
    }

    private boolean ContainsParamVarDefinition(String parameter){
        Matcher m = getMatcher(parameter);
        return m.find();
    }

    private String ReplaceParamVar(String parameter)
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
