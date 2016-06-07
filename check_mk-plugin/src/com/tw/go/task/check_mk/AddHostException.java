package com.tw.go.task.check_mk;

import org.json.JSONObject;

public class AddHostException extends Exception
{
    public AddHostException(JSONObject requestObject, JSONObject resultObject)
    {
        super("Failed to add host "+requestObject.get("hostname")+" to monitoring server. " +resultObject.get("result"));
    }
}

