package com.tw.go.plugin.common.utils;

import com.tw.go.plugin.common.Context;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by MarkusW on 10.11.2015.
 */
public class Environment {

    public static Context getDefaultContext(Properties props) {
        Map contextMap = new HashMap();
        contextMap.put("workingDirectory", ".");
        contextMap.put("environmentVariables", getDefaultEnvVarMap(props));
        return new Context(contextMap);
    }

    public static Map getDefaultEnvVarMap(Properties props){

        Map envVars = new HashMap();
        envVars.put("GO_SERVER_URL",props.get("GO_SERVER_URL"));
        envVars.put("GO_PIPELINE_NAME", props.get("GO_PIPELINE_NAME"));
        envVars.put("GO_PIPELINE_COUNTER", props.get("GO_PIPELINE_COUNTER"));
        envVars.put("GO_STAGE_NAME", props.get("GO_STAGE_NAME"));
        envVars.put("GO_STAGE_COUNTER", props.get("GO_STAGE_COUNTER"));
        envVars.put("GO_JOB_NAME", props.get("GO_JOB_NAME"));
        envVars.put("GO_TRIGGER_USER", props.get("GO_TRIGGER_USER"));
        envVars.put("GO_BUILD_USER_PASSWORD", props.get("GO_BUILD_USER_PASSWORD"));
        envVars.put("GO_BUILD_USER", props.get("GO_BUILD_USER"));

        return envVars;
    }
}
