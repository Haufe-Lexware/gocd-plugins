package com.tw.go.task.check_mk;

import org.json.JSONObject;

public class EditHostException extends Exception
{
    public EditHostException(JSONObject requestObject, JSONObject resultObject)
    {
        super("Failed to edit host "+requestObject.get("hostname")+". " +resultObject.get("result"));
    }
}

