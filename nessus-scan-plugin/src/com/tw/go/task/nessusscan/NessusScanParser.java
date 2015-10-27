package com.tw.go.task.nessusscan;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by MarkusW on 22.10.2015.
 */
public class NessusScanParser
{
    private JSONObject scan;
    private JSONObject remediations;
    private JSONObject info;

    public NessusScanParser(JSONObject scan){
        this.scan = scan;
        this.remediations = scan.getJSONObject("remediations");
        this.info = scan.getJSONObject("info");
    }

    public boolean isFinished()
    {
        if ( info.has("scan_end")) {
            return true;
        }
        return false;
    }

    public int numHosts()
    {
        return remediations.getInt("num_hosts");
    }

    public JSONArray Hosts()
    {
        return scan.getJSONArray("hosts");
    }

    public int numIssuesCritical()
    {
        int total = 0;
        JSONArray hosts = Hosts();
        for (Object obj : hosts)
        {
            JSONObject host = (JSONObject) obj;
            total += host.getInt("critical");

        }

        return total;
    }

    public int numIssuesHigh()
    {
        int total = 0;
        JSONArray hosts = Hosts();
        for (int i = 0; i < hosts.length(); i++)
        {
            JSONObject host = (JSONObject) hosts.get(i);
            total += host.getInt("high");

        }
        return total;
    }

    public int numIssuesMedium()
    {
        int total = 0;
        JSONArray hosts = Hosts();
        for (int i = 0; i < hosts.length(); i++)
        {
            JSONObject host = (JSONObject) hosts.get(i);
            total += host.getInt("medium");

        }
        return total;
    }

    public int numIssuesLow()
    {
        int total = 0;
        JSONArray hosts = Hosts();
        for (int i = 0; i < hosts.length(); i++)
        {
            JSONObject host = (JSONObject) hosts.get(i);
            total += host.getInt("low");

        }
        return total;
    }

    public int scanProgressTotal()
    {
        int total = 0;
        JSONArray hosts = Hosts();
        for (int i = 0; i < hosts.length(); i++)
        {
            JSONObject host = (JSONObject) hosts.get(i);
            total += host.getInt("scanprogresstotal");

        }
        return total;
    }

    public int scanProgressCurrent()
    {
        int total = 0;
        JSONArray hosts = Hosts();
        for (int i = 0; i < hosts.length(); i++)
        {
            JSONObject host = (JSONObject) hosts.get(i);
            total += host.getInt("scanprogresscurrent");

        }
        return total;
    }


}
