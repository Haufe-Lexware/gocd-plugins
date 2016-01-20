package com.tw.go.task.check_mk;

import org.json.JSONObject;

public class CheckMkRequestObjectFactory {
    public static JSONObject CreateAddHostObject(String folderName, String hostname, String serverIp)throws Exception
    {
        JSONObject objCreateAttributes = new JSONObject();
        objCreateAttributes.put("ipaddress", serverIp);
        JSONObject objAddHost= new JSONObject();
        objAddHost.put("folder", folderName);
        objAddHost.put("hostname", hostname);
        objAddHost.put("attributes", objCreateAttributes);
        return objAddHost;
    }

    public static JSONObject CreateRemoveHostObject(String hostname)throws Exception
    {
        JSONObject objRemoveHost= new JSONObject();
        objRemoveHost.put("hostname", hostname);
        return objRemoveHost;
    }

}




