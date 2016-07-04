package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.ConfigVars;

/**
 * Created by thomassc on 30.06.16.
 */
public class DockerCommand extends AbstractCommand {

    public DockerCommand(JobConsoleLogger console, ConfigVars configVars) {
        super(console);

        // 1. just copy from the current environment
        addEnv(configVars.environmentVars());
    }
}