package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.ConfigVars;
import java.util.List;

public class DockerRemoveAllContainers extends AbstractCommand {
    public DockerRemoveAllContainers(JobConsoleLogger console, ConfigVars configVars) {
        super(console);
    }

    @Override
    public void run() throws Exception {
        add("/bin/sh");
        add("-c");
        add("test -n \"$(docker ps -a -q)\" && docker rm -f $(docker ps -a -q) || echo \"No containers to delete\"");

        super.run();
    }
}
