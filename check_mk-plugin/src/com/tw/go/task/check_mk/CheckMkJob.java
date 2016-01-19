package com.tw.go.task.check_mk;

import org.json.JSONObject;

public abstract class CheckMkJob{
    protected ICheckMkClient checkMkClient;
    protected String Action;
    protected String Parameters;
    public CheckMkJob(ICheckMkClient checkMkClient)
    {
        this.checkMkClient = checkMkClient;
    }
    public ICheckMkClient getCheckMkClient() {
        return checkMkClient;
    }

    protected JSONObject ExecuteWebRequest(JSONObject requestObject) throws Exception {
        String url = getCheckMkClient().CreateRequestUrl(Action,Parameters);
        String urlParameters= getCheckMkClient().CreateUrlParameters(requestObject);
        String result = getCheckMkClient().ExecuteJob(url,urlParameters);
        return new JSONObject(result);
    }

    public abstract String Execute(JSONObject requestObject) throws Exception;
}
