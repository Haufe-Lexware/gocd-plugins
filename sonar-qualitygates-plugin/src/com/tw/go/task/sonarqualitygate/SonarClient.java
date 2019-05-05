package com.tw.go.task.sonarqualitygate;

import java.security.GeneralSecurityException;

import org.json.JSONObject;

import com.tw.go.plugin.common.ApiRequestBase;
import com.tw.go.task.sonarqualitygate.enumeration.EndpointSonarEnum;

/**
 * Created by MarkusW on 20.10.2015.
 */
public class SonarClient extends ApiRequestBase {

	public SonarClient(String apiUrl) throws GeneralSecurityException {
		super(apiUrl, "", "", true);
	}

	public JSONObject getProjectWithQualityGateDetails(String projectKey) throws Exception {
		String uri = getApiUrl() + EndpointSonarEnum.URI_SONAR_QUALITYGATES_PROJECT_STATUS.getUri();
		uri = String.format(uri, projectKey);
		String resultData = requestGet(uri);
		JSONObject jsonObject = new JSONObject(resultData);
		return jsonObject;
	}

	public String getSonarVersion() throws Exception {
		String uri = getApiUrl() + EndpointSonarEnum.URI_SONAR_VERSION.getUri();
		return requestGet(uri);
	}

	public String getLastAnalisysDate(String projectKey, String sonarVersion) throws Exception {
		String uri = getApiUrl() + EndpointSonarEnum.getUriAnalisysDate(sonarVersion);
		uri = String.format(uri, projectKey);
		return requestGet(uri);

	} 
}
