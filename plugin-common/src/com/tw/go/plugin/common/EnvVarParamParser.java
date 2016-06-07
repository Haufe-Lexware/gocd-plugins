package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by MarkusW on 09.11.2015.
 */
public class EnvVarParamParser extends ParamParser {

    private Map envVars;

    public EnvVarParamParser(Map envVars, JobConsoleLogger console){
        super(console);
        this.envVars = envVars;
    }

    @Override
    protected String getParamVarValue(String envVarParam){
        String envVar = envVarParam.substring(envVarParam.indexOf("{") + 1, envVarParam.indexOf("}"));
        if(envVars.get(envVar) != null)
        {
            return envVars.get(envVar).toString();
        }
        // we will not replace then...
        return envVar;
    }

    @Override
    protected Pattern getPattern(){
        return Pattern.compile("\\$\\{(.*?)\\}");
    }

}
