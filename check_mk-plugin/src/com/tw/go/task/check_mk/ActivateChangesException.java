package com.tw.go.task.check_mk;

import org.json.JSONObject;

public class ActivateChangesException extends Exception
{
    public ActivateChangesException(JSONObject resultObject)
    {
        super("Failed to activate changes. " +resultObject.get("result"));
    }
}
