package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.ConfigVars;

public class DockerLoginCommand extends AbstractCommand
{
    public DockerLoginCommand(JobConsoleLogger console, ConfigVars configVars)
    {
        super(console);

        add("docker");
        add("login");

        add("--username");
        add(configVars.getValue(DockerTask.REGISTRY_USERNAME));

        add("--password");
        add(configVars.getValue(DockerTask.REGISTRY_PASSWORD));

        add(configVars.getValue(DockerTask.REGISTRY_URL));
    }
}