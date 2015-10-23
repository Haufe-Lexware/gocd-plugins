package com.tw.go.task.nessusscan;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;


/**
 * Created by MarkusW on 20.10.2015.
 */
public class NessusClient {

    private String _apiUrl;
    private String _secretKey;
    private String _accessKey;

    public NessusClient(String apiUrl, String accessKey, String secretKey)
    {
        _apiUrl = apiUrl;
        _secretKey = secretKey;
        _accessKey = accessKey;

        disableSslVerification();
    }

    public String getScanTemplateUuid(String templateName) throws Exception
    {

        String uri = _apiUrl + "/editor/scan/templates";
        String resultData = requestGet(uri);
        JSONObject resultObj = new JSONObject(resultData);
        JSONArray templates = resultObj.getJSONArray("templates");
        for (Object obj : templates)
        {
            JSONObject template = (JSONObject) obj;
            String currentTemplateName = template.getString("name");
            if(currentTemplateName.equals(templateName))
            {
                return template.getString("uuid");
            }
        }

        throw new FileNotFoundException("Scan template '" + templateName + "' not found");
    }

    public int createScan(String name, String description, String targets, String templateUuid, int policyId) throws Exception
    {
        int scanId = 0;

        JSONObject objCreateScanSettings= new JSONObject();
        objCreateScanSettings.put("name", name);
        objCreateScanSettings.put("description", description);
        objCreateScanSettings.put("enabled", true);
        objCreateScanSettings.put("text_targets", targets);
        objCreateScanSettings.put("policy_id", policyId);

        JSONObject objCreateScan = new JSONObject();
        objCreateScan.put("uuid", templateUuid);
        objCreateScan.put("settings", objCreateScanSettings);

        String scanData = objCreateScan.toString();

        // create a new scan in nessus
        String uri = _apiUrl + "/scans";

        String resultData = requestPost(uri, scanData);
        JSONObject objResult = new JSONObject(resultData);
        JSONObject objScan = objResult.getJSONObject("scan");

        scanId = objScan.getInt("id");

        return scanId;

    }

    public JSONObject getScan(int scanId)
    {
        String scanUuid = "";
        String uri = _apiUrl + "/scans/%1$s";
        uri = String.format(uri, scanId);
        String resultData = requestGet(uri);
        return new JSONObject(resultData);
    }

    public boolean isScanFinished(int scanId)
    {
        JSONObject scan = getScan(scanId);
        NessusScanParser parser = new NessusScanParser(scan);
        return parser.isFinished();
    }

    public void exportScan(int scanId, String fileName, String format) throws Exception
    {
        JSONObject exportScan= new JSONObject();
        exportScan.put("format", format);
        exportScan.put("chapters", "vuln_hosts_summary;vuln_by_host;compliance_exec;remediations;vuln_by_plugin;compliance");
        String exportScanData = exportScan.toString();

        String uri = _apiUrl + "/scans/%1$s/export";
        uri = String.format(uri, scanId );
        String resultData = requestPost(uri, exportScanData);
        JSONObject objResult = new JSONObject(resultData);
        int fileId = objResult.getInt("file");

        while(!isExportFinished(scanId, fileId))
        {
            Thread.sleep(50);
        }

        downloadScanExportFile(scanId, fileId, fileName);
    }

    public void downloadScanExportFile(int scanId, int fileId, String fileName) throws Exception
    {
        String uri = _apiUrl + "/scans/%1$s/export/%2$s/download";
        uri = String.format(uri, scanId, fileId);
        String resultData = requestGet(uri);

        PrintWriter out = new PrintWriter(fileName);
        out.write(resultData);
    }

    public boolean isExportFinished(int scanId, int fileId)
    {
        String uri = _apiUrl + "/scans/%1$s/export/%2$s/status";
        uri = String.format(uri, scanId, fileId);
        String resultData = requestGet(uri);
        JSONObject objResult = new JSONObject(resultData);
        if(objResult.getString("status").equals("ready") )
        {
            return true;
        }
        return false;
    }

    public String launchScan(int scanId) throws Exception
    {
        String scanUuid = "";
        String uri = _apiUrl + "/scans/%1$s/launch";
        uri = String.format(uri, scanId);
        String resultData = requestPost(uri, "");
        JSONObject objResult = new JSONObject(resultData);
        scanUuid = objResult.getString("scan_uuid");

        return scanUuid;
    }

    public void deleteScan(int scanId) throws Exception
    {
        String uri = _apiUrl + "/scans/%1$s";
        uri = String.format(uri, scanId);
        requestDelete(uri);
    }


    public void getScanTemplates()
    {
        String uri = _apiUrl + "/editor/scan/templates";

        String data = requestGet(uri);

    }

    private String requestGet(String requestUrlString){
        return request(requestUrlString, "GET" );
    }

    private void requestDelete(String requestUrl)
    {
        request(requestUrl, "DELETE");
    }

    private String requestPost(String requestUrlString, String jsonData) throws Exception{
        return request(requestUrlString, jsonData, "POST");
    }

    private String request(String requestUrlString, String requestMethod){
        String result = "";


        // HTTP Get
        try {
            URL url = new URL(requestUrlString);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod(requestMethod);
            conn.setRequestProperty("X-ApiKeys", getXApiKeys()); // nessus x API keys for secure access
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            StringBuffer response = new StringBuffer();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            result = response.toString();

            conn.disconnect();
        } catch (Exception e ) {
            return e.getMessage();
        }

        return result;
    }


    private String request(String requestUrlString, String jsonData, String requestMethod) throws Exception{

        String result = "";

        // pass JSON File Data to REST Service
        try {
            URL url = new URL(requestUrlString);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            conn.setRequestMethod(requestMethod);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("X-ApiKeys", getXApiKeys()); // nessus x API keys for secure access
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
        } catch (Exception e) {
            System.out.println("\nError while calling REST Service");
            System.out.println(e.getMessage());
            throw e;
        }

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
