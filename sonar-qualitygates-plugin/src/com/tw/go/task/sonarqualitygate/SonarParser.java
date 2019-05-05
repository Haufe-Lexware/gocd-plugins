package com.tw.go.task.sonarqualitygate;

import org.json.JSONArray;
import org.json.JSONObject;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.tw.go.task.sonarqualitygate.enumeration.EndpointSonarEnum;

/**
 * Created by MarkusW on 22.10.2015.
 */
public class SonarParser {
	private static final Logger LOG = Logger.getLoggerFor(SonarParser.class);
	private static final String TOKEN_DATETIME = "dt";
	private static final String TOKEN_PROJECT_VERSION = "n";
	private static final String TOKEN_PROJECT_VERSION_SINCE_63 = "projectVersion";

	private static final String TOKEN_ANALYSES = "analyses";
	private static final String TOKEN_DATE = "date";
	private JSONObject jsonObject;
	private JSONArray jsonArray;
	private String sonarVersion;
	private static SonarParser instance;

	static {
		try {
			instance = new SonarParser();
		} catch (Exception e) {
			throw new RuntimeException("Exception occured in creating singleton instance");
		}
	}

	private SonarParser() {
		super();
	}

	public static final SonarParser getInstance(String jsonObject, String sonarVersion) {
		if (jsonObject == null || jsonObject.trim().isEmpty()) {
			throw new RuntimeException(
					"The response of the SonarQube is empty or null. Please contact with the Administrator of the project.");
		}
		if (EndpointSonarEnum.isSonarVersionUntil62(sonarVersion)) {
			instance.jsonArray = new JSONArray(jsonObject);
		} else {

			instance.jsonObject = new JSONObject(jsonObject);
		}

		instance.sonarVersion = sonarVersion;
		return instance;
	}

	public static final SonarParser getInstance(JSONObject jsonObject) {
		instance.jsonObject = jsonObject;
		instance.sonarVersion = null;
		return instance;
	}

	public String getProjectQualityGateStatus() {
		if (jsonObject.has("projectStatus")) {
			JSONObject projectStatus = jsonObject.getJSONObject("projectStatus");
			if (projectStatus.has("status")) {
				return projectStatus.getString("status");
			}
		}
		return null;
	}

	public String getLastAnalysisDate() {
		try {
			if (EndpointSonarEnum.isSonarVersionUntil62(sonarVersion)) {
				return getAnalysisDateUntil62();
			} else {
				return getAnalysisDateSince63();
			}
		} catch (Exception e) {
			LOG.error("Error read the last analysis date", e);
		}
		return null;
	}

	public String getProjectVersion() {
		try {
			if (EndpointSonarEnum.isSonarVersionUntil62(sonarVersion)) {
				return getProjectVersionUntil62();
			} else {
				return getProjectVersionSince63();
			}
		} catch (Exception e) {
			LOG.error("Error read the project version", e);
		}
		return null;
	}

	private String getAnalysisDateUntil62() {
		/*
		 * [{ "id": "2", "rk": "org.report:report4j", "n": "1.0.0-SNAPSHOT", "c":
		 * "Version", "dt": "2019-05-04T16:03:55-0500" } ]
		 */

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject object = (JSONObject) jsonArray.get(i);
			if (object.has(TOKEN_DATETIME) && "VERSION".equalsIgnoreCase(object.getString("c"))) {
				return object.getString(TOKEN_DATETIME);
			}
		}

		return null;
	}

	private String getProjectVersionUntil62() {
		/*
		 * [{ "id": "2", "rk": "org.report:report4j", "n": "1.0.0-SNAPSHOT", "c":
		 * "Version", "dt": "2019-05-04T16:03:55-0500" } ]
		 */
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject object = (JSONObject) jsonArray.get(i);
			if (object.has(TOKEN_PROJECT_VERSION) && "VERSION".equalsIgnoreCase(object.getString("c"))) {
				return object.getString(TOKEN_PROJECT_VERSION);
			}
		}

		return null;
	}

	private String getAnalysisDateSince63() {
		/*
		 * { "paging":{ "pageIndex":1, "pageSize":100, "total":1 }, "analyses":[ {
		 * "key":"AWqEon5uoFaYT6ac4sti", "date":"2019-05-04T15:56:30-0500", "events":[ {
		 * "key":"AWqEooVToFaYT6ac4szA", "category":"VERSION", "name":"1.0.0-SNAPSHOT" }
		 * ], "projectVersion":"1.0.0-SNAPSHOT", "manualNewCodePeriodBaseline":false } ]
		 * }
		 */
		JSONArray analyses = jsonObject.getJSONArray(TOKEN_ANALYSES);
		JSONObject object = (JSONObject) analyses.get(0);
		return object.getString(TOKEN_DATE);
	}

	private String getProjectVersionSince63() {
		/*
		 * { "paging":{ "pageIndex":1, "pageSize":100, "total":1 }, "analyses":[ {
		 * "key":"AWqEon5uoFaYT6ac4sti", "date":"2019-05-04T15:56:30-0500", "events":[ {
		 * "key":"AWqEooVToFaYT6ac4szA", "category":"VERSION", "name":"1.0.0-SNAPSHOT" }
		 * ], "projectVersion":"1.0.0-SNAPSHOT", "manualNewCodePeriodBaseline":false } ]
		 * }
		 */
		if (!jsonObject.has(TOKEN_ANALYSES)) {
			return null;
		}
		JSONArray analyses = jsonObject.getJSONArray(TOKEN_ANALYSES);
		JSONObject object = (JSONObject) analyses.get(0);
		if (!object.has(TOKEN_PROJECT_VERSION_SINCE_63)) {
			return null;
		}
		return object.getString(TOKEN_PROJECT_VERSION_SINCE_63);
	}
}