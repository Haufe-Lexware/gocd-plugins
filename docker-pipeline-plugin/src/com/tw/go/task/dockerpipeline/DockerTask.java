package com.tw.go.task.dockerpipeline;

import java.util.*;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.Context;
import com.tw.go.plugin.common.Result;
import org.apache.commons.io.IOUtils;
import com.tw.go.plugin.common.BaseGoPlugin;
import com.tw.go.plugin.common.ConfigDef;
import com.tw.go.plugin.common.ConfigDef.Required;
import com.tw.go.plugin.common.ConfigDef.Secure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Extension
public class DockerTask extends BaseGoPlugin
{
    public static final String CLEAN_BEFORE_TASK = "CleanBeforeTask";

    public static final String IMAGE_NAME = "ImageName";
    public static final String DOCKER_FILE_NAME = "DockerFileName";
    public static final String BUILD_ARGS = "BuildArgs";

    public static final String USERNAME = "Username";
    public static final String IMAGE_TAG = "ImageTag";

    public static final String REGISTRY_USERNAME = "RegistryUsername";
    public static final String REGISTRY_PASSWORD = "RegistryPassword";

    public static final String REGISTRY_EMAIL = "RegistryEmail";
    public static final String REGISTRY_URL_FOR_LOGIN = "RegistryURLForLogin";

    public static final String CLEAN_AFTER_TASK = "CleanAfterTask";

    private static final Logger LOGGER = Logger.getLoggerFor(DockerTask.class);

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor)
    {
        //shouldn't be
        //implemented
    }

    @Override
    protected GoPluginApiResponse handleTaskView(GoPluginApiRequest request)
    {
        HashMap result = new HashMap();
        result.put("displayValue", "Docker pipeline plugin");

        try
        {
            result.put("template", IOUtils.toString(getClass().getResourceAsStream("/views/task.template.html"), "UTF-8"));
            return success(result);
        }
        catch (Exception e)
        {
            String errorMessage = "Failed to find template: " + e.getMessage();
            result.put("exception", errorMessage);

            LOGGER.error(errorMessage, e);

            return internalError(result);
        }
    }

    @Override
    protected GoPluginApiResponse handleTaskExecution(GoPluginApiRequest request)
    {
        JobConsoleLogger console = JobConsoleLogger.getConsoleLogger();

        Map executionRequest = (Map) new GsonBuilder().create().fromJson(request.requestBody(), Object.class);
        Map<String, Map> config = (Map) executionRequest.get("config");
        Context context = new Context((Map) executionRequest.get("context"));
        Map<String, String> envVars = context.getEnvironmentVariables();
        DockerTaskExecutor executor = new DockerTaskExecutor(console, context, config);

        ArrayList<String> secure = getConfigSecureList();

        ConfigVars configVars = new ConfigVars(config, envVars, secure);

        Result result = executor.execute(configVars);

        // The RESPONSE_CODE MUST be 200 (SUCCESS) to be processed by Go.CD
        // The outcome is defined by the property "success", and the cause can be stored in "message"
        // See
        //  public <T> T submitRequest(String pluginId, String requestName, PluginInteractionCallback<T> pluginInteractionCallback) {
        // in
        //  .../gocd/plugin-infra/go-plugin-access/src/com/thoughtworks/go/plugin/access/PluginRequestHelper.java

        return success(result.toMap());
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

    private ArrayList getConfigSecureList()
    {
        ArrayList<String> secure = new ArrayList<>();
        Map<String, Map> configDef = getConfigDef();

        for (Map.Entry<String, Map> entry : configDef.entrySet())
        {
            Map cfg = entry.getValue();

            if ((boolean) cfg.get("secure"))
            {
                secure.add(entry.getKey());
            }
        }

        return secure;
    }

    private Map getConfigDef()
    {
        return new ConfigDef()
                // cleaning ...
                .add(CLEAN_AFTER_TASK, "true", Required.No)
                .add(CLEAN_BEFORE_TASK, "false", Required.No)

                // build
                .add(DOCKER_FILE_NAME, "", Required.No)
                .add(BUILD_ARGS, "", Required.No)
                .add(IMAGE_NAME, "", Required.Yes)

                // tag
                .add(IMAGE_TAG, "", Required.Yes)
                .add(USERNAME, "", Required.No)

                // login
                .add(REGISTRY_URL_FOR_LOGIN, "", Required.Yes)
                .add(REGISTRY_USERNAME, "", Required.No)
                .add(REGISTRY_PASSWORD, "", Required.No, Secure.Yes)
                .add(REGISTRY_EMAIL, "", Required.No)

                //
                .toMap();
    }

    @Override
    public GoPluginIdentifier pluginIdentifier()
    {
        return new GoPluginIdentifier("task", Arrays.asList("1.0"));
    }
}
