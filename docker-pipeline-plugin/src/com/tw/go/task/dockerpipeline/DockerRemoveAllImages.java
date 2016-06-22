package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.ConfigVars;
import java.util.List;

public class DockerRemoveAllImages extends AbstractCommand {
    public DockerRemoveAllImages(JobConsoleLogger console, ConfigVars configVars) {
        super(console);
    }

    @Override
    public void run() throws Exception {
        add("/bin/sh");
        add("-c");
        add("test -n \"$(docker images -q)\" && docker rmi -f $(docker images -q) || echo \"No images to delete\"");

        super.run();
    }
}