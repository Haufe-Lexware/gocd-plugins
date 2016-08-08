package com.tw.go.task.dockercompose;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.ConfigVars;

import static com.tw.go.plugin.common.GoApiConstants.ENVVAR_NAME_GO_PIPELINE_NAME;
import static com.tw.go.task.dockercompose.DockerMachineTask.MACHINE_STORAGE_PATH;

/**
 * Created by thomassc on 08.05.16.
 */
public class DockerMachineListCommand extends DockerMachineCommand {
    public static final boolean QUIET_MODE_ON = true;
    public static final boolean QUIET_MODE_OFF = false;

    public DockerMachineListCommand(JobConsoleLogger console, ConfigVars configVars) {
        this(console, configVars, QUIET_MODE_OFF, null, null);
    }

    public DockerMachineListCommand(JobConsoleLogger console, ConfigVars configVars, boolean quiet, String filter, String format) {
        super(console, true);
        add("docker-machine");
        add("ls");
        if (quiet) {
            add("--quiet");
        }
        if (filter != null) {
            add("--filter");
            add(filter);
        }
        if (format != null) {
            add("--format");
            add(format);
        }
    }
}
