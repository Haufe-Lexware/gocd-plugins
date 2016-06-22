package com.tw.go.task.dockerpipeline;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.ConfigVars;
import com.tw.go.plugin.common.ProcessOutput;
import com.tw.go.plugin.common.Selector;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

public class DockerContainerIdCommand extends AbstractCommand {

    public DockerContainerIdCommand(JobConsoleLogger console, ConfigVars configVars) {
        super(console);

        disableConsoleOutput();

        try {
            add("docker");
            add("ps");
            add("-q");
            run();
            ProcessOutput output = getProcessOutput();
            if (output.isZeroReturnCode() && (output.getStdOut().size() > 0)) {
                command.clear();
                add("docker");
                add("inspect");
                for (String s : output.getStdOut()) {
                    add(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String extractId(ProcessOutput output) {
        if (output.isZeroReturnCode() && (output.getStdOut().size() > 0)) {
            try {
                InetAddress iAddress = InetAddress.getLocalHost();
                String currentIp = iAddress.getHostAddress();

                String json = "";
                for (String s : output.getStdOut()) {
                    json += s;
                }

                ArrayList list = (ArrayList) new GsonBuilder().serializeNulls().create().fromJson(json, Object.class);

                for (int i = 0; i < list.size(); i++) {
                    Map cont = (Map)list.get(i);
                    Map networkSettings = (Map)cont.get("NetworkSettings");
                    Map<String,Object> networks = (Map)networkSettings.get("Networks");
                    for (String k : networks.keySet()) {
                        Map network = (Map)networks.get(k);
                        String ip = (String)network.get("IPAddress");
                        if (ip.equals(currentIp)) {
                            String id = (String)cont.get("Id");
                            return id;
                        }
                    }
                }
            } catch (UnknownHostException e) {
//            e.printStackTrace();
            }
        }

        return null;
    }
}