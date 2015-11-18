package com.tw.go.plugin.common;

import com.google.gson.JsonArray;
import org.json.JSONArray;
import org.json.CDL;
import org.json.JSONObject;

/**
 * Created by MarkusW on 17.11.2015.
 */
public class GoApiClient extends ApiRequestBase {


    public GoApiClient(String apiUrl) {
        super(apiUrl, "", "", true);
    }

    public String getJobProperty(String pipelineName, String pipelineCounter, String stageName, String stageCounter, String jobName, String propertyName) throws Exception {
        String uri = getJobPropertyRequestUri(pipelineName, pipelineCounter, stageName, stageCounter, jobName, propertyName);
        String resultCSV = requestGet(uri);

        // result is delivered in CSV. Only for this api function. We want to use JSON, so convert the stuff
        JSONArray array = CDL.toJSONArray(resultCSV);
        JSONObject obj = array.getJSONObject(0);

        // get the request property value
        String result = obj.getString(propertyName);

        return  result;
    }

    public String setJobProperty(String pipelineName, String pipelineCounter, String stageName, String stageCounter, String jobName, String propertyName, String propertyValue) throws Exception {
        String uri = getJobPropertyRequestUri(pipelineName, pipelineCounter, stageName, stageCounter, jobName, propertyName);
        int response = requestPostFormUrlEncodedValue(uri, propertyValue);
        if(response == 201){
            return propertyValue;
        }
        else if (response == 409) {
            return getJobProperty(pipelineName, pipelineCounter, stageName, stageCounter, jobName, propertyName);
        }

        throw new Exception("An error occured: Http response = " + response);
    }

    public JSONObject getJobProperties(String pipelineName, String pipelineCounter, String stageName, String stageCounter, String jobName) throws Exception {
        String uri = getJobPropertyRequestUri(pipelineName, pipelineCounter, stageName, stageCounter, jobName);
        String resultCSV = requestGet(uri);

        // result is delivered in CSV. Only for this api function. We want to use JSON, so convert the stuff
        JSONArray array = CDL.toJSONArray(resultCSV);
        return array.getJSONObject(0);
    }

    private String getJobPropertyRequestUri(String pipelineName, String pipelineCounter, String stageName, String stageCounter, String jobName, String propertyName){
        return getApiUrl() + "/properties/" + pipelineName + "/" + pipelineCounter + "/" + stageName + "/" + stageCounter + "/" + jobName + "/" + propertyName;
    }

    private String getJobPropertyRequestUri(String pipelineName, String pipelineCounter, String stageName, String stageCounter, String jobName){
        return getApiUrl() + "/properties/" + pipelineName + "/" + pipelineCounter + "/" + stageName + "/" + stageCounter + "/" + jobName;
    }

}
