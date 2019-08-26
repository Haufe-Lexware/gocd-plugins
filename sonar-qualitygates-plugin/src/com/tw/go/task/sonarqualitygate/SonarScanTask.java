
package com.tw.go.task.sonarqualitygate;

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
import com.tw.go.plugin.common.Context;
import com.tw.go.plugin.common.GoApiConstants;
import com.tw.go.plugin.common.MaskingJobConsoleLogger;
import com.tw.go.plugin.common.Result;
import org.apache.commons.io.IOUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE;

@Extension
public class SonarScanTask implements GoPlugin {

    public static final String ISSUE_TYPE_FAIL = "IssueTypeFail";
    public static final String SONAR_API_URL = "SonarApiUrl";
    public static final String SONAR_PROJECT_KEY = "SonarProjectKey";
    public static final String STAGE_NAME = "StageName";
    public static final String JOB_NAME = "JobName";
    public static final String JOB_COUNTER = "JobCounter";

    private static final Logger LOGGER = Logger.getLoggerFor(SonarScanTask.class);

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
        // not required in most plugins
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) throws UnhandledRequestTypeException {
        switch (request.requestName()) {
            case "configuration":
            case "go.plugin-settings.get-configuration":
                return handleGetConfigRequest();
            case "validate":
            case "go.plugin-settings.validate-configuration":
                return handleValidation();
            case "execute":
                return handleTaskExecution(request);
            case "view":
            case "go.plugin-settings.get-view":
                return handleTaskView();
            default:
                throw new UnhandledRequestTypeException(request.requestName());
        }
    }

    private GoPluginApiResponse handleTaskView() {
        int responseCode = DefaultGoApiResponse.SUCCESS_RESPONSE_CODE;
        HashMap view = new HashMap();
        view.put("displayValue", "SonarQube - Quality Gate");
        try {
            view.put("template", IOUtils.toString(getClass().getResourceAsStream("/views/task.template.html"), "UTF-8"));
        } catch (Exception e) {
            responseCode = DefaultGoApiResponse.INTERNAL_ERROR;
            String errorMessage = "Failed to find template: " + e.getMessage();
            view.put("exception", errorMessage);
            LOGGER.error(errorMessage, e);
        }
        return createResponse(responseCode, view);
    }

    private GoPluginApiResponse handleTaskExecution(GoPluginApiRequest request) {
        Map executionRequest = (Map) new GsonBuilder().create().fromJson(request.requestBody(), Object.class);
        Map config = (Map) executionRequest.get("config");
        Map context = (Map) executionRequest.get("context");

        SonarTaskExecutor executor = new SonarTaskExecutor(MaskingJobConsoleLogger.getConsoleLogger(), new Context(context), config);

        try {
            Result result = executor.execute();
            return createResponse(result.responseCode(), result.toMap());
        } catch (Exception e) {
            Result result = new Result(false,e.getMessage());
            return createResponse(SUCCESS_RESPONSE_CODE, result.toMap());
        }
    }

    private GoPluginApiResponse handleValidation() {
        HashMap validationResult = new HashMap();
        int responseCode = SUCCESS_RESPONSE_CODE;
        return createResponse(responseCode, validationResult);
    }

    // return json description to tell go.cd which config properties need to be stored
    // sample and schema can be found at http://www.go.cd/documentation/developer/writing_go_plugins/task/version_1_0/configuration.html
    private GoPluginApiResponse handleGetConfigRequest() {

        HashMap config = new HashMap();

        HashMap stageName = new HashMap();
        stageName.put(GoApiConstants.PROPERTY_NAME_DISPLAY_ORDER, "1");
        stageName.put(GoApiConstants.PROPERTY_NAME_DISPLAY_NAME, "Stage name");
        stageName.put(GoApiConstants.PROPERTY_NAME_REQUIRED, false);
        config.put(STAGE_NAME, stageName);

        HashMap jobName = new HashMap();
        jobName.put(GoApiConstants.PROPERTY_NAME_DISPLAY_ORDER, "2");
        jobName.put(GoApiConstants.PROPERTY_NAME_DISPLAY_NAME, "Job name");
        jobName.put(GoApiConstants.PROPERTY_NAME_REQUIRED, false);
        config.put(JOB_NAME, jobName);

        HashMap stageCounter = new HashMap();
        stageCounter.put(GoApiConstants.PROPERTY_NAME_DEFAULT_VALUE, "1");
        stageCounter.put(GoApiConstants.PROPERTY_NAME_DISPLAY_ORDER, "3");
        stageCounter.put(GoApiConstants.PROPERTY_NAME_DISPLAY_NAME, "Stage counter");
        stageCounter.put(GoApiConstants.PROPERTY_NAME_REQUIRED, false);
        config.put(JOB_COUNTER, stageCounter);

        HashMap sonarProjectKey = new HashMap();
        sonarProjectKey.put(GoApiConstants.PROPERTY_NAME_DISPLAY_ORDER, "4");
        sonarProjectKey.put(GoApiConstants.PROPERTY_NAME_DISPLAY_NAME, "Key of the SonarQube project");
        sonarProjectKey.put(GoApiConstants.PROPERTY_NAME_REQUIRED, false);
        config.put(SONAR_PROJECT_KEY, sonarProjectKey);

        HashMap issueTypeFail = new HashMap();
        issueTypeFail.put(GoApiConstants.PROPERTY_NAME_DEFAULT_VALUE, "error");
        issueTypeFail.put(GoApiConstants.PROPERTY_NAME_DISPLAY_ORDER, "5");
        issueTypeFail.put(GoApiConstants.PROPERTY_NAME_DISPLAY_NAME, "Fail Quality Gate result");
        issueTypeFail.put(GoApiConstants.PROPERTY_NAME_REQUIRED, false);
        config.put(ISSUE_TYPE_FAIL, issueTypeFail);

        HashMap sonarApiUrl = new HashMap();
        sonarApiUrl.put(GoApiConstants.PROPERTY_NAME_DISPLAY_ORDER, "6");
        sonarApiUrl.put(GoApiConstants.PROPERTY_NAME_DISPLAY_NAME, "Sonar Api Url");
        sonarApiUrl.put(GoApiConstants.PROPERTY_NAME_REQUIRED, true);
        config.put(SONAR_API_URL, sonarApiUrl);

        return createResponse(SUCCESS_RESPONSE_CODE, config);
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
