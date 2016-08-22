package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.ConfigVars;
import com.tw.go.plugin.common.Context;

import java.util.List;

public class DockerStopContainers extends AbstractCommand {

    public DockerStopContainers(JobConsoleLogger console, ConfigVars configVars) { super(console); }
        @Override
        public void run ()throws Exception {
            List<String> ids = DockerEngine.getIds(new String[]{"docker", "ps", "-a", "-q"});

            if (ids.size() > 0) {
                add("docker");
                add("stop");
                for (String s : ids) {
                    add(s);
                }

                super.run();
            }
        }
}
