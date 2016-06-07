package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.ConfigVars;

public class DockerRestartCommand extends AbstractCommand
{
    public DockerRestartCommand(JobConsoleLogger console, ConfigVars configVars)
    {
        super(console);

        add("sudo");
        add("service");
        add("docker");
        add("restart");
    }
}
