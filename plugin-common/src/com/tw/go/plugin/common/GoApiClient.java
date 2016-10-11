package com.tw.go.plugin.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MarkusW on 17.11.2015.
 */
public class GoApiClient extends ApiRequestBase {

    public final static  String PLAIN_JSON = "application/json";
    public final static  String API_V1_JSON = "application/vnd.go.cd.v1+json";

    Gson gson = new GsonBuilder().create();

    public GoApiClient(String apiUrl) throws GeneralSecurityException {
        super(apiUrl, null, null, true, true);
        setDefaultAccept(API_V1_JSON);
    }

    public String getJobProperty(String pipelineName, String pipelineCounter, String stageName, String stageCounter, String jobName, String propertyName) throws IOException {
        String uri = getJobPropertyRequestUri(pipelineName, pipelineCounter, stageName, stageCounter, jobName, propertyName);
        String resultCSV = requestGet(uri);
        Map<String, String> map = fromCSV(resultCSV);
        return map.get(propertyName);
    }

    public String setJobProperty(String pipelineName, String pipelineCounter, String stageName, String stageCounter, String jobName, String propertyName, String propertyValue) throws IOException {
        String uri = getJobPropertyRequestUri(pipelineName, pipelineCounter, stageName, stageCounter, jobName, propertyName);
        String result = requestPostFormUrlEncoded(uri, encodeFormData("value", escapeCSV(propertyValue)));
        return result.contains("created with value") ? propertyValue : result;
    }

    public Map<String, String> getJobProperties(String pipelineName, String pipelineCounter, String stageName, String stageCounter, String jobName) throws IOException {
        String uri = getJobPropertyRequestUri(pipelineName, pipelineCounter, stageName, stageCounter, jobName);
        String resultCSV = requestGet(uri);
        Map<String, String> map = fromCSV(resultCSV);
        return map;
    }

    public Map getPipelineConfig(String pipelineName) throws IOException {
        String uri = getPipelineConfigRequestUri(pipelineName);
        String data = requestGet(uri);
        return (Map) gson.fromJson(data, Object.class);
    }

    public InputStream getArtifact(String pipelineName, String pipelineCounter, String stageName, String stageCounter, String jobName, String artifact) throws IOException {
        String uri = getArtifactRequestUri(pipelineName, pipelineCounter, stageName, stageCounter, jobName, artifact);
        setAccept(PLAIN_JSON);
        return requestStream(uri, "GET", null, null);
    }

    public Map getPipelineHistory(String pipelineName, int offset) throws IOException {
        String uri = getPipelineHistoryRequestUri(pipelineName, offset);
        setAccept(PLAIN_JSON);
        String data = requestGet(uri);
        return (Map) gson.fromJson(data, Object.class);
    }

    private String getPipelineHistoryRequestUri(String pipelineName, int offset) {
        return getApiUrl() + "/api/pipelines/" + pipelineName + "/history/" + offset;
    }

    private String getPipelineConfigRequestUri(String pipelineName) {
        return getApiUrl() + "/api/admin/pipelines/" + pipelineName;
    }

    private String getJobPropertyRequestUri(String pipelineName, String pipelineCounter, String stageName, String stageCounter, String jobName, String propertyName) {
        return getApiUrl() + "/properties/" + pipelineName + "/" + pipelineCounter + "/" + stageName + "/" + stageCounter + "/" + jobName + "/" + propertyName;
    }

    private String getJobPropertyRequestUri(String pipelineName, String pipelineCounter, String stageName, String stageCounter, String jobName) {
        return getApiUrl() + "/properties/" + pipelineName + "/" + pipelineCounter + "/" + stageName + "/" + stageCounter + "/" + jobName;
    }

    private String getArtifactRequestUri(String pipelineName, String pipelineCounter, String stageName, String stageCounter, String jobName, String artifact) {
        return getApiUrl() + "/files/" + pipelineName + "/" + pipelineCounter + "/" + stageName + "/" + stageCounter + "/" + jobName + "/" + artifact;
    }


    private static String escapeCSV(String csv) {
        return csv.replace("\\", "\\\\")
                .replace(",", "\\,")
                .replace("\n", "\\\n,");
    }

    private static Map<String, String> fromCSV(String csv) {
        HashMap data = new HashMap();
        StringBuilder sb = new StringBuilder();
        ArrayList<String> header = new ArrayList<>();
        boolean readHeaders = true;
        int col = 0;
        for (char c : csv.toCharArray()) {
            if (c == ',') {
                if (readHeaders) {
                    header.add(sb.toString());
                } else {
                    data.put(header.get(col), sb.toString());
                }
                sb.setLength(0);
                col++;
            }
            else if (c == '\n') {
                if (!readHeaders) {
                    data.put(header.get(col), sb.toString());
                    sb.setLength(0);
                    col = 0;
                    break;
                }
                header.add(sb.toString());
                sb.setLength(0);
                col = 0;
                readHeaders = false;
            }
            else {
                sb.append(c);
            }
        }
        return data;
    }

}