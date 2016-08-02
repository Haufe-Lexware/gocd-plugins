package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.ConfigVars;


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
        String registryUrl = configVars.getValue(DockerTask.REGISTRY_URL_FOR_LOGIN);
        // Tweak registry URL when using official docker hub 
        if (!registryUrl.equals("hub.docker.com") &&
            !registryUrl.equals("docker.io")) {
            add(registryUrl);
        } else {
            add("https://index.docker.io/v1/");
        }
    }
}