package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.ConfigVars;
import com.tw.go.plugin.common.ListUtil;

import java.util.ArrayList;

public class DockerCreateCommand extends DockerCommand {

    private final Logger logger = Logger.getLoggerFor(DockerCreateCommand.class);
    protected String _containerId = null;

    public DockerCreateCommand(JobConsoleLogger console, ConfigVars configVars) throws Exception {
        super(console, configVars);
        add("docker");
        add("create");
        add("--name");
        _containerName = createRandomContainerName();
        add(_containerName);

        /*
        String id = getContainerID(console, configVars);
        String workingDir = getAbsoluteWorkingDir();
        if (id != null) {
            logger.info(String.format("Running inside container '%s'. Using '--volume-from' with working directory set to '%s'", id, workingDir));
            // todo: check, if the container provides access to the actual pipeline directory
            // e.g. "/var/lib/go-agent" ..
            add("--volumes-from");
            add(id);
        } else {
            logger.info(String.format("(Most likely) NOT running in container. Using '--volume' with working directory set to '%s'", workingDir));
            add("--volume");
            add(workingDir + ":" + workingDir);
        }

        add("--workdir");
        add(workingDir);
        */

        addRunEnvVars(configVars.getValue(DockerTask.RUN_ENV_VARS));

        add(configVars.getValue(DockerTask.RUN_IMAGE));
        for (String arg : splitArgs(configVars.getValue(DockerTask.RUN_ARGS))) {
            add(arg);
        }
    }

    protected String createRandomContainerName() {
        String randomId = Long.toHexString(Double.doubleToLongBits(Math.random()));
        return String.format("tmp_%s", randomId);        
    }

    protected String getContainerName() {
        return this._continerName;
    }

    protected void addRunEnvVars(String envVars) {
        for (String envVar : ListUtil.splitByFirstOrDefault(envVars, ';')) {
            if (!envVar.isEmpty()) {
                command.add("-e");
                command.add(envVar);
            }
        }
    }

    protected String getContainerID(JobConsoleLogger console, ConfigVars configVars) throws Exception {
        AbstractCommand cmd = new DockerContainerIdCommand(console, configVars)
                .disableConsoleOutput();
        cmd.run();
        return DockerContainerIdCommand.extractId(cmd.getProcessOutput());
    }
}