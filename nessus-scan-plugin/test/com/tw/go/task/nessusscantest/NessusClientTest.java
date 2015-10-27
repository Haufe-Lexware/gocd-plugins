package com.tw.go.task.nessusscantest;

import com.tw.go.task.nessusscan.NessusClient;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Created by MarkusW on 26.10.2015.
 */
public class NessusClientTest {

    // properites required for executing the tests
    private static String nessusApiUrl;
    private static String nessusApiAccessKey;
    private static String nessusApiSecretKey;

    private static String nessusScanTemplateName;
    private static int nessusScanPolicy;

    private static String serverToScanIp;
    private static String exportFilename;


    @BeforeClass
    public static void init() throws Exception{

        // init from properites file (this is nessus installation specific.
        // Since we do not have a cloud nessus running for tests this input is individual and must not be published to github
        Properties props = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.properties");
        props.load(in);

        // api properites
        NessusClientTest.nessusApiUrl = props.getProperty("nessusApiUrl");
        NessusClientTest.nessusApiAccessKey = props.getProperty("nessusApiAccessKey");
        NessusClientTest.nessusApiSecretKey = props.getProperty("nessusApiSecretKey");


        NessusClientTest.nessusScanTemplateName = props.getProperty("nessusScanTemplateName");
        NessusClientTest.nessusScanPolicy = Integer.parseInt(props.getProperty("nessusScanPolicy"));
        NessusClientTest.serverToScanIp = props.getProperty("serverToScanIp");
        NessusClientTest.exportFilename = props.getProperty("exportFilename");

    }


    @Test
    public void testCreateScan() throws Exception {

        // create a scan client
        NessusClient nessusClient = new NessusClient(this.nessusApiUrl, this.nessusApiAccessKey, this.nessusApiSecretKey);

        // get scan template uuid
        String scanTemlateUuid = nessusClient.getScanTemplateUuid(this.nessusScanTemplateName);

        // create scan
        int scanId = nessusClient.createScan("TestScan", "Test Scan Description", this.serverToScanIp, scanTemlateUuid, this.nessusScanPolicy );

        // run the scan
        String scanUuid = nessusClient.launchScan(scanId);

        // wait until scan is finished.
        while (!nessusClient.isScanFinished(scanId))
        {
            Thread.sleep(5000);
        }

        // fetch the result
        JSONObject scanResult = nessusClient.getScan(scanId);

        // export the scan
        nessusClient.exportScan(scanId, this.exportFilename, "html");
    }
}