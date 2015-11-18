package com.tw.go.plugin.common;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpClientConnection;


/**
 * Created by MarkusW on 20.10.2015.
 */
public abstract class ApiRequestBase {

    private String _apiUrl;
    private String _secretKey;
    private String _accessKey;
    private boolean _apiKeysAvailable;

    public ApiRequestBase(String apiUrl, String accessKey, String secretKey, boolean disableSslVerification)
    {
        _apiUrl = apiUrl;
        if(secretKey.isEmpty() && accessKey.isEmpty())
        {
            _apiKeysAvailable = false;
        }
        else
        {
            _apiKeysAvailable = true;
            _secretKey = secretKey;
            _accessKey = accessKey;
        }

        if(disableSslVerification){
            disableSslVerification();
        }
    }

    public String getApiUrl(){
        return _apiUrl;
    }


    protected String requestGet(String requestUrlString) throws Exception{
        return request(requestUrlString, "GET" );
    }

    protected void requestDelete(String requestUrl) throws Exception
    {
        request(requestUrl, "DELETE");
    }

    protected String requestPost(String requestUrlString, String jsonData) throws Exception{
        return request(requestUrlString, jsonData, "POST");
    }

    private String request(String requestUrlString, String requestMethod) throws Exception{
        String result = "";


        // HTTP Get
        URL url = new URL(requestUrlString);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(requestMethod);

        if(_apiKeysAvailable) {
            conn.setRequestProperty("X-ApiKeys", getXApiKeys()); // API keys for secure access
        }
        conn.setRequestProperty("Accept", "application/json");


        if (conn.getResponseCode() != 200) {
            if(conn.getResponseCode() == 404)
            {
                throw new FileNotFoundException("Failed : HTTP error code : "
                    + conn.getResponseCode() + " Reguested Uri: " + requestUrlString);
            }
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        StringBuffer response = new StringBuffer();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            response.append("\n");
        }
        in.close();

        result = response.toString();

        conn.disconnect();


        return result;
    }


    protected int requestPostFormUrlEncodedValue(String requestUrlString, String value) throws Exception{

        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(requestUrlString);

        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("value", value));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        return response.getStatusLine().getStatusCode();

    }

    private String request(String requestUrlString, String jsonData, String requestMethod) throws Exception{

        String result = "";

        URL url = new URL(requestUrlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod(requestMethod);
        conn.setRequestProperty("Content-Type", "application/json");
        if(_apiKeysAvailable) {
            conn.setRequestProperty("X-ApiKeys", getXApiKeys()); // API keys for secure access
        }
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setDoOutput(true);
        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
        out.write(jsonData);
        out.close();

        int responseCode = conn.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        StringBuffer response = new StringBuffer();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        result = response.toString();

        in.close();


        return result;
    }


    private String getXApiKeys()
    {
        String apiKeys = "accessKey=%1$s; secretKey=%2$s;";
        return String.format(apiKeys, _accessKey, _secretKey);
    }

    static {
        disableSslVerification();
    }

    // this needs to be done, if there is no proper ssl connection available for nessus api server
    private static void disableSslVerification() {
        try
        {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

}
