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
    public static final String FORCE_PULL = "FORCE_PULL";
    public static final String FORCE_BUILD_ONLY = "FORCE_BUILD_ONLY";

    @Override
    protected GoPluginApiResponse handleGetConfigRequest(GoPluginApiRequest request) {
        return success(new ConfigDef()
                .add(VMNAME, "", ConfigDef.Required.Yes)
                .add(SERVICE, "", ConfigDef.Required.No)
                .add(ENV_VARS, "", ConfigDef.Required.No)
                .add(COMPOSE_BUILD, "", ConfigDef.Required.No)
                .add(COMPOSE_FILE, "", ConfigDef.Required.No)
                .add(FORCE_RECREATE, "false", ConfigDef.Required.No)
                .add(FORCE_BUILD, "false", ConfigDef.Required.No)
                .add(COMPOSE_NO_CACHE, "false", ConfigDef.Required.No)
                .add(COMPOSE_REMOVE_VOLUMES, "false", ConfigDef.Required.No)
                .add(FORCE_PULL, "false", ConfigDef.Required.No)
                .add(FORCE_BUILD_ONLY, "false", ConfigDef.Required.No)
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