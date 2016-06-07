package com.tw.go.task.dockerpipeline;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.ConfigDef.Required;
import com.tw.go.plugin.common.ConfigDef.Secure;
import com.tw.go.plugin.common.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Extension
public class DockerTask extends BaseGoPlugin {
    public static final String IMAGE_NAME = "ImageName";
    public static final String DOCKER_FILE_NAME = "DockerFileName";
    public static final String BUILD_ARGS = "BuildArgs";

    public static final String USERNAME = "Username";
    public static final String IMAGE_TAG = "ImageTag";

    public static final String REGISTRY_USERNAME = "RegistryUsername";
    public static final String REGISTRY_PASSWORD = "RegistryPassword";

    public static final String REGISTRY_URL_FOR_LOGIN = "RegistryURLForLogin";

    public static final String CLEAN_BEFORE_TASK = "CleanBeforeTask";
    public static final String CLEAN_AFTER_TASK = "CleanAfterTask";

    private static final Logger LOGGER = Logger.getLoggerFor(DockerTask.class);

    @Override
    protected GoPluginApiResponse handleTaskView(GoPluginApiRequest request) {
        return getViewResponse("Docker plugin",
                getClass().getResourceAsStream("/views/task.template.html"));
    }

    // https://developer.go.cd/current/writing_go_plugins/task/version_1_0/execute.html
    @Override
    protected GoPluginApiResponse handleTaskExecution(GoPluginApiRequest request) {
        Map executionRequest = (Map) new GsonBuilder().create().fromJson(request.requestBody(), Object.class);

        Map<String, Map> config = (Map) executionRequest.get("config");

        fixConfigAttrs(config,getConfigDef());

        Context context = new Context((Map) executionRequest.get("context"));

        Map<String, String> envVars = context.getEnvironmentVariables();

        DockerTaskExecutor executor = new DockerTaskExecutor(
                MaskingJobConsoleLogger.getConsoleLogger(), context, config);

        Result result = executor.execute();

        // The RESPONSE_CODE MUST be 200 (SUCCESS) to be processed by Go.CD
        // The outcome is defined by the property "success", and the cause can be stored in "message"
        // See
        //  public <T> T submitRequest(String pluginId, String requestName, PluginInteractionCallback<T> pluginInteractionCallback) {
        // in
        //  .../gocd/plugin-infra/go-plugin-access/src/com/thoughtworks/go/plugin/access/PluginRequestHelper.java

        return success(result.toMap());
    }

    @Override
    protected GoPluginApiResponse handleValidation(GoPluginApiRequest request) {
        HashMap result = new HashMap();
        return success(result);
    }

    @Override
    protected GoPluginApiResponse handleGetConfigRequest(GoPluginApiRequest request) {
        return success(getConfigDef());
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
                // cleaning ...
                .add(CLEAN_BEFORE_TASK, "false", Required.No)
                .add(CLEAN_AFTER_TASK, "false", Required.No)

                // build
                .add(DOCKER_FILE_NAME, "", Required.No)
                .add(BUILD_ARGS, "", Required.No)
                .add(IMAGE_NAME, "", Required.No)

                // tag
                .add(IMAGE_TAG, "", Required.No)
                .add(USERNAME, "", Required.No)

                // login
                .add(REGISTRY_URL_FOR_LOGIN, "", Required.No)
                .add(REGISTRY_USERNAME, "", Required.No)
                .add(REGISTRY_PASSWORD, "", Required.No, Secure.Yes)

                //
                .toMap();
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return new GoPluginIdentifier("task", Arrays.asList("1.0"));
    }
}