package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.ConfigVars;

public class DockerCpCommand extends DockerCommand
{

    public DockerCpCommand(JobConsoleLogger console, ConfigVars configVars, String source, String dest)
    {
        super(console, configVars);
        add("docker");
        add("cp");
        add(source);
        add(dest);
    }
}