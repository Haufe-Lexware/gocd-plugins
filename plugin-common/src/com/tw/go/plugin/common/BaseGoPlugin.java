package com.tw.go.plugin.common;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.AbstractGoPlugin;
import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;

import java.util.Map;

public abstract class BaseGoPlugin extends AbstractGoPlugin
{
    protected GoPluginApiResponse success(Object response)
    {
        return renderJSON(DefaultGoApiResponse.SUCCESS_RESPONSE_CODE, null, response);
    }

    protected GoPluginApiResponse internalError(Object response)
    {
        return renderJSON(DefaultGoApiResponse.INTERNAL_ERROR, null, response);
    }

    protected GoPluginApiResponse validationError(Object response)
    {
        return renderJSON(DefaultGoApiResponse.VALIDATION_ERROR, null, response);
    }

    protected GoPluginApiResponse renderJSON(final int responseCode, Object response)
    {
        return renderJSON(responseCode, null, response);
    }

    // https://developer.go.cd/current/writing_go_plugins/task/version_1_0/configuration.html
    abstract protected GoPluginApiResponse handleGetConfigRequest(GoPluginApiRequest request);

    // https://developer.go.cd/current/writing_go_plugins/task/version_1_0/validate.html
    abstract protected GoPluginApiResponse handleValidation(GoPluginApiRequest request);

    // https://developer.go.cd/current/writing_go_plugins/task/version_1_0/execute.html
    abstract protected GoPluginApiResponse handleTaskExecution(GoPluginApiRequest request);

    // https://developer.go.cd/current/writing_go_plugins/task/version_1_0/view.html
    abstract protected GoPluginApiResponse handleTaskView(GoPluginApiRequest request);

    // https://developer.go.cd/current/writing_go_plugins/json_message_based_plugin_api.html
    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) throws UnhandledRequestTypeException
    {
        switch (request.requestName())
        {
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
                throw new UnhandledRequestTypeException(request.requestName());
        }
    }

    protected GoPluginApiResponse renderJSON(final int responseCode, final Map<String, String> responseHeaders, Object response)
    {
        final String json = response == null ? null : new GsonBuilder().serializeNulls().create().toJson(response);

        return new GoPluginApiResponse()
        {
            @Override
            public int responseCode()
            {
                return responseCode;
            }

            @Override
            public Map<String, String> responseHeaders()
            {
                return responseHeaders;
            }

            @Override
            public String responseBody()
            {
                return json;
            }
        };
    }
}