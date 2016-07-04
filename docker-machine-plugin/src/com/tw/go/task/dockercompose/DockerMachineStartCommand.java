package com.tw.go.task.dockercompose;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.ConfigVars;

import static com.tw.go.task.dockercompose.DockerMachineListCommand.QUIET_MODE_ON;

/**
 * Created by thomassc on 08.05.16.
 */
public class DockerMachineStartCommand extends DockerMachineCommand {

    public DockerMachineStartCommand(JobConsoleLogger console, ConfigVars configVars) {
        super(console, true);

        add("docker-machine");
        add("start");
        add(configVars.getValue(DockerMachineTask.VMNAME));
    }
}
