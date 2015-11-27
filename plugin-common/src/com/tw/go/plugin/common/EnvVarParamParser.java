package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.util.Map;
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

    @Override
    protected String getParamVarValue(String envVarParam){
        String envVar = envVarParam.substring(envVarParam.indexOf("{") + 1, envVarParam.indexOf("}"));
        if(EnvVars.get(envVar) != null)
        {
            return EnvVars.get(envVar).toString();
        }
        // we will not replace then...
        return envVar;
    }

    @Override
    protected Pattern getPattern(){
        return Pattern.compile("\\$\\{(.*?)\\}");
    }

}
