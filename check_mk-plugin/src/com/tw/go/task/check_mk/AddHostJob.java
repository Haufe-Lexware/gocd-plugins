package com.tw.go.task.check_mk;

import org.json.JSONObject;

public class AddHostJob extends CheckMkJob
{
    public AddHostJob(ICheckMkClient checkMkClient) {
        super(checkMkClient);
        Action="add_host";
        Parameters="";
    }

    @Override
    public String Execute(JSONObject requestObject) throws Exception {
        JSONObject jsonResult = ExecuteWebRequest(requestObject);
        if(!jsonResult.get("result_code").toString().equals("0"))
        {
            throw new AddHostException(requestObject,jsonResult);
        }
        return "Host "+requestObject.get("hostname")+" added";
    }
}


