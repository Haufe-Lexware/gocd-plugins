package com.tw.go.task.check_mk;

import org.json.JSONObject;

public class RemoveHostException extends Exception
{
    public RemoveHostException(JSONObject requestObject, JSONObject resultObject)
    {
        super("Failed to remove host "+requestObject.get("hostname")+" from monitoring server. " +resultObject.get("result"));
    }
}
