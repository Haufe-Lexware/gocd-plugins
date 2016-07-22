package com.tw.go.plugin.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.AbstractGoPlugin;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thomassc on 21.04.16.
 */
public abstract class BaseGoPlugin extends AbstractGoPlugin {

    protected Gson gson = new GsonBuilder().serializeNulls().create();
    private static final Logger logger = Logger.getLoggerFor(BaseGoPlugin.class);

    protected GoPluginApiResponse success(Object response) {
        return renderJSON(DefaultGoApiResponse.SUCCESS_RESPONSE_CODE, null, response);
    }

    protected GoPluginApiResponse internalError(Object response) {
        return renderJSON(DefaultGoApiResponse.INTERNAL_ERROR, null, response);
    }

    protected GoPluginApiResponse validationError(Object response) {
        return renderJSON(DefaultGoApiResponse.VALIDATION_ERROR, null, response);
    }

    protected GoPluginApiResponse renderJSON(final int responseCode, Object response) {
        return renderJSON(responseCode, null, response);
    }

    protected GoPluginApiResponse getViewResponse(String displayValue, InputStream template) {
        HashMap result = new HashMap();
        result.put("displayValue", displayValue);
        try {
            result.put("template", IOUtils.toString(template, "UTF-8"));
            return success(result);
        } catch (Exception e) {
            String errorMessage = "Failed to find template: " + e.getMessage();
            result.put("exception", errorMessage);
            return internalError(result);
        }
    }

    // https://developer.go.cd/current/writing_go_plugins/task/version_1_0/configuration.html
    abstract protected GoPluginApiResponse handleGetConfigRequest(GoPluginApiRequest request);

    // https://developer.go.cd/current/writing_go_plugins/task/version_1_0/validate.html
    abstract protected GoPluginApiResponse handleValidation(GoPluginApiRequest request);

    // https://developer.go.cd/current/writing_go_plugins/task/version_1_0/execute.html
    abstract protected GoPluginApiResponse handleTaskExecution(GoPluginApiRequest request) throws Exception;

    // https://developer.go.cd/current/writing_go_plugins/task/version_1_0/view.html
    abstract protected GoPluginApiResponse handleTaskView(GoPluginApiRequest request);

    // https://developer.go.cd/current/writing_go_plugins/json_message_based_plugin_api.html
    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) throws UnhandledRequestTypeException {
        try {
            switch (request.requestName()) {
                case "configuration":
                case "go.plugin-settings.get-configuration":
                    return handleGetConfigRequest(request);
                case "validate":
                case "go.plugin-settings.validate-configuration":
                    return handleValidation(request);
                case "execute":
                    return handleTaskExecution(request);
                case "view":
                case "go.plugin-settings.get-view":
                    return handleTaskView(request);
                default:
                    return DefaultGoPluginApiResponse.badRequest(String.format("Invalid request name %s", request.requestName()));
            }
        } catch (Throwable e) {
            return DefaultGoPluginApiResponse.error(e.getMessage());
        }
    }

    protected GoPluginApiResponse renderJSON(final int responseCode, final Map<String, String> responseHeaders, Object response) {
        final String json = response == null ? null : gson.toJson(response);
        logger.debug(json);
        return new GoPluginApiResponse() {
            @Override
            public int responseCode() {
                return responseCode;
            }

            @Override
            public Map<String, String> responseHeaders() {
                return responseHeaders;
            }

            @Override
            public String responseBody() {
                return json;
            }
        };
    }
}