package com.tw.go.task.sonarqualitygate;

import org.json.JSONObject;

/**
 * Created by MarkusW on 22.10.2015.
 */
public class SonarParser
{
    private JSONObject project;

    public SonarParser(JSONObject projectResult){
        this.project = projectResult;
    }

    public String getProjectQualityGateStatus()
    {
        if (project.has("projectStatus")) {
            JSONObject projectStatus = project.getJSONObject("projectStatus");
            if (projectStatus.has("status")) {
                return projectStatus.getString("status");
            }
        }
        return null;
    }


}
