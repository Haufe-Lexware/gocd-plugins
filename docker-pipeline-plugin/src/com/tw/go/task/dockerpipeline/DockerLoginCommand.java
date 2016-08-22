package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.ConfigVars;

public class DockerLoginCommand extends DockerCommand
{
    public DockerLoginCommand(JobConsoleLogger console, ConfigVars configVars)
    {
        super(console, configVars);

        String registryUrl = configVars.getValue(DockerTask.REGISTRY_URL_FOR_LOGIN);

        add("docker");
        add("login");

        if (!("".equals(registryUrl))) {
            add("--username");
            add(configVars.getValue(DockerTask.REGISTRY_USERNAME));
            add("--password");
            add(configVars.getValue(DockerTask.REGISTRY_PASSWORD));
            add(registryUrl);
        } else {
            add("-u");
            add(configVars.getValue(DockerTask.REGISTRY_USERNAME));
            add("-p");
            add(configVars.getValue(DockerTask.REGISTRY_PASSWORD));
        }
    }
}