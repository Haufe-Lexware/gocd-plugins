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
public class EnvVarParamParser extends ParamParser {

    private Map EnvVars;

    public EnvVarParamParser(Map EnvVars, JobConsoleLogger console){
        super(console);
        this.EnvVars = EnvVars;
    }

    protected String getParamVarValue(String envVar){
        envVar = envVar.substring(envVar.indexOf("{") + 1, envVar.indexOf("}"));
        if(EnvVars.get(envVar) != null)
        {
            return EnvVars.get(envVar).toString();
        }
        // we will not replace then...
        return envVar;
    }

    protected Pattern getPattern(){
        return Pattern.compile("\\$\\{(.*?)\\}");
    }

}
