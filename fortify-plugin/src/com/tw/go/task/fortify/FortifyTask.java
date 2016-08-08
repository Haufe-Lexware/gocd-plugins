package com.tw.go.task.fortify;

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
public class FortifyTask extends BaseGoPlugin
{
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String FORTIFY_URL = "fortifyurl";
    public static final String SSC_PROJECT = "sscproject";
    public static final String SSC_VERSION = "sscversion";

    @Override
    protected GoPluginApiResponse handleTaskView(GoPluginApiRequest request)
    {
        return getViewResponse("Fortify",
                getClass().getResourceAsStream("/views/task.template.html"));
    }

    // https://developer.go.cd/current/writing_go_plugins/task/version_1_0/execute.html
    @Override
    protected GoPluginApiResponse handleTaskExecution(GoPluginApiRequest request)
    {
        Map executionRequest = (Map) new GsonBuilder().create().fromJson(request.requestBody(), Object.class);

        Map<String, Map> config = (Map) executionRequest.get("config");

        fixConfigAttrs(config,getConfigDef());

        Context context = new Context((Map) executionRequest.get("context"));

        Map<String, String> envVars = context.getEnvironmentVariables();

        FortifyTaskExecutor executor = new FortifyTaskExecutor(
                MaskingJobConsoleLogger.getConsoleLogger(), context, config);

        // The RESPONSE_CODE MUST be 200 (SUCCESS) to be processed by Go.CD
        // The outcome is defined by the property "success", and the cause can be stored in "message"
        // See
        //  public <T> T submitRequest(String pluginId, String requestName, PluginInteractionCallback<T> pluginInteractionCallback) {
        // in
        //  .../gocd/plugin-infra/go-plugin-access/src/com/thoughtworks/go/plugin/access/PluginRequestHelper.java

        try
        {
            Result result = executor.execute();
            return success(result.toMap());
        }
        catch (Exception e)
        {
            Result result = new Result(false,e.getMessage());
            return success(result.toMap());
        }

    }

    @Override
    protected GoPluginApiResponse handleValidation(GoPluginApiRequest request)
    {
        HashMap result = new HashMap();
        return success(result);
    }

    @Override
    protected GoPluginApiResponse handleGetConfigRequest(GoPluginApiRequest request)
    {
        return success(getConfigDef());
    }

    private void fixConfigAttrs(Map<String, Map> configAct, Map<String, Map> configDef)
    {
        for (Map.Entry<String,Map> e : configAct.entrySet())
        {
            Map def = configDef.get(e.getKey());
            Map act = e.getValue();

            act.put("required",def.get("required"));
            act.put("secure",def.get("secure"));
        }
    }

    private Map getConfigDef()
    {
        return new ConfigDef()
                .add(USERNAME, "", Required.NO)
                .add(PASSWORD, "", Required.NO, Secure.YES)
                .add(FORTIFY_URL, "", Required.YES)
                .add(SSC_PROJECT, "", Required.YES)
                .add(SSC_VERSION, "", Required.YES)
                .toMap();
    }

    @Override
    public GoPluginIdentifier pluginIdentifier()
    {
        return new GoPluginIdentifier("task", Arrays.asList("1.0"));
    }
}

