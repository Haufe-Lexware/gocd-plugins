package com.tw.go.plugin.common.utils;

import com.tw.go.plugin.common.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MarkusW on 10.11.2015.
 */
public class Environment {

    public static Context getDefaultContext() {
        Map contextMap = new HashMap();
        contextMap.put("workingDirectory", ".");
        contextMap.put("environmentVariables", getDefaultEnvVarMap());
        return new Context(contextMap);
    }

    public static Map getDefaultEnvVarMap(){
        Map envVars = new HashMap();
        envVars.put("GO_SERVER_URL","https://localhost:8154/go/" );
        envVars.put("GO_PIPELINE_NAME", "TestPluginsPipeline");
        envVars.put("GO_PIPELINE_COUNTER", "1");
        envVars.put("GO_STAGE_NAME", "TestPluginStage");
        envVars.put("GO_STAGE_COUNTER", "1");
        envVars.put("GO_JOB_NAME", "TestPluginJob");
        envVars.put("GO_TRIGGER_USER", "TestUser");

        return envVars;
    }
}
