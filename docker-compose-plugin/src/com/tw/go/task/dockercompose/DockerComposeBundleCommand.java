package com.tw.go.task.dockercompose;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.ConfigVars;

import java.io.FileNotFoundException;

/**
 * Created by thomassc on 08.05.16.
 */
public class DockerComposeBundleCommand extends DockerComposeCommand {

    public DockerComposeBundleCommand(JobConsoleLogger console, ConfigVars configVars) throws Exception {
        super(console, configVars);

        if (configVars.isEmpty(DockerComposeTask.BUNDLE_OUTPUT_PATH)) {
            throw new Exception("Missing setting for " + DockerComposeTask.BUNDLE_OUTPUT_PATH);
        }

        add("bundle");
        add("--output");
        add(configVars.getValue(DockerComposeTask.BUNDLE_OUTPUT_PATH));
    }
}
