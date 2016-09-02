package com.tw.go.task.dockercompose;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.ConfigVars;

import java.io.FileNotFoundException;

/**
 * Created by thomassc on 08.05.16.
 */
public class DockerComposeDownCommand extends DockerComposeCommand {

    public DockerComposeDownCommand(JobConsoleLogger console, ConfigVars configVars) throws FileNotFoundException {
        super(console,configVars);

        add("down");
        add("--remove-orphans");

        if (configVars.isChecked(DockerComposeTask.COMPOSE_REMOVE_VOLUMES)) {
            add("-v");
        }

        //addServiceList(configVars);
    }
}
