package com.tw.go.task.check_mk;

import org.json.JSONObject;

public class EditHostJob extends CheckMkJob
{
    public EditHostJob(ICheckMkClient checkMkClient) {
        super(checkMkClient);
        Action="edit_host";
        Parameters="";
    }

    @Override
    public String Execute(JSONObject requestObject) throws Exception {
        JSONObject jsonResult = ExecuteWebRequest(requestObject);
        if(!jsonResult.get("result_code").toString().equals("0"))
        {
            throw new EditHostException(requestObject,jsonResult);
        }
        return "Host "+requestObject.get("hostname")+" updated.";
    }
}


