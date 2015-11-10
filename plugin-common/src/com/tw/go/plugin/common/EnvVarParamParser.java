package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

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
        if(ContainsEnvVarDefinition(parameter))
        {

        }

        return parameter;
    }

    private boolean ContainsEnvVarDefinition(String parameter){
        Pattern p  = Pattern.compile(Pattern.quote("${*}"));
        Matcher m = p.matcher(parameter);
        return m.matches();
    }

    private String ReplaceEnvVarDefinition(String parameter)
    {
        // get env var name
      //  String envVarValue =

        // replace
       // parameter.

        return parameter;
    }

    private void Log(String message)
    {
        this.console.printLine("[EnvVarParser] " + message);
    }


}
