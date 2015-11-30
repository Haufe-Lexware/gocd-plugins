package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by MarkusW on 17.11.2015.
 */
public class JobPropParamParser extends  ParamParser{

    private static final String GO_SERVER_URL = "GO_SERVER_URL";
    private static final String GO_BUILD_USER = "GO_BUILD_USER";
    private static final String GO_BUILD_USER_PASSWORD = "GO_BUILD_USER_PASSWORD";

    private Map envVars;

    public JobPropParamParser( Map envVars, JobConsoleLogger console){
        super(console);
        this.envVars = envVars;
    }

    @Override
    protected String getParamVarValue(String propertiesVarParam) {
        String propertiesVar = propertiesVarParam.substring(propertiesVarParam.indexOf("{") + 1, propertiesVarParam.indexOf("}"));

        // get property from current job
        try {
            GoApiClient client = new GoApiClient(envVars.get(GO_SERVER_URL).toString());


            // get go build user authorization
            if (envVars.get(GO_BUILD_USER) != null &&
                    envVars.get(GO_BUILD_USER_PASSWORD) != null) {
                log("Authorization set");
                log("User: " + envVars.get(GO_BUILD_USER).toString());
                log("Password: " + envVars.get(GO_BUILD_USER_PASSWORD).toString());

                client.setBasicAuthentication(envVars.get(GO_BUILD_USER).toString(), envVars.get(GO_BUILD_USER_PASSWORD).toString());
            }


            String jobProperty = "";
            jobProperty = client.getJobProperty(
                        envVars.get("GO_PIPELINE_NAME").toString(),
                        envVars.get("GO_PIPELINE_COUNTER").toString(),
                        envVars.get("GO_STAGE_NAME").toString(),
                        envVars.get("GO_STAGE_COUNTER").toString(),
                        envVars.get("GO_JOB_NAME").toString(),
                        propertiesVar);

            if (!jobProperty.isEmpty()) {
                propertiesVar = jobProperty;
            }
        }

        catch (GeneralSecurityException e)
        {
            log(e);
        }
        catch (IOException e) {
            log(e);
        }


        return propertiesVar;
    }

    @Override
    protected Pattern getPattern(){
        return Pattern.compile("\\%\\{(.*?)\\}");
    }

    private void log(String message) {
        this.console.printLine(getLogName() + message);
    }
    private void log(Exception e) {
        this.console.printLine(getLogName() + e.getMessage() + Arrays.toString(e.getStackTrace()));
    }
    private String getLogName(){
        return "[JobPropParamParser] ";
    }

}
