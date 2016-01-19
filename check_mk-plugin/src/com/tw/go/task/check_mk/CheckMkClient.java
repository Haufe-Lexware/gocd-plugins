package com.tw.go.task.check_mk;

import com.tw.go.plugin.common.ApiRequestBase;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class CheckMkClient extends ApiRequestBase implements ICheckMkClient {

    private String username;
    private String password;

    public CheckMkClient(String serverUrl,String username, String password) throws Exception {
        super(serverUrl, "","", true);
        this.password=password;
        this.username=username;
    }

    @Override
    public String CreateRequestUrl(String action, String additionalParameters)
    {
        String uri = getApiUrl() + "?action="+action+"&_username="+username+"&_secret="+password+additionalParameters;
        return uri;
    }

    @Override
    public String CreateUrlParameters(JSONObject folderObject) throws UnsupportedEncodingException {
        if(folderObject==null) {
            return "";
        }
        String urlParameters = "request=" + URLEncoder.encode(folderObject.toString(), "UTF-8");
        return urlParameters;
    }

    @Override
    public String ExecuteJob(String url, String urlParameters)throws Exception
    {
        String data = requestPostFormUrlEncoded(url,urlParameters);
        return data;
    }

}
