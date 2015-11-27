package com.tw.go.task.nessusscan;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import com.tw.go.plugin.common.ApiRequestBase;


/**
 * Created by MarkusW on 20.10.2015.
 */
public class NessusClient extends ApiRequestBase {

    public NessusClient(String apiUrl, String accessKey, String secretKey) throws GeneralSecurityException
    {
        super(apiUrl, accessKey, secretKey, true);
    }

    public String getScanTemplateUuid(String templateName) throws Exception
    {
        String uri = getApiUrl() + "/editor/scan/templates";
        String resultData = requestGet(uri);

        JSONObject resultObj = new JSONObject(resultData);
        JSONArray templates = resultObj.getJSONArray("templates");

        for (int i = 0; i < templates.length(); i++)
        {
            JSONObject template = (JSONObject) templates.get(i);
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
        String uri = getApiUrl() + "/scans";

        String resultData = requestPost(uri, scanData);
        JSONObject objResult = new JSONObject(resultData);
        JSONObject objScan = objResult.getJSONObject("scan");

        scanId = objScan.getInt("id");

        return scanId;

    }

    public JSONObject getScan(int scanId) throws Exception
    {
        String scanUuid = "";
        String uri = getApiUrl() + "/scans/%1$s";
        uri = String.format(uri, scanId);
        String resultData = requestGet(uri);
        return new JSONObject(resultData);
    }

    public boolean isScanFinished(int scanId) throws Exception
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

        String uri = getApiUrl() + "/scans/%1$s/export";
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
        String uri = getApiUrl() + "/scans/%1$s/export/%2$s/download";
        uri = String.format(uri, scanId, fileId);
        String resultData = requestGet(uri);

        PrintWriter out = new PrintWriter(fileName);
        out.write(resultData);
    }

    public boolean isExportFinished(int scanId, int fileId) throws Exception
    {
        String uri = getApiUrl() + "/scans/%1$s/export/%2$s/status";
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
        String uri = getApiUrl() + "/scans/%1$s/launch";
        uri = String.format(uri, scanId);
        String resultData = requestPost(uri, "");
        JSONObject objResult = new JSONObject(resultData);
        scanUuid = objResult.getString("scan_uuid");

        return scanUuid;
    }

    public void deleteScan(int scanId) throws Exception
    {
        String uri = getApiUrl() + "/scans/%1$s";
        uri = String.format(uri, scanId);
        requestDelete(uri);
    }

}
