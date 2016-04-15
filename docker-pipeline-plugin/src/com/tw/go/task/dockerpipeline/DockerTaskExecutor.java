package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.Context;
import com.tw.go.plugin.common.Result;
import com.tw.go.plugin.common.TaskExecutor;

import java.util.Map;

/**
 * Created by BradeaC on 15/12/2015.
 */
public class DockerTaskExecutor extends TaskExecutor
{
    public DockerTaskExecutor(JobConsoleLogger console, Context context, Map config)
    {
        super(console, context, config);
    }

    public Result execute (Config config, Context context)
    {
        try
        {
            return runCommand (context, config);
        }
        catch (Exception e)
        {
            return new Result(false, "Failed while running the task", e);
        }
    }

    public Result runCommand (Context taskContext, Config taskConfig) throws Exception
    {
        try
        {
            if (taskConfig.isDockerClean)
            {
                ICommand cmd = new DockerCleanBefore(taskContext, taskConfig);
                cmd.run();
            }

            if (!(taskConfig.registryUsername.isEmpty()) && !(taskConfig.registryPassword.isEmpty()))
            {
                ICommand cmd = new DockerLoginCommand(taskContext, taskConfig);
                cmd.run();
            }

            DockerBuildCommand build = new DockerBuildCommand(taskContext, taskConfig);
            build.run();

            for (String tag : build.imageAndTag)
            {
                if (tag != null)
                {
                    ICommand cmd = new DockerPushCommand(taskContext, taskConfig, tag);
                    cmd.run();
                }
            }

            return new Result(true, "Finished");
        }
        catch (Exception e)
        {
            return new Result(false, "Failed", e);
        }
        finally
        {
            if (taskConfig.isDockerCleanAfter)
            {
                new DockerCleanCommand(taskContext, taskConfig).run();
            }
        }
    }

    @Override
    protected String getPluginLogPrefix()
    {
        return "Docker pipeline plugin: ";
    }
}
