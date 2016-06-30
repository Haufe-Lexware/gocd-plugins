
package com.tw.go.task.fetchanyartifact;

import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.tw.go.plugin.common.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Extension
public class FetchAnyArtifactTask extends BaseGoPlugin {

    public final static String FAA_PIPELINE_NAME = "FAA_PIPELINE_NAME";
    public final static String FAA_ARTIFACT_SOURCE = "FAA_ARTIFACT_SOURCE";
    public final static String FAA_ARTIFACT_DESTINATION = "FAA_ARTIFACT_DESTINATION";
    public final static String FAA_STAGE_NAME = "FAA_STAGE_NAME";
    public final static String FAA_JOB_NAME = "FAA_JOB_NAME";
    public final static String FAA_ARTIFACT_IS_FILE="FAA_ARTIFACT_IS_FILE";
    public final static String FAA_FETCH_IF_FAILED="FAA_FETCH_IF_FAILED";

    @Override
    protected GoPluginApiResponse handleTaskView(GoPluginApiRequest request) {
        return getViewResponse("Fetch Any Artifact",
                getClass().getResourceAsStream("/views/task.template.html"));
    }

    @Override
    protected GoPluginApiResponse handleValidation(GoPluginApiRequest request) {
        HashMap result = new HashMap();
        Map config = (Map) gson.fromJson(request.requestBody(), Object.class);
        return success(result);
    }

    @Override
    protected GoPluginApiResponse handleTaskExecution(GoPluginApiRequest request) {
        Map executionRequest = (Map) gson.fromJson(request.requestBody(), Object.class);

        Map<String, Map> config = (Map) executionRequest.get("config");

        Context context = new Context((Map) executionRequest.get("context"));

        try {
            Result result = new FetchAnyArtifactTaskExecutor(
                    MaskingJobConsoleLogger.getConsoleLogger(), context, config).execute();
            return success(result.toMap());
        } catch (Exception e) {
            Result result = new Result(false,e.getMessage());
            return success(result.toMap());
        }

    }
    @Override
    protected GoPluginApiResponse handleGetConfigRequest(GoPluginApiRequest request) {
        return success(new ConfigDef()
                .add("FAA_PIPELINE_NAME", "", ConfigDef.Required.No)
                .add("FAA_ARTIFACT_SOURCE", "", ConfigDef.Required.Yes)
                .add("FAA_ARTIFACT_DESTINATION", "", ConfigDef.Required.No)
                .add("FAA_STAGE_NAME", "", ConfigDef.Required.No)
                .add("FAA_JOB_NAME", "", ConfigDef.Required.No)
                .add("FAA_FETCH_IF_FAILED", "", ConfigDef.Required.No)
                .toMap());
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return new GoPluginIdentifier("task", Arrays.asList("1.0"));
    }
}