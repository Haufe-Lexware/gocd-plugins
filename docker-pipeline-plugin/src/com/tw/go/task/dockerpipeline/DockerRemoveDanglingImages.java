package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.ConfigVars;

import java.util.List;

public class DockerRemoveDanglingImages extends AbstractCommand {
    public DockerRemoveDanglingImages(JobConsoleLogger console, ConfigVars configVars) {
        super(console);
    }

    @Override
    public void run() throws Exception {

        List<String> ids = DockerEngine.getIds(new String[]{"docker", "images", "-f", "dangling=true", "-q"});

        if (ids.size() > 0) {
            add("docker");
            add("rmi");
            add("-f");
            for (String s : ids) {
                add(s);
            }
            super.run();
        }
    }
}
