package com.tw.go.task.dockerpipeline;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.tw.go.plugin.common.*;
import com.tw.go.plugin.common.ConfigDef.Required;
import com.tw.go.plugin.common.ConfigDef.Secure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE;

@Extension
public class DockerTask extends BaseGoPlugin {
    public static final String IMAGE_NAME = "ImageName";
    public static final String DOCKER_FILE_NAME = "DockerFileName";
    public static final String BUILD_ARGS = "BuildArgs";
    public static final String BUILD_NO_CACHE = "BUILD_NO_CACHE";

    public static final String USERNAME = "Username";
    public static final String IMAGE_TAG = "ImageTag";
    public static final String IMAGE_TAG_POSTFIX = "IMAGE_TAG_POSTFIX";

    public static final String REGISTRY_USERNAME = "RegistryUsername";
    public static final String REGISTRY_PASSWORD = "RegistryPassword";

    public static final String REGISTRY_EMAIL = "RegistryEmail";
    public static final String REGISTRY_URL_FOR_LOGIN = "RegistryURL";

    public static final String RUN_IMAGE = "RUN_IMAGE";
    public static final String RUN_ENV_VARS = "RUN_ENV_VARS";
    //    public static final String RUN_VOLUMES_FROM = "RUN_VOLUMES_FROM";
//    public static final String RUN_VOLUME = "RUN_VOLUME";
//    public static final String RUN_WORKING_DIR = "RUN_WORKING_DIR";
    public static final String RUN_ARGS = "RUN_ARGS";

    public static final String CLEAN_BEFORE_TASK = "CleanBeforeTask";
    public static final String CLEAN_AFTER_TASK = "CleanAfterComplete";

    private static final Logger LOGGER = Logger.getLoggerFor(DockerTask.class);

    public DockerTask() {
    }

    @Override
    protected GoPluginApiResponse handleTaskView(GoPluginApiRequest request) {
        return getViewResponse("Docker",
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
                .add(CLEAN_AFTER_TASK, "false", Required.No)

                // build
                .add(DOCKER_FILE_NAME, "", Required.No)
                .add(BUILD_ARGS, "", Required.No)
                .add(BUILD_NO_CACHE, "false", Required.No)
                .add(IMAGE_NAME, "", Required.No)

                // tag
                .add(IMAGE_TAG, "", Required.No)
                .add(IMAGE_TAG_POSTFIX, "", Required.No)
                .add(USERNAME, "", Required.No)

                // login
                .add(REGISTRY_URL_FOR_LOGIN, "", Required.No)
                .add(REGISTRY_USERNAME, "", Required.No)
                .add(REGISTRY_PASSWORD, "", Required.No, Secure.Yes)

                // run
                .add(RUN_IMAGE, "", Required.No)
                .add(RUN_ENV_VARS, "", Required.No)
//                .add(RUN_VOLUME, "", Required.No)
//                .add(RUN_VOLUMES_FROM, "", Required.No)
//                .add(RUN_WORKING_DIR, "", Required.No)
                .add(RUN_ARGS, "", Required.No)

                //
                .toMap();
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return new GoPluginIdentifier("task", Arrays.asList("1.0"));
    }
}
