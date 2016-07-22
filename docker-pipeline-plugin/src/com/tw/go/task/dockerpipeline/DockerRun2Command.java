package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.ConfigVars;
import com.tw.go.plugin.common.ListUtil;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class DockerRun2Command extends DockerCommand {

    private final Logger logger = Logger.getLoggerFor(DockerRun2Command.class);
    ConfigVars configVars;

    public DockerRun2Command(JobConsoleLogger console, ConfigVars configVars) throws Exception {
        super(console, configVars);
        this.configVars = configVars;
    }

    @Override
    public void run() throws Exception {
        AbstractCommand cmd = new DockerCreateCommand(console, configVars);
        cmd.run();
        if (cmd.isSuccessful()) {
            String id = cmd.getProcessOutput().getStdOut().get(0);

            if (!configVars.isEmpty(DockerTask.RUN_PRE_COPY_TO)) {
                String hostDir = makeHostDir(getAbsoluteWorkingDir(), configVars.getValue(DockerTask.RUN_PRE_COPY_FROM));
                logger.info(String.format("Copying FROM %s",hostDir));
                new DockerCpCommand(console, configVars, hostDir, id + ":" + configVars.getValue(DockerTask.RUN_PRE_COPY_TO))
                        .run();
            }

            new DockerStartCommand(console, configVars, id)
                    .run();

            if (!configVars.isEmpty(DockerTask.RUN_POST_COPY_FROM)) {
                String hostDir = makeHostDir(getAbsoluteWorkingDir(), configVars.getValue(DockerTask.RUN_POST_COPY_TO));
                logger.info(String.format("Copying TO %s",hostDir));
                new DockerCpCommand(console, configVars, id + ":" + configVars.getValue(DockerTask.RUN_POST_COPY_FROM), hostDir)
                        .run();
            }

            new DockerRmCommand(console, configVars, id)
                    .run();
        }
    }

    String makeHostDir(String workingDir, String relative) throws Exception {
        String relativePath = Paths.get(workingDir,relative).normalize().toAbsolutePath().toString();
        if (!relativePath.startsWith(workingDir)) {
            throw new Exception("Trying to escape working directory");
        }
        return  relativePath;
    }
}