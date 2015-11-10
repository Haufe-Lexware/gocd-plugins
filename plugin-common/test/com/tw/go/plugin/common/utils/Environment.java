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
        envVars.put("GO_SERVER_URL","https://localhost:8154/go" );

        /*        .with(GO_SERVER_DASHBOARD_URL, "http://go.server:8153")
                .with("GO_SERVER_URL", "https://localhost:8154/go")
                .with("GO_PIPELINE_NAME", "s3-publish-test")
                .with("GO_PIPELINE_COUNTER", "20")
                .with("GO_STAGE_NAME", "build-and-publish")
                .with("GO_STAGE_COUNTER", "1")
                .with("GO_JOB_NAME", "publish")
                .with("GO_TRIGGER_USER", "Krishna")

        */

        return envVars;
    }
}
