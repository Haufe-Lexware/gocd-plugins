package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import com.tw.go.plugin.common.ConfigVars;
import com.tw.go.plugin.common.AbstractCommand;

import java.util.List;

/**
 * Created by BradeaC on 13/04/2016.
 */
public class DockerRemoveAllContainers extends AbstractCommand {
    public DockerRemoveAllContainers(JobConsoleLogger console, ConfigVars configVars) {
        super(console);
    }

    @Override
    public void run() throws Exception {

        List<String> ids = DockerEngine.getIds(new String[]{"docker", "ps", "-a", "-q", "-f"});

        if (ids.size() > 0) {
            add("docker");
            add("rm");
            for (String s : ids) {
                add(s);
            }
            super.run();
        }
    }
}
