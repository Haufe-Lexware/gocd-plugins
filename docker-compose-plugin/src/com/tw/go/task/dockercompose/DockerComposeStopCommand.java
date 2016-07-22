package com.tw.go.task.dockercompose;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.ConfigVars;

import java.io.FileNotFoundException;

/**
 * Created by thomassc on 08.05.16.
 */
public class DockerComposeStopCommand extends DockerComposeCommand {

    public DockerComposeStopCommand(JobConsoleLogger console, ConfigVars configVars) throws FileNotFoundException {
        super(console,configVars);

        add("stop");

        addServiceList(configVars);
    }
}
