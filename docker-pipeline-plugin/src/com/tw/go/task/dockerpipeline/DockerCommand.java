package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BradeaC on 16/12/2015.
 */
public abstract class DockerCommand implements ICommand
{
    protected List<String> command = new ArrayList<>();
    protected List<String> imageAndTag = new ArrayList<>();

    protected static JobConsoleLogger logger = JobConsoleLogger.getConsoleLogger();

    public DockerCommand(Context taskContext, Config taskConfig)
    {
        buildCommand(taskContext, taskConfig);
    }

    protected abstract void buildCommand(Context taskContext, Config taskConfig);

    @Override
    public void run() throws Exception
    {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        logger.readErrorOf(process.getErrorStream());
        logger.readOutputOf(process.getInputStream());

        int exitCode = process.waitFor();
        process.destroy();

        if (exitCode != 0)
        {
            throw new ExitCodeNotZeroException("Failed while running task");
        }
    }

    public class ExitCodeNotZeroException extends Exception
    {
        public ExitCodeNotZeroException(String s)
        {
            super(s);
        }
    }
}
