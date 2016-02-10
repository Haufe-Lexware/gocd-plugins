package com.tw.go.task.dockerpipeline;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.Context;
import com.tw.go.plugin.common.Result;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BradeaC on 14/12/2015.
 */
@Extension
public class DockerTask implements GoPlugin
{
    public static final String IS_DOCKER_CLEAN = "IsDockerClean";

    public static final String REGISTRY_URL = "RegistryURL";
    public static final String IMAGE_NAME = "ImageName";
    public static final String DOCKER_FILE_NAME = "DockerFileName";

    public static final String USERNAME = "Username";
    public static final String IMAGE_TAG1 = "ImageTag1";
    public static final String IMAGE_TAG2 = "ImageTag2";
    public static final String IMAGE_TAG3 = "ImageTag3";

    public static final String REGISTRY_USERNAME = "RegistryUsername";
    public static final String REGISTRY_PASSWORD = "RegistryPassword";
    public static final String REGISTRY_EMAIL = "RegistryEmail";
    public static final String REGISTRY_URL_FOR_LOGIN = "RegistryURLForLogin";

    Logger logger = Logger.getLoggerFor(DockerTask.class);

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor)
    {

    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) throws UnhandledRequestTypeException
    {
        if ("configuration".equals(request.requestName()))
        {
            return handleGetConfigRequest();
        }
        else if ("validate".equals(request.requestName()))
        {
            return handleValidation(request);
        }
        else if ("execute".equals(request.requestName()))
        {
            return handleTaskExecution(request);
        }
        else if ("view".equals(request.requestName()))
        {
            return handleTaskView();
        }
        throw new UnhandledRequestTypeException(request.requestName());
    }

    private GoPluginApiResponse handleTaskView()
    {
        int responseCode = DefaultGoApiResponse.SUCCESS_RESPONSE_CODE;

        HashMap view = new HashMap();
        view.put("displayValue", "Docker pipeline plugin");

        try
        {
            view.put("template", IOUtils.toString(getClass().getResourceAsStream("/views/task.template.html"), "UTF-8"));
        }
        catch (Exception e)
        {
            responseCode = DefaultGoApiResponse.INTERNAL_ERROR;
            String errorMessage = "Failed to find template: " + e.getMessage();
            view.put("exception", errorMessage);

            logger.error(errorMessage, e);
        }
        return createResponse(responseCode, view);
    }

    private GoPluginApiResponse handleTaskExecution(GoPluginApiRequest request)
    {
        Map executionRequest = (Map) new GsonBuilder().create().fromJson(request.requestBody(), Object.class);
        Map config = (Map) executionRequest.get("config");
        Map context = (Map) executionRequest.get("context");

        DockerTaskExecutor executor = new DockerTaskExecutor(JobConsoleLogger.getConsoleLogger(), new Context(context), config);

        Result result = executor.execute(new Config(config), new Context(context));

        return createResponse(result.responseCode(), result.toMap());
    }

    private GoPluginApiResponse handleValidation(GoPluginApiRequest request)
    {
        HashMap validationResult = new HashMap();
        int responseCode = DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE;

        return createResponse(responseCode, validationResult);
    }

    private GoPluginApiResponse createResponse(int responseCode, Map body)
    {
        final DefaultGoPluginApiResponse response = new DefaultGoPluginApiResponse(responseCode);
        response.setResponseBody(new GsonBuilder().serializeNulls().create().toJson(body));

        return response;
    }

    private GoPluginApiResponse handleGetConfigRequest()
    {
        HashMap config = new HashMap();

        addDockerCleanConfig(config);
        addDockerBuildConfig(config);
        addDockerTagConfig(config);
        addDockerLoginConfig(config);

        return createResponse(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE, config);
    }

    private void addDockerCleanConfig(HashMap config)
    {
        HashMap isDockerClean = new HashMap();
        isDockerClean.put("default-value", "true");
        isDockerClean.put("required", true);
        config.put(IS_DOCKER_CLEAN, isDockerClean);
    }

    private void addDockerBuildConfig(HashMap config)
    {
        HashMap registryURL = new HashMap();
        registryURL.put("default-value", "");
        registryURL.put("required", true);

        config.put(REGISTRY_URL, registryURL);


        HashMap imageName = new HashMap();
        imageName.put("default-value", "");
        imageName.put("required", true);

        config.put(IMAGE_NAME, imageName);


        HashMap dockerFileName = new HashMap();
        dockerFileName.put("default-value", "");
        dockerFileName.put("required", false);

        config.put(DOCKER_FILE_NAME, dockerFileName);
    }

    private void addDockerTagConfig(HashMap config)
    {
        HashMap username = new HashMap();
        username.put("default-value", "");
        username.put("required", true);

        config.put(USERNAME, username);


        HashMap imageTag1 = new HashMap();
        imageTag1.put("default-value", "");
        imageTag1.put("required", false);

        config.put(IMAGE_TAG1, imageTag1);

        HashMap imageTag2 = new HashMap();
        imageTag2.put("default-value", "");
        imageTag2.put("required", false);

        config.put(IMAGE_TAG2, imageTag2);

        HashMap imageTag3 = new HashMap();
        imageTag3.put("default-value", "");
        imageTag3.put("required", false);

        config.put(IMAGE_TAG3, imageTag3);
    }

    private void addDockerLoginConfig(HashMap config)
    {
        HashMap registryUsername = new HashMap();
        registryUsername.put("default-value", "");
        registryUsername.put("required", true);

        config.put(REGISTRY_USERNAME, registryUsername);


        HashMap registryPassword = new HashMap();
        registryPassword.put("default-value", "");
        registryPassword.put("required", true);

        config.put(REGISTRY_PASSWORD, registryPassword);


        HashMap registryEmail = new HashMap();
        registryEmail.put("default-value", "");
        registryEmail.put("required", true);

        config.put(REGISTRY_EMAIL, registryEmail);


        HashMap registryURLForLogin = new HashMap();
        registryURLForLogin.put("default-value", "");
        registryURLForLogin.put("required", true);

        config.put(REGISTRY_URL_FOR_LOGIN, registryURLForLogin);
    }

    @Override
    public GoPluginIdentifier pluginIdentifier()
    {
        return new GoPluginIdentifier("task", Arrays.asList("1.0"));
    }
}
