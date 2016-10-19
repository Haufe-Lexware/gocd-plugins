package com.tw.go.task.fortify;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.Context;
import com.tw.go.plugin.common.GoApiConstants;
import com.tw.go.plugin.common.Result;
import com.tw.go.plugin.common.TaskExecutor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static com.tw.go.plugin.common.GoApiConstants.ENVVAR_NAME_GO_PIPELINE_COUNTER;

public class FortifyTaskExecutor extends TaskExecutor
{
    public FortifyTaskExecutor(JobConsoleLogger console, Context context, Map config)
    {
        super(console, context, config);
    }

    public Result execute() throws Exception
    {
        try
        {
            return runCommand();
        }
        catch (Exception e)
        {
            return new Result(false, getPluginLogPrefix() + e.getMessage(), e);
        }
    }

    public Result runCommand() throws Exception
    {
        boolean passed = true;
        int Ycritical = 0, Yhigh = 0, Ymedium = 0, Ylow = 0;

        FortifyRequest request = new FortifyRequest("", "", "", true);
        request.setUsername(configVars.getValue(FortifyTask.USERNAME));
        request.setPassword(configVars.getValue(FortifyTask.PASSWORD));
        request.setSscProject(configVars.getValue(FortifyTask.SSC_PROJECT));
        request.setSscVersion(configVars.getValue(FortifyTask.SSC_VERSION));

        getToken(request);

        int projectId = getProjectId(request);
        log("Project ID: " + projectId + "\n");

        JSONArray pointArrayData = getPointsArrayIssuePriority(request, projectId);

        for (int i = 0; i < pointArrayData.length(); i++)
        {
            JSONObject points = pointArrayData.getJSONObject(i);
            String filterX = points.getString("filterX");

            if(filterX.equals("Critical"))
                Ycritical = points.getInt("y");
            if(filterX.equals("High"))
                Yhigh = points.getInt("y");
            if(filterX.equals("Medium"))
                Ymedium = points.getInt("y");
            if(filterX.equals("Low"))
                Ylow = points.getInt("y");
        }

        log("Critical priority issues: " + Ycritical);
        log("High priority issues: " + Yhigh);
        log("Medium priority issues: " + Ymedium);
        log("Low priority issues: " + Ylow + "\n");

        if (Ycritical > 0 || Yhigh > 0)
        {
            passed = false;
        }

        JSONArray array = getDataArrayArtifacts(request, projectId);

        if(isUnsuccessfulScan(array))
        {
            passed = false;
        }

        log("Link to the scan: " + "https://v-fortifyapp/ssc/html/ssc/index.jsp#!/version/"
                + projectId + "/scan" + "\n");

        if(passed)
            return new Result(true, "Finished");
        else
            return new Result(false, "[Fortify] Error ! There may be: \n- critical or high priority " +
                    "issues\n- the name of the filescan doesn't match the pipeline counter\n" +
                    "- there are scans which require approval" + "\n");
    }

    public boolean isUnsuccessfulScan(JSONArray array)
    {
        boolean unsuccessfulScan = false;

        for (int i = 0; i < array.length(); i++)
        {
            JSONObject obj = array.getJSONObject(i);

            String pipelineNumber = obj.getString("originalFileName").substring(
                    obj.getString("originalFileName").lastIndexOf('_') + 1,
                    obj.getString("originalFileName").lastIndexOf('.'));

            if(!pipelineNumber.equals(configVars.getValue(ENVVAR_NAME_GO_PIPELINE_COUNTER)))
            {
                unsuccessfulScan = true;
            }

            if(obj.getString("status").equals("REQUIRE_AUTH"))
            {
                log("Scan " + i + 1 + ":");
                log("ID: " + obj.getInt("id"));
                log("Filename: " + obj.getString("fileName"));
                log("Original filename: " + obj.getString("originalFileName") + "\n");

                unsuccessfulScan = true;
            }
        }

        if(unsuccessfulScan)
            return true;

        return false;
    }

    public static JSONArray getSortedList(JSONArray array) throws JSONException {

        List<JSONObject> list = new ArrayList<JSONObject>();

        for (int i = 0; i < array.length(); i++) {
            list.add(array.getJSONObject(i));
        }

        Collections.sort(list, new FortifySortBasedOnUploadDate());
        JSONArray resultArray = new JSONArray(list);

        return resultArray;
    }

    private JSONArray getDataArrayArtifacts(FortifyRequest request, int projectId) {
        String result = request.request("GET", configVars.getValue(FortifyTask.FORTIFY_URL) +
                "/api/v1/projectVersions/" + projectId + "/artifacts", "application/json");

        JSONObject objResult = new JSONObject(result);
        JSONArray arrayData = objResult.getJSONArray("data");

        return arrayData;
    }

    private JSONArray getDataArrayIssueGroups(FortifyRequest request, int projectId) {
        String result = request.request("GET", configVars.getValue(FortifyTask.FORTIFY_URL) +
                "/api/v1/projectVersions/" + projectId + "/issueGroups", "application/json");

        JSONObject objResult = new JSONObject(result);
        JSONArray arrayData = objResult.getJSONArray("data");

        return arrayData;
    }

    private JSONArray getPointsArrayIssueFolder(FortifyRequest request, int projectId) {
        JSONArray pointArrayData;
        String result = request.request("GET", configVars.getValue(FortifyTask.FORTIFY_URL) +
                "/api/v1/projectVersions/" + projectId +
                "/issueSummaries/?seriestype=DEFAULT&groupaxistype=ISSUE_FOLDER",
                "application/json");

        JSONObject objResult = new JSONObject(result);
        JSONArray arrayData = objResult.getJSONArray("data");
        JSONObject first = arrayData.getJSONObject(0);
        JSONArray seriesArrayData = first.getJSONArray("series");
        JSONObject series = seriesArrayData.getJSONObject(0);
        pointArrayData = series.getJSONArray("points");

        return pointArrayData;
    }

    private JSONArray getPointsArrayIssuePriority(FortifyRequest request, int projectId) {
        String result = request.request("GET", configVars.getValue(FortifyTask.FORTIFY_URL) +
                "/api/v1/projectVersions/" + projectId +
                "/issueSummaries/?seriestype=DEFAULT&groupaxistype=ISSUE_FRIORITY",
                "application/json");

        JSONObject objResult = new JSONObject(result);
        JSONArray arrayData = objResult.getJSONArray("data");
        JSONObject first = arrayData.getJSONObject(0);
        JSONArray seriesArrayData = first.getJSONArray("series");
        JSONObject series = seriesArrayData.getJSONObject(0);

        return series.getJSONArray("points");
    }

    private int getProjectId(FortifyRequest request) {
        String result = request.request("GET", "https://v-fortifyapp/ssc" +
                "/api/v1/projectVersions?q=project.name:" + request.getSscProject() +
                "%2BAND%2Bname:" + request.getSscVersion() + "&fields=id",
                "application/json");

        JSONObject objResult = new JSONObject(result);
        JSONArray arrayData = objResult.getJSONArray("data");
        JSONObject first = arrayData.getJSONObject(0);
        int projectId = first.getInt("id");

        return projectId;
    }

    private void getToken(FortifyRequest request) {
        String result = request.request("POST", configVars.getValue(FortifyTask.FORTIFY_URL) +
                "/api/v1/auth/obtain_token", "application/json");
    }

    @Override
    protected String getPluginLogPrefix()
    {
        return "[Fortify] ";
    }
}
