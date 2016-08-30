package com.tw.go.task.dockercompose;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.Context;
import com.tw.go.plugin.common.Result;
import com.tw.go.plugin.common.TaskExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.tw.go.task.dockercompose.DockerMachineListCommand.QUIET_MODE_OFF;
import static com.tw.go.task.dockercompose.DockerMachineTask.*;

public class DockerMachineTaskExecutor extends TaskExecutor {

    private final Logger logger = Logger.getLoggerFor(DockerMachineTaskExecutor.class);

    public DockerMachineTaskExecutor(JobConsoleLogger console, Context context, Map config) {
        super(console, context, config);
    }

    @Override
    protected String getPluginLogPrefix() {
        return "[Docker Machine] ";
    }

    @Override
    public Result execute() throws Exception {
        try {
            HashMap<String, String> states = getMachineStates();

            new DockerMachineListCommand(console, configVars)
                    .run();

            ArrayList<NameParams> nameParams = getNameParams(configVars.getValue(VMNAME));
            ArrayList<String> names = getExpandedValueArray(nameParams);

            ArrayList<NameParams> ipParams = getNameParams(configVars.getValue(GENERIC_IP_ADDRESS));
            ArrayList<String> addrs = getExpandedValueArray(ipParams);

            if ((addrs.size() > 0) && (names.size() != addrs.size())) {
                throw new Exception("Number of (expanded) names and ip-addresses do not match!");
            }

            for (int idx = 0; idx < names.size(); idx++) {
                String name = names.get(idx);
                configVars.setConfigValue(VMNAME, name);

                String addr = addrs.get(idx);
                configVars.setConfigValue(GENERIC_IP_ADDRESS, addr);

                if (states.keySet().contains(name)) {
                    if (configVars.isChecked(DockerMachineTask.REMOVE)
                            || "Error".equalsIgnoreCase(states.get(name))) {
                        new DockerMachineRemoveCommand(console, configVars)
                                .run();
                        new DockerMachineListCommand(console, configVars)
                                .run();
                        states.remove(name);
                    }
                }

                if (!"undefined".equals(configVars.getValue(DockerMachineTask.DRIVER))) {
                    if ("Stopped".equalsIgnoreCase(states.get(name))) {
                        new DockerMachineStartCommand(console, configVars)
                                .run();
                    } else if (!"Running".equalsIgnoreCase(states.get(name))) {
                        new DockerMachineCreateCommand(console, configVars)
                                .run();
                    }
                    new DockerMachineListCommand(console, configVars)
                            .run();
                }
            }
        } catch (Exception e) {
            logException(logger, e);
            return new Result(false, getPluginLogPrefix() + e.getMessage(), e);
        }

        return new Result(true, "Finished");
    }

    public ArrayList<String> getExpandedValueArray(ArrayList<NameParams> nameParams) {
        ArrayList<String> names = new ArrayList<>();
        for (NameParams params : nameParams) {
            for (int i = params.start; i <= params.stop; i += params.step) {
                names.add(String.format(params.format, i));
            }
        } return names;
    }

    public HashMap<String, String> getMachineStates() throws Exception {
        HashMap<String, String> states = new HashMap<>();
        AbstractCommand cmd = new DockerMachineListCommand(console, configVars, QUIET_MODE_OFF, null, "{{.Name}} {{.State}}");
        cmd.disableConsoleOutput().run();
        for (String s : cmd.getProcessOutput().getStdOut()) {
            String[] parts = s.split(" ");
            states.put(parts[0], parts[1]);
        }
        return states;
    }


    private class NameParams {
        public String format = "%d";
        public int start = 1;
        public int stop = 1;
        public int step = 1;
    }

    // "front[%d,1,5]back;front[%d,1,5,2]back"
    private ArrayList<NameParams> getNameParams(String value) {
        ArrayList<NameParams> nameParams = new ArrayList<>();
        if (!value.isEmpty()) {
            for (String s : value.split(";")) {
                if (!s.isEmpty()) {
                    nameParams.add(splitNameParams(s));
                }
            }
        }
        return nameParams;
    }

    // "front[%d,1..5,2]back"
    // "front[%d,1,5,2]back"
    private NameParams splitNameParams(String value) {
        NameParams nameParams = new NameParams();
        value = value.replace("..", ",");
        String[] parts = value.split("[\\[\\]]");

        if (parts.length > 3) {
            throw new IllegalArgumentException("Format allows only 3 parts: '<front>[%<format-char>,<start>,<stop>,<step>]<back>'.");
        }

        nameParams.format = parts[0];

        if (parts.length > 1) {
            parseFormatArgs(nameParams, parts);

            if (parts.length == 3) {
                nameParams.format += parts[2];
            }
        }


        return nameParams;
    }

    private void parseFormatArgs(NameParams nameParams, String[] parts) {
        String[] args = parts[1].split("[,]");
        if (args.length < 3 || args.length > 4) {
            throw new IllegalArgumentException("Only 3 or 4 format arguments allowed. ");
        }
        nameParams.format = parts[0] + args[0];
        nameParams.start = Integer.parseInt(args[1]);
        nameParams.stop = Integer.parseInt(args[2]);
        if (args.length == 4) {
            nameParams.step = Integer.parseInt(args[3]);
        }
    }

}