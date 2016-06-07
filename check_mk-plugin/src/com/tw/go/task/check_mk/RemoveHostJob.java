package com.tw.go.task.check_mk;

import org.json.JSONObject;

public class RemoveHostJob extends CheckMkJob
{
    public RemoveHostJob(ICheckMkClient checkMkClient) {
        super(checkMkClient);
        Action="delete_host";
        Parameters="";
    }

    @Override
    public String Execute(JSONObject requestObject) throws Exception {
        JSONObject jsonResult = ExecuteWebRequest(requestObject);
        if(!jsonResult.get("result_code").toString().equals("0"))
        {
            throw new RemoveHostException(requestObject,jsonResult);
        }
        return "Host "+requestObject.get("hostname")+" removed";
    }
}


