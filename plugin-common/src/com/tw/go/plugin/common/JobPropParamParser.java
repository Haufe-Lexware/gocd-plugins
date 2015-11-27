package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.io.IOException;
import java.security.GeneralSecurityException;
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

    @Override
    protected String getParamVarValue(String propertiesVar) {
        propertiesVar = propertiesVar.substring(propertiesVar.indexOf("{") + 1, propertiesVar.indexOf("}"));

        // get property from current job
        try {
            GoApiClient client = new GoApiClient(EnvVars.get("GO_SERVER_URL").toString());


            // get go build user authorization
            if (EnvVars.get("GO_BUILD_USER") != null &&
                    EnvVars.get("GO_BUILD_USER_PASSWORD") != null) {
                Log("Authorization set");
                Log("User: " + EnvVars.get("GO_BUILD_USER").toString());
                Log("Password: " + EnvVars.get("GO_BUILD_USER_PASSWORD").toString());

                client.setBasicAuthentication(EnvVars.get("GO_BUILD_USER").toString(), EnvVars.get("GO_BUILD_USER_PASSWORD").toString());
            }


            String jobProperty = "";
            jobProperty = client.getJobProperty(
                        EnvVars.get("GO_PIPELINE_NAME").toString(),
                        EnvVars.get("GO_PIPELINE_COUNTER").toString(),
                        EnvVars.get("GO_STAGE_NAME").toString(),
                        EnvVars.get("GO_STAGE_COUNTER").toString(),
                        EnvVars.get("GO_JOB_NAME").toString(),
                        propertiesVar);

            if (!jobProperty.isEmpty()) {
                propertiesVar = jobProperty;
            }
        }

        catch (GeneralSecurityException e)
        {
            Log(e);
        }
        catch (IOException e) {
            Log(e);
        }


        return propertiesVar;
    }

    @Override
    protected Pattern getPattern(){
        return Pattern.compile("\\%\\{(.*?)\\}");
    }

    private void Log(String message) {
        this.console.printLine("[JobPropParamParser] " + message);
    }
    private void Log(Exception e) {
        this.console.printLine("[JobPropParamParser] " + e.getMessage() + e.getStackTrace().toString());
    }
}
