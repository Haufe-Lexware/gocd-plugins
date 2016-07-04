package com.tw.go.task.dockercompose;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.ConfigVars;

import static com.tw.go.plugin.common.GoApiConstants.ENVVAR_NAME_GO_PIPELINE_NAME;
import static com.tw.go.task.dockercompose.DockerMachineListCommand.QUIET_MODE_ON;
import static com.tw.go.task.dockercompose.DockerMachineTask.MACHINE_STORAGE_PATH;

/**
 * Created by thomassc on 08.05.16.
 */
public class DockerMachineRemoveCommand extends DockerMachineCommand {

    public DockerMachineRemoveCommand(JobConsoleLogger console, ConfigVars configVars) {
        super(console, true);

        String vmname = configVars.getValue(DockerMachineTask.VMNAME);

        add("docker-machine");
        add("rm");
        add("--force");
        add(vmname);

        try {
            AbstractCommand command = new DockerMachineListCommand(console, configVars, QUIET_MODE_ON, "state=Error", null)
                    .disableConsoleOutput();
            command.run();
            for (String s : command.getProcessOutput().getStdOut()) {
                if (s != null) {
                    if (s.split(" ").length == 1) {
                        if (!vmname.equals(s)) {
                            add(s);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // do nothing
        }
    }
}
