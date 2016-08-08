package com.tw.go.task.dockercompose;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.ProcessRunner;

import java.io.File;
import java.io.FileFilter;

import static com.tw.go.task.dockercompose.DockerMachineTask.MACHINE_STORAGE_PATH;

/**
 * Created by thomassc on 24.05.16.
 */
public class DockerMachineCommand extends AbstractCommand {

    public DockerMachineCommand(JobConsoleLogger logger) {
        this(logger, new ProcessRunner(), false);
    }

    public DockerMachineCommand(JobConsoleLogger logger, boolean ignoreErrors) {
        this(logger, new ProcessRunner(), ignoreErrors);
    }

    public DockerMachineCommand(JobConsoleLogger logger, ProcessRunner processRunner, boolean ignoreErrors) {
        super(logger, processRunner, ignoreErrors);

        String storagePath = ".docker/machine";
        addEnv(MACHINE_STORAGE_PATH, storagePath);

        fixPrivatKeyPermissions(storagePath);
    }

    public void fixPrivatKeyPermissions(String storagePath) {
        String machinesDir = getAbsoluteWorkingDir() + "/" + storagePath + "/machines";

        try {
            File[] dirs = new File(machinesDir).listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory();
                }
            });

            for (File dir : dirs) {
                File file = new File(dir.getAbsolutePath() + "/id_rsa");
                file.setExecutable(false, false);
                file.setWritable(false, false);
                file.setReadable(false, false);
                file.setWritable(true, true);
                file.setReadable(true, true);
            }
        } catch (Exception e) {
            // do we really want to suppress everything???
        }
    }
}
