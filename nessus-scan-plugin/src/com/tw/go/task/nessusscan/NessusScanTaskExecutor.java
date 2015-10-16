
package com.tw.go.task.nessusscan;

import com.thoughtworks.go.plugin.api.task.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NessusScanTaskExecutor {

//    public static final String CURLED_FILE = "index.txt";

//    public Result execute(Config config, Context context, JobConsoleLogger console) {
    public Result execute( Context context, JobConsoleLogger console) {
        // TODO execute scan here

        return new Result(true, "scan of server successful" );
        /*
        try {
            return runCommand(context, config, console);
        } catch (Exception e) {
            return new Result(false, "Failed to download file from URL: " + config.getUrl(), e);
        }
        */
    }

    /*
    private Result runCommand(Context taskContext, Config taskConfig, JobConsoleLogger console) throws IOException, InterruptedException {
        ProcessBuilder curl = createCurlCommandWithOptions(taskContext, taskConfig);
        console.printLine("Launching command: " + curl.command());
        curl.environment().putAll(taskContext.getEnvironmentVariables());
        console.printEnvironment(curl.environment());

        Process curlProcess = curl.start();
        console.readErrorOf(curlProcess.getErrorStream());
        console.readOutputOf(curlProcess.getInputStream());

        int exitCode = curlProcess.waitFor();
        curlProcess.destroy();

        if (exitCode != 0) {
            return new Result(false, "Failed downloading file. Please check the output");
        }

        return new Result(true, "Downloaded file: " + CURLED_FILE);
    }

    ProcessBuilder createCurlCommandWithOptions(Context taskContext, Config taskConfig) {
        String destinationFilePath = taskContext.getWorkingDir() + "/" + CURLED_FILE;

        List<String> command = new ArrayList<String>();
        command.add("nessusscan");
        command.add(taskConfig.getRequestType());
        if (taskConfig.getSecureConnection().equals("no")) {
            command.add("--insecure");
        }
        if (taskConfig.getAdditionalOptions() != null && !taskConfig.getAdditionalOptions().trim().isEmpty()) {
            String parts[] = taskConfig.getAdditionalOptions().split("\\s+");
            for (String part : parts) {
                command.add(part);
            }
        }
        command.add("-o");
        command.add(destinationFilePath);
        command.add(taskConfig.getUrl());

        return new ProcessBuilder(command);
    }
    */
}