package com.tw.go.task.check_mk;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by bocr on 30/12/2015.
 */
public interface ICheckMkClient {
    String CreateRequestUrl(String action, String additionalParameters);

    String CreateUrlParameters(JSONObject folderObject) throws UnsupportedEncodingException;

    String ExecuteJob(String url, String urlParameters)throws Exception;
}
