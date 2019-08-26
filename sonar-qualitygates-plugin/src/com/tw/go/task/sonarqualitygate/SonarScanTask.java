
package com.tw.go.task.sonarqualitygate;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.tw.go.plugin.common.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;;

@Extension
public class SonarScanTask extends BaseGoPlugin {

    public static final String ISSUE_TYPE_FAIL = "IssueTypeFail";
    public static final String SONAR_API_URL = "SonarApiUrl";
    public static final String SONAR_PROJECT_KEY = "SonarProjectKey";
    public static final String STAGE_NAME = "StageName";
    public static final String JOB_NAME = "JobName";
    public static final String JOB_COUNTER = "JobCounter";

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
        // not required in most plugins
    }

    @Override
    protected GoPluginApiResponse handleTaskView(GoPluginApiRequest request) {
        return getViewResponse("SonarQube - Quality Gate",
                getClass().getResourceAsStream("/views/task.template.html"));
    }

    @Override
    protected GoPluginApiResponse handleGetConfigRequest(GoPluginApiRequest request) {
        return success(getConfigDef());
    }

    @Override
    protected GoPluginApiResponse handleValidation(GoPluginApiRequest request) {
        HashMap result = new HashMap();
        return success(result);
    }

    protected GoPluginApiResponse handleTaskExecution(GoPluginApiRequest request) {
        Map executionRequest = (Map) new GsonBuilder().create().fromJson(request.requestBody(), Object.class);

        Map<String, Map> config = (Map) executionRequest.get("config");

        fixConfigAttrs(config,getConfigDef());

        Context context = new Context((Map) executionRequest.get("context"));

        Map<String, String> envVars = context.getEnvironmentVariables();

        SonarTaskExecutor executor = new SonarTaskExecutor(
                MaskingJobConsoleLogger.getConsoleLogger(), context, config);

        // The RESPONSE_CODE MUST be 200 (SUCCESS) to be processed by Go.CD
        // The outcome is defined by the property "success", and the cause can be stored in "message"
        // See
        //  public <T> T submitRequest(String pluginId, String requestName, PluginInteractionCallback<T> pluginInteractionCallback) {
        // in
        //  .../gocd/plugin-infra/go-plugin-access/src/com/thoughtworks/go/plugin/access/PluginRequestHelper.java

        try {
            Result result = executor.execute();
            return success(result.toMap());
        } catch (Exception e) {
            Result result = new Result(false,e.getMessage());
            return success(result.toMap());
        }

    }

    private void fixConfigAttrs(Map<String, Map> configAct, Map<String, Map> configDef) {
        for (Map.Entry<String,Map> e : configAct.entrySet()) {
            Map def = configDef.get(e.getKey());
            Map act = e.getValue();
            act.put("required",def.get("required"));
            act.put("secure",def.get("secure"));
        }
    }

    private Map getConfigDef() {
        return new ConfigDef()
                .add(STAGE_NAME, "", Required.NO)
                .add(JOB_NAME, "", Required.NO)
                .add(JOB_COUNTER, "", Required.NO)
                .add(SONAR_PROJECT_KEY, "", Required.YES)
                .add(ISSUE_TYPE_FAIL, "error", Required.YES)
                .add(SONAR_API_URL, "", Required.YES)
                .toMap();
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return new GoPluginIdentifier("task", Arrays.asList("1.0"));
    }
}