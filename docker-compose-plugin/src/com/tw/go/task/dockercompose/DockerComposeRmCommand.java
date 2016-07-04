package com.tw.go.task.dockercompose;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.ConfigVars;

import java.io.FileNotFoundException;

/**
 * Created by thomassc on 08.05.16.
 */
public class DockerComposeRmCommand extends DockerComposeCommand {

    public DockerComposeRmCommand(JobConsoleLogger console, ConfigVars configVars) throws FileNotFoundException {
        super(console,configVars);

        add("rm");
        add("-f");

        if (configVars.isChecked(DockerComposeTask.COMPOSE_REMOVE_VOLUMES)) {
            add("-v");
        }

        addServiceList(configVars);
    }
}
