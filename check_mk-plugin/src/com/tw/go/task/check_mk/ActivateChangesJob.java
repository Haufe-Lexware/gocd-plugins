package com.tw.go.task.check_mk;

import org.json.JSONObject;

public class ActivateChangesJob extends CheckMkJob
{
    public ActivateChangesJob(ICheckMkClient checkMkClient) {
        super(checkMkClient);
        Action="activate_changes";
        Parameters="&allow_foreign_changes=1&mode=dirty";
    }

    @Override
    public String Execute(JSONObject requestObject) throws Exception {
        JSONObject jsonResult = ExecuteWebRequest(requestObject);
        if(!jsonResult.get("result_code").toString().equals("0"))
        {
            throw new ActivateChangesException(jsonResult);
        }
        return "Changes activated";
    }
}
