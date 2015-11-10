
package com.tw.go.task.nessusscan;

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
import com.thoughtworks.go.plugin.api.task.*;
import com.tw.go.plugin.common.Context;
import com.tw.go.plugin.common.Result;
import org.apache.commons.io.IOUtils;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Extension
public class NessusScanTask implements GoPlugin {

    public static final String SERVER_IP = "ServerIp";
    public static final String POLICY = "Policy";
    public static final String SCANTEMPLATE = "ScanTemplate";
    public static final String ISSUE_TYPE_FAIL = "IssueTypeFail";
    public static final String NESSUS_API_URL = "NessusApiUrl";
    public static final String NESSUS_API_ACCESS_KEY = "NessusApiAccessKey";
    public static final String NESSUS_API_SECRET_KEY = "NessusApiAccessSecret";
    private Logger logger = Logger.getLoggerFor(NessusScanTask.class);

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) throws UnhandledRequestTypeException {
        if ("configuration".equals(request.requestName())) {
            return handleGetConfigRequest();
        } else if ("validate".equals(request.requestName())) {
            return handleValidation(request);
        } else if ("execute".equals(request.requestName())) {
            return handleTaskExecution(request);
        } else if ("view".equals(request.requestName())) {
            return handleTaskView();
        }
        throw new UnhandledRequestTypeException(request.requestName());
    }

    private GoPluginApiResponse handleTaskView() {
        int responseCode = DefaultGoApiResponse.SUCCESS_RESPONSE_CODE;
        HashMap view = new HashMap();
        view.put("displayValue", "Security Scan - Nessus");
        try {
            view.put("template", IOUtils.toString(getClass().getResourceAsStream("/views/task.template.html"), "UTF-8"));
        } catch (Exception e) {
            responseCode = DefaultGoApiResponse.INTERNAL_ERROR;
            String errorMessage = "Failed to find template: " + e.getMessage();
            view.put("exception", errorMessage);
            logger.error(errorMessage, e);
        }
        return createResponse(responseCode, view);
    }

    private GoPluginApiResponse handleTaskExecution(GoPluginApiRequest request) {
        Map executionRequest = (Map) new GsonBuilder().create().fromJson(request.requestBody(), Object.class);
        Map config = (Map) executionRequest.get("config");
        Map context = (Map) executionRequest.get("context");

        NessusScanTaskExecutor executor = new NessusScanTaskExecutor(JobConsoleLogger.getConsoleLogger(), new Context(context), config );

        Result result = executor.execute();
        return createResponse(result.responseCode(), result.toMap());
    }

    private GoPluginApiResponse handleValidation(GoPluginApiRequest request) {
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
    private GoPluginApiResponse handleGetConfigRequest() {

        HashMap config = new HashMap();

        HashMap serverIp = new HashMap();
        serverIp.put("display-order", "0");
        serverIp.put("display-name", "Server Ip");
        serverIp.put("required", true);
        config.put(SERVER_IP, serverIp);

        HashMap policy = new HashMap();
        policy.put("display-order", "1");
        policy.put("display-name", "Policy");
        policy.put("required", true);
        config.put(POLICY, policy);

        HashMap scanTemplate = new HashMap();
        policy.put("display-order", "2");
        policy.put("display-name", "Scan Template Name");
        policy.put("required", true);
        config.put(SCANTEMPLATE, scanTemplate);

        HashMap issueTypeFail = new HashMap();
        issueTypeFail.put("default-value", "critical");
        issueTypeFail.put("display-order", "3");
        issueTypeFail.put("display-name", "Issue Type Fail");
        issueTypeFail.put("required", false);
        config.put(ISSUE_TYPE_FAIL, issueTypeFail);

        HashMap nessusApiUrl = new HashMap();
        nessusApiUrl.put("display-order", "4");
        nessusApiUrl.put("display-name", "Nessus Api Url");
        nessusApiUrl.put("required", true);
        config.put(NESSUS_API_URL, nessusApiUrl);

        HashMap nessusApiAccessKey = new HashMap();
        nessusApiAccessKey.put("display-order", "5");
        nessusApiAccessKey.put("display-name", "Nessus Api Access Key");
        nessusApiAccessKey.put("required", true);
        config.put(NESSUS_API_ACCESS_KEY, nessusApiAccessKey);

        HashMap nessusApiSecretKey = new HashMap();
        nessusApiSecretKey.put("display-order", "6");
        nessusApiSecretKey.put("display-name", "Nessus Api Secret Key");
        nessusApiSecretKey.put("required", true);
        config.put(NESSUS_API_SECRET_KEY, nessusApiSecretKey);

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
