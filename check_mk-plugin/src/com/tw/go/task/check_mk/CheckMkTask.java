
package com.tw.go.task.check_mk;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.tw.go.plugin.common.*;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Extension
public class CheckMkTask extends BaseGoPlugin {

    public static final String CHECK_MK_SERVER = "Server";
    public static final String HOSTNAME = "Hostname";
    public static final String HOST_IP = "HostIp";
    public static final String FOLDER_PATH = "FolderPath";
    public static final String USERNAME = "Username";
    public static final String PASSWORD = "Password";
    public static final String ACTION = "SelectedAction";
    private Logger logger = Logger.getLoggerFor(CheckMkTask.class);

    @Override
    protected GoPluginApiResponse handleTaskView(GoPluginApiRequest request) {
        int responseCode = DefaultGoApiResponse.SUCCESS_RESPONSE_CODE;
        HashMap view = new HashMap();
        view.put("displayValue", "Monitoring - Check MK");
        try {
            String checkMkTemplate = IOUtils.toString(getClass().getResourceAsStream("/views/checkMk.template.html"), "UTF-8");
            view.put("template", checkMkTemplate);
        } catch (Exception e) {
            responseCode = DefaultGoApiResponse.INTERNAL_ERROR;
            String errorMessage = "Failed to find template: " + e.getMessage();
            view.put("exception", errorMessage);
            logger.error(errorMessage, e);
        }
        return createResponse(responseCode, view);
    }

    @Override
    protected GoPluginApiResponse handleTaskExecution(GoPluginApiRequest request) throws Exception {
        Map executionRequest = (Map) new GsonBuilder().create().fromJson(request.requestBody(), Object.class);

        Map<String, Map> config = (Map) executionRequest.get("config");

        Context context = new Context((Map) executionRequest.get("context"));

        try {
            CheckMkTaskExecutor checkMkTaskExecutor = TaskExecutorFactory.Create(MaskingJobConsoleLogger.getConsoleLogger(),
                    context, config);
            Result result = checkMkTaskExecutor.execute();
            return createResponse(result.responseCode(), result.toMap());
        } catch (JobNotSupportedException | IOException e) {
            return success(new Result(false, e.getMessage()));
        }
    }

    @Override
    protected GoPluginApiResponse handleValidation(GoPluginApiRequest request) {
        HashMap validationResult = new HashMap();
        int responseCode = DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE;
        Map configMap = (Map) new GsonBuilder().create().fromJson(request.requestBody(), Object.class);
        HashMap errorMap = new HashMap();
     /*   if (!configMap.containsKey(URL_PROPERTY) || ((Map) configMap.get(URL_PROPERTY)).get("value") == null || ((String) ((Map) configMap.get(URL_PROPERTY)).get("value")).trim().isEmpty()) {
            errorMap.put(URL_PROPERTY, "URL cannot be empty");
        }
        validationResult.put("errors", errorMap);
        */
        return createResponse(responseCode, validationResult);
    }

    // return json description to tell go.cd which config properties need to be stored
    // sample and schema can be found at http://www.go.cd/documentation/developer/writing_go_plugins/task/version_1_0/configuration.html
    @Override
    protected GoPluginApiResponse handleGetConfigRequest(GoPluginApiRequest request) {

        HashMap config = new HashMap();

        HashMap server = new HashMap();
        server.put("display-order", "0");
        server.put("display-name", "Server");
        server.put("required", true);
        config.put(CHECK_MK_SERVER, server);

        HashMap username = new HashMap();
        username.put("display-order", "1");
        username.put("display-name", "Username");
        username.put("required", true);
        config.put(USERNAME, username);

        HashMap password = new HashMap();
        password.put("display-order", "2");
        password.put("display-name", "Password");
        password.put("required", true);
        config.put(PASSWORD, password);

        HashMap action = new HashMap();
        action.put("display-order", "3");
        action.put("display-name", "Selected Action");
        action.put("required", true);
        config.put(ACTION, action);

        HashMap hostname = new HashMap();
        hostname.put("display-order", "4");
        hostname.put("display-name", "Host name");
        hostname.put("required", false);
        config.put(HOSTNAME, hostname);

        HashMap hostIp = new HashMap();
        hostIp.put("display-order", "5");
        hostIp.put("display-name", "Host Ip");
        hostIp.put("required", false);
        config.put(HOST_IP, hostIp);

        HashMap folderPath = new HashMap();
        folderPath.put("display-order", "6");
        folderPath.put("display-name", "Folder path");
        folderPath.put("required", false);
        config.put(FOLDER_PATH, folderPath);

        return createResponse(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE, config);
    }

    private GoPluginApiResponse createResponse(int responseCode, Map body) {
        final DefaultGoPluginApiResponse response = new DefaultGoPluginApiResponse(responseCode);
        response.setResponseBody(new GsonBuilder().serializeNulls().create().toJson(body));
        return response;
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return new GoPluginIdentifier("task", Arrays.asList("1.0"));
    }
}

