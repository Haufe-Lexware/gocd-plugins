package com.tw.go.task.sonarqualitygate;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by MarkusW on 22.10.2015.
 */
public class SonarParser {
    private JSONObject project;

    public SonarParser(JSONObject projectResult) {
        this.project = projectResult;
    }

    public String getProjectQualityGateStatus() {
        if (project.has("projectStatus")) {
            JSONObject projectStatus = project.getJSONObject("projectStatus");
            if (projectStatus.has("status")) {
                return projectStatus.getString("status");
            }
        }
        return null;
    }


    public String getDate() {
        if (project.has("projectStatus")) {
            JSONObject projectStatus = project.getJSONObject("projectStatus");
            if (projectStatus.has("periods")) {
                JSONArray periods = projectStatus.getJSONArray("periods");
                if (periods.length() > 0 && periods.getJSONObject(0).has("date")) {
                    return periods.getJSONObject(0).getString("date");
                }
            }
        }
        return null;
    }
}
