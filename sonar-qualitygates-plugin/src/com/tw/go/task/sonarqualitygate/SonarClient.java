package com.tw.go.task.sonarqualitygate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.security.GeneralSecurityException;
import com.tw.go.plugin.common.ApiRequestBase;


/**
 * Created by MarkusW on 20.10.2015.
 */
public class SonarClient extends ApiRequestBase {

    public SonarClient(String apiUrl) throws GeneralSecurityException
    {
        super(apiUrl, "", "", true);
    }

    public JSONObject getProjectWithQualityGateDetails(String projectKey) throws Exception
    {
        String uri = getApiUrl() + "/qualitygates/project_status?projectKey=%1$s";
        uri = String.format(uri, projectKey);
        String resultData = requestGet(uri);

        JSONObject jsonObject = new JSONObject(resultData);

        return jsonObject;
    }
}
