package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MarkusW on 09.11.2015.
 */
public class EnvVarParamParser {

    private Map EnvVars;
    private JobConsoleLogger console;

    public EnvVarParamParser(Map EnvVars, JobConsoleLogger console){
        this.EnvVars = EnvVars;
        this.console = console;
    }


    public String Parse(String parameter)
    {
        while (ContainsEnvVarDefinition(parameter))
        {
            parameter = ReplaceEnvVarParam(parameter);
        }
        return parameter;
    }

    private boolean ContainsEnvVarDefinition(String parameter){
        Matcher m = getMatcher(parameter);
        return m.find();
    }

    private String ReplaceEnvVarParam(String parameter)
    {
        String envVar = getFirstEnvVar(parameter);
        String envVarValue = getEnvVarValue(envVar);
        return parameter.replace(envVar, envVarValue);

    }

    private String getEnvVarValue(String envVar){
        envVar = envVar.substring(envVar.indexOf("{") + 1, envVar.indexOf("}"));
        if(EnvVars.get(envVar) != null)
        {
            return EnvVars.get(envVar).toString();
        }
        // we will not replace then...
        return envVar;
    }

    private String getFirstEnvVar(String parameter) {
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

    private Pattern getPattern(){
        return Pattern.compile("\\$\\{(.*?)\\}");
    }

    private void Log(String message)
    {
        this.console.printLine("[EnvVarParamParser] " + message);
    }


}
