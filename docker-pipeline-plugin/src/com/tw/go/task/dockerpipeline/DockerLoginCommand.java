package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.ConfigVars;
import com.tw.go.plugin.common.Context;
import com.tw.go.plugin.common.AbstractCommand;

public class DockerLoginCommand extends DockerCommand
{

    public DockerLoginCommand(JobConsoleLogger console, ConfigVars configVars)
    {
        super(console, configVars);

        add("docker");
        add("login");
        add("--username");
        add(configVars.getValue(DockerTask.REGISTRY_USERNAME));
        add("--password");
        add(configVars.getValue(DockerTask.REGISTRY_PASSWORD));

        if (!configVars.isEmpty(DockerTask.REGISTRY_URL_FOR_LOGIN)) {
            add(configVars.getValue(DockerTask.REGISTRY_URL_FOR_LOGIN));
        }
    }
}