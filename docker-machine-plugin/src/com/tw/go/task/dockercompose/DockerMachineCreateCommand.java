package com.tw.go.task.dockercompose;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.ConfigVars;
import com.tw.go.plugin.common.ListUtil;

import static com.tw.go.plugin.common.GoApiConstants.*;
import static com.tw.go.task.dockercompose.DockerMachineTask.*;

/**
 * Created by thomassc on 08.05.16.
 */
public class DockerMachineCreateCommand extends DockerMachineCommand {
    public DockerMachineCreateCommand(JobConsoleLogger console, ConfigVars configVars) {
        super(console);

        addEnv(configVars.environmentVars());

        add("docker-machine");
        add("create");

        for (String opt : ListUtil.splitByFirstOrDefault(configVars.getValue(ENGINE_OPT),';')) {
            add("--engine-opt");
            add(opt);
        }

        for (String env : ListUtil.splitByFirstOrDefault(configVars.getValue(ENGINE_ENV),';')) {
            add("--engine-env");
            add(env);
        }

        add("--driver");

        switch(configVars.getValue(DRIVER)) {
            case "generic":
                add("generic");
                addEnvFromConfig(configVars, GENERIC_SSH_USER);
                addEnvFromConfig(configVars, GENERIC_SSH_KEY);
                addEnvFromConfig(configVars, GENERIC_IP_ADDRESS);
                addEnvFromConfig(configVars, GENERIC_ENGINE_PORT);
                addEnvFromConfig(configVars, GENERIC_IP_ADDRESS);
                break;
            case "azure":
                add("azure");
                addEnvFromConfig(configVars, AZURE_SUBSCRIPTION_ID);
                addEnvFromConfig(configVars, AZURE_ENVIRONMENT);
                addEnvFromConfig(configVars, AZURE_IMAGE);
                addEnvFromConfig(configVars, AZURE_LOCATION);
                addEnvFromConfig(configVars, AZURE_RESOURCE_GROUP);
                addEnvFromConfig(configVars, AZURE_SIZE);
                addEnvFromConfig(configVars, AZURE_SSH_USER);
                addEnvFromConfig(configVars, AZURE_VNET);
                addEnvFromConfig(configVars, AZURE_SUBNET);
                addEnvFromConfig(configVars, AZURE_SUBNET_PREFIX);
                addEnvFromConfig(configVars, AZURE_AVAILABILITY_SET);
                addEnvFromConfig(configVars, AZURE_DOCKER_PORT);
                break;
            case "vmwarevsphere":
                add("vmwarevsphere");
                addEnvFromConfig(configVars, VSPHERE_USERNAME);
                addEnvFromConfig(configVars, VSPHERE_PASSWORD);
                addEnvFromConfig(configVars, VSPHERE_VCENTER);
                addEnvFromConfig(configVars, VSPHERE_DATACENTER);
                addEnvFromConfig(configVars, VSPHERE_CPU_COUNT);
                addEnvFromConfig(configVars, VSPHERE_MEMORY_SIZE);
                addEnvFromConfig(configVars, VSPHERE_DISK_SIZE);
                addEnvFromConfig(configVars, VSPHERE_BOOT2DOCKER_URL);
                break;
            default:
                throw new IllegalArgumentException("Unsupported driver");
        }

        add(configVars.getValue(VMNAME));
    }
}
