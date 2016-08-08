package com.tw.go.task.sonarqualitygate;

import org.json.JSONArray;
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

    public JSONObject GetQualityGateDetails()
    {
        if (project.has("msr")) {
            JSONArray msrList = project.getJSONArray("msr");

            for (int i = 0; i < msrList.length(); i++)
            {
                JSONObject msr = (JSONObject) msrList.get(i);
                String key = msr.getString("key");

                if("quality_gate_details".equals(key))
                {
                    String data = msr.getString("data");
                    //data = data.replace("\\", "");
                    JSONObject resultObj = new JSONObject(data);
                    return resultObj;
                }

            }


        }
        return null;
    }


}
