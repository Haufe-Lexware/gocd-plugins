package com.tw.go.task.fortify;

import java.io.*;
import java.security.GeneralSecurityException;

import com.tw.go.plugin.common.ApiRequestBase;

public class FortifyRequest extends ApiRequestBase
{
    private String sscProject = null;
    private String sscVersion = null;
    private String username = null;
    private String password = null;

    public FortifyRequest(String apiUrl, String accessKey, String secretKey, boolean disableSslVerification) throws GeneralSecurityException {
        super(apiUrl, accessKey, secretKey, disableSslVerification);
    }

    public String request(String methodType ,String url, String accept)
    {
        String resultData = null;

        setAccept(accept);
        setBasicAuthentication(username, password);

        try
        {
            if(methodType.equals("POST"))
                resultData = requestPost(url, "");
            if(methodType.equals("GET"))
                resultData = requestGet(url);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return resultData;
    }

    public void setUsername(String u)
    {
        username = u;
    }

    public void setPassword(String p)
    {
        password = p;
    }

    public void setSscProject(String ssp)
    {
        sscProject = ssp;
    }

    public void setSscVersion(String ssv)
    {
        sscVersion = ssv;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public String getSscProject()
    {
        return sscProject;
    }

    public String getSscVersion()
    {
        return sscVersion;
    }
}
