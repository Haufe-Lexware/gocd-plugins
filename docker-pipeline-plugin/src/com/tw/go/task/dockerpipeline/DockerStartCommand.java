package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.ConfigVars;

public class DockerStartCommand extends DockerCommand
{

    public DockerStartCommand(JobConsoleLogger console, ConfigVars configVars, String id)
    {
        super(console, configVars);
        add("docker");
        add("start");
        add("-a");
        add(id);
    }
}