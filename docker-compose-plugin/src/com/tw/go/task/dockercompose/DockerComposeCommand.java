package com.tw.go.task.dockercompose;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.ConfigVars;
import com.tw.go.plugin.common.Selector;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thomassc on 27.05.16.
 */
public class DockerComposeCommand extends AbstractCommand {

    public static final int DOCKER_PORT = 2376;
    protected Gson gson = new GsonBuilder().serializeNulls().create();
    private final static Logger logger = Logger.getLoggerFor(DockerComposeCommand.class);

    public DockerComposeCommand(JobConsoleLogger console, ConfigVars configVars) throws FileNotFoundException {
        super(console);

        // 1. just copy from the current environment
        addEnv(configVars.environmentVars());

        // 2. variables stated/defined explicitly
        addEnv(splitKeyValues(configVars.getValue(DockerComposeTask.ENV_VARS)));

        // 3. variables defined by the configuration settings
        addEnvFromConfig(configVars, DockerComposeTask.COMPOSE_FILE);

        // 4. variables based on the docker-machine environment settings
        addEnv(getDockerMachineEnv(configVars.getValue(DockerComposeTask.VMNAME)));

        add("docker-compose");
    }

    protected void addServiceList(ConfigVars configVars) {

        if (!configVars.getValue(DockerComposeTask.SERVICE).isEmpty()) {
            for (String svc : configVars.getValue(DockerComposeTask.SERVICE).split("[,; ]")) {
                add(svc);
            }
        }
    }

    public Map getDockerMachineEnv(String vmname) throws FileNotFoundException {
        Selector machines = new Selector(getDockerMachines(gson, getAbsoluteWorkingDir() + "/.docker/machine"));
        if (!machines.getMap().containsKey(vmname)) {
            throw new IllegalArgumentException("trying to use docker-compose with unknown machine");
        }

        String configPath = ".docker/machine/machines/" + vmname;
        String unifiedVmName = unifyMachineName(vmname);

        int port = machines.select(unifiedVmName + ".Driver.EnginePort", DOCKER_PORT);

        Map dme = new HashMap<>();

        dme.put("DOCKER_TLS_VERIFY", "1");
        dme.put("DOCKER_HOST", "tcp://" + machines.select(unifiedVmName + ".Driver.IPAddress") + ":" + port);
        dme.put("DOCKER_CERT_PATH", configPath);
        dme.put("DOCKER_MACHINE_NAME", "" + machines.select(unifiedVmName + ".Driver.MachineName"));
        dme.put("DM_HOST_IP", machines.select(unifiedVmName + ".Driver.IPAddress"));

        for (String name : ((Map<String, Object>) machines.getMap()).keySet()) {
            dme.put("DM_" + name.toUpperCase() + "_IP", machines.select(name + ".Driver.IPAddress"));
        }

        for (String key : ((Map<String, Object>) dme).keySet()) {
            console.printLine(String.format("setting extracted environment variable '%s' to value '%s'", key, dme.get(key)));
        }

        return dme;
    }

    static Map getDockerMachines(Gson gson, String storagePath) throws FileNotFoundException {
        Map map = new HashMap<>();
        File[] dirs = new File(storagePath + "/machines").listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });
        if (dirs != null) {
            for (File dir : dirs) {
                Map config = (Map) gson.fromJson(new FileReader(dir.getAbsolutePath() + "/config.json"), Object.class);
                map.put(unifyMachineName(dir.getName()), config);
            }
        } else {
            logger.info("No (machine) directories found under '" + storagePath + "/machines'");
        }
        return map;
    }

    static String unifyMachineName(String name) {
        return name.replaceAll("[.-]", "_");
    }

}