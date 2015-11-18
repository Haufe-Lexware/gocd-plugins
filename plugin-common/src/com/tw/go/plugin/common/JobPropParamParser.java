package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by MarkusW on 17.11.2015.
 */
public class JobPropParamParser extends  ParamParser{

    private Map EnvVars;

    public JobPropParamParser( Map envVars, JobConsoleLogger console){
        super(console);
        this.EnvVars = envVars;
    }

    protected String getParamVarValue(String propertiesVar){
        propertiesVar = propertiesVar.substring(propertiesVar.indexOf("{") + 1, propertiesVar.indexOf("}"));

        // get property from current job
        GoApiClient client = new GoApiClient(EnvVars.get("GO_SERVER_URL").toString());
        String jobProperty = "";
        try
        {
            jobProperty = client.getJobProperty(
                    EnvVars.get("GO_PIPELINE_NAME").toString(),
                    EnvVars.get("GO_PIPELINE_COUNTER").toString(),
                    EnvVars.get("GO_STAGE_NAME").toString(),
                    EnvVars.get("GO_STAGE_COUNTER").toString(),
                    EnvVars.get("GO_JOB_NAME").toString(),
                    propertiesVar );
        }
        catch (Exception e)
        {
            Log(e.getMessage());
        }
        if(!jobProperty.isEmpty())
        {
            return jobProperty;
        }

        return propertiesVar;
    }

    protected Pattern getPattern(){
        return Pattern.compile("\\%\\{(.*?)\\}");
    }

    private void Log(String message) {
        this.console.printLine("[JobPropParamParser] " + message);
    }
}
