package com.tw.go.task.dockercompose;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.ConfigVars;
import com.tw.go.plugin.common.Selector;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thomassc on 08.05.16.
 */
public class DockerComposePullCommand extends DockerComposeCommand {

    public DockerComposePullCommand(JobConsoleLogger console, ConfigVars configVars) throws FileNotFoundException {
        super(console,configVars);

        add("pull");
        add("--ignore-pull-failures");

        addServiceList(configVars);
    }
}
