package com.tw.go.task.sonarqualitygate;

import com.google.gson.Gson;
import com.tw.go.task.sonarqualitygate.model.Sonar;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.GeneralSecurityException;

import com.tw.go.plugin.common.ApiRequestBase;


/**
 * Created by MarkusW on 20.10.2015.
 */
public class SonarClient extends ApiRequestBase {
    public SonarClient(String apiUrl) throws GeneralSecurityException {
        super(apiUrl, "", "", true);
    }

    @Deprecated
    public JSONObject getProjectWithQualityGateDetails(String projectKey) throws Exception {
        String uri = getApiUrl() + "/resources?resource=%1$s&metrics=quality_gate_details";
        uri = String.format(uri, projectKey);
        String resultData = requestGet(uri);

        JSONArray jsonArray = new JSONArray(resultData);
        JSONObject jsonObject = jsonArray.getJSONObject(0);

        return jsonObject;
    }

    /**
     * check the quality of the project at sonar by project key
     *
     * @param projectKey - projectKey from project
     * @return sonar dto with the result of quality
     * @throws Exception
     */
    public Sonar getSonarQualityStatus(final String projectKey) throws Exception {
        String uri = getApiUrl() + "/qualitygates/project_status?projectKey=" + projectKey;
        String resultData = requestGet(uri);

        final Gson gson = new Gson();
        final Sonar sonar = gson.fromJson(resultData, Sonar.class);

        return sonar;
    }
}
