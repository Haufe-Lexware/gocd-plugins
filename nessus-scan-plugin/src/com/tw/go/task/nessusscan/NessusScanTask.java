
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
import com.tw.go.plugin.common.GoApiConstants;
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
        // not required in most plugins
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) throws UnhandledRequestTypeException {
        if ("configuration".equals(request.requestName())) {
            return handleGetConfigRequest();
        } else if ("validate".equals(request.requestName())) {
            return handleValidation();
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

    private GoPluginApiResponse handleValidation() {
        HashMap validationResult = new HashMap();
        int responseCode = DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE;

        return createResponse(responseCode, validationResult);
    }

    // return json description to tell go.cd which config properties need to be stored
    // sample and schema can be found at http://www.go.cd/documentation/developer/writing_go_plugins/task/version_1_0/configuration.html
    private GoPluginApiResponse handleGetConfigRequest() {

        HashMap config = new HashMap();

        HashMap serverIp = new HashMap();
        serverIp.put(GoApiConstants.PROPERTY_NAME_DISPLAY_ORDER, "0");
        serverIp.put(GoApiConstants.PROPERTY_NAME_DISPLAY_NAME, "Server Ip");
        serverIp.put(GoApiConstants.PROPERTY_NAME_REQUIRED, true);
        config.put(SERVER_IP, serverIp);

        HashMap policy = new HashMap();
        policy.put(GoApiConstants.PROPERTY_NAME_DISPLAY_ORDER, "1");
        policy.put(GoApiConstants.PROPERTY_NAME_DISPLAY_NAME, "Policy");
        policy.put(GoApiConstants.PROPERTY_NAME_REQUIRED, true);
        config.put(POLICY, policy);

        HashMap scanTemplate = new HashMap();
        policy.put(GoApiConstants.PROPERTY_NAME_DISPLAY_ORDER, "2");
        policy.put(GoApiConstants.PROPERTY_NAME_DISPLAY_NAME, "Scan Template Name");
        policy.put(GoApiConstants.PROPERTY_NAME_REQUIRED, true);
        config.put(SCANTEMPLATE, scanTemplate);

        HashMap issueTypeFail = new HashMap();
        issueTypeFail.put(GoApiConstants.PROPERTY_NAME_DEFAULT_VALUE, "critical");
        issueTypeFail.put(GoApiConstants.PROPERTY_NAME_DISPLAY_ORDER, "3");
        issueTypeFail.put(GoApiConstants.PROPERTY_NAME_DISPLAY_NAME, "Issue Type Fail");
        issueTypeFail.put(GoApiConstants.PROPERTY_NAME_REQUIRED, false);
        config.put(ISSUE_TYPE_FAIL, issueTypeFail);

        HashMap nessusApiUrl = new HashMap();
        nessusApiUrl.put(GoApiConstants.PROPERTY_NAME_DISPLAY_ORDER, "4");
        nessusApiUrl.put(GoApiConstants.PROPERTY_NAME_DISPLAY_NAME, "Nessus Api Url");
        nessusApiUrl.put(GoApiConstants.PROPERTY_NAME_REQUIRED, true);
        config.put(NESSUS_API_URL, nessusApiUrl);

        HashMap nessusApiAccessKey = new HashMap();
        nessusApiAccessKey.put(GoApiConstants.PROPERTY_NAME_DISPLAY_ORDER, "5");
        nessusApiAccessKey.put(GoApiConstants.PROPERTY_NAME_DISPLAY_NAME, "Nessus Api Access Key");
        nessusApiAccessKey.put(GoApiConstants.PROPERTY_NAME_REQUIRED, true);
        config.put(NESSUS_API_ACCESS_KEY, nessusApiAccessKey);

        HashMap nessusApiSecretKey = new HashMap();
        nessusApiSecretKey.put(GoApiConstants.PROPERTY_NAME_DISPLAY_ORDER, "6");
        nessusApiSecretKey.put(GoApiConstants.PROPERTY_NAME_DISPLAY_NAME, "Nessus Api Secret Key");
        nessusApiSecretKey.put(GoApiConstants.PROPERTY_NAME_REQUIRED, true);
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
