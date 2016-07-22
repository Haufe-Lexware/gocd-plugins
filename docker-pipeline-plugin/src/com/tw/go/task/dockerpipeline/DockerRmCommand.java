package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.ConfigVars;

public class DockerRmCommand extends DockerCommand
{

    public DockerRmCommand(JobConsoleLogger console, ConfigVars configVars, String id)
    {
        super(console, configVars);
        add("docker");
        add("rm");
        add(id);
    }
}