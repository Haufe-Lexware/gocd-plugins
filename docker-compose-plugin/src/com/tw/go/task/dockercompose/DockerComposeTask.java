package com.tw.go.task.dockercompose;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.tw.go.plugin.common.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Extension
public class DockerComposeTask extends BaseGoPlugin {

    public static final String VMNAME = "vmname";
    public static final String SERVICE = "service";
    public static final String COMPOSE_BUILD = "COMPOSE_BUILD";
    public static final String COMPOSE_FILE = "COMPOSE_FILE";
    public static final String ENV_VARS = "ENV_VARS";
    public static final String FORCE_RECREATE = "FORCE_RECREATE";
    public static final String FORCE_BUILD = "FORCE_BUILD";
    public static final String COMPOSE_NO_CACHE = "COMPOSE_NO_CACHE";
    public static final String COMPOSE_REMOVE_VOLUMES = "COMPOSE_REMOVE_VOLUMES";
    public static final String COMPOSE_DOWN = "COMPOSE_DOWN";
    public static final String FORCE_PULL = "FORCE_PULL";
    public static final String FORCE_BUILD_ONLY = "FORCE_BUILD_ONLY";
    public static final String BUNDLE_OUTPUT_PATH = "BUNDLE_OUTPUT_PATH";

    @Override
    protected GoPluginApiResponse handleGetConfigRequest(GoPluginApiRequest request) {
        return success(new ConfigDef()
                .add(VMNAME, "", Required.YES)
                .add(SERVICE, "", Required.NO)
                .add(ENV_VARS, "", Required.NO)
                .add(COMPOSE_BUILD, "", Required.NO)
                .add(COMPOSE_FILE, "", Required.NO)
                .add(FORCE_RECREATE, "false", Required.NO)
                .add(FORCE_BUILD, "false", Required.NO)
                .add(COMPOSE_NO_CACHE, "false", Required.NO)
                .add(COMPOSE_REMOVE_VOLUMES, "false", Required.NO)
                .add(COMPOSE_DOWN, "false", Required.NO)
                .add(FORCE_PULL, "false", Required.NO)
                .add(FORCE_BUILD_ONLY, "false", Required.NO)
                .add(BUNDLE_OUTPUT_PATH, "", Required.NO)
                .toMap());
    }

    @Override
    protected GoPluginApiResponse handleValidation(GoPluginApiRequest request) {
        return success(new HashMap<String, String>());
    }

    @Override
    protected GoPluginApiResponse handleTaskExecution(GoPluginApiRequest request) throws Exception {
        Map executionRequest = (Map) new GsonBuilder().create().fromJson(request.requestBody(), Object.class);

        Map<String, Map> config = (Map) executionRequest.get("config");

        Context context = new Context((Map) executionRequest.get("context"));

        TaskExecutor executor = new DockerComposeTaskExecutor(MaskingJobConsoleLogger.getConsoleLogger(), context, config);
        return success(executor.execute());
    }

    @Override
    protected GoPluginApiResponse handleTaskView(GoPluginApiRequest request) {
        return getViewResponse("Docker Compose",
                getClass().getResourceAsStream("/views/task.template.html"));
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return new GoPluginIdentifier("task", Arrays.asList("1.0"));
    }
}