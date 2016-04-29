package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.Context;
import com.tw.go.plugin.common.Result;
import com.tw.go.plugin.common.TaskExecutor;

import java.util.Map;

public class DockerTaskExecutor extends TaskExecutor
{
    public DockerTaskExecutor(JobConsoleLogger console, Context context, Map config)
    {
        super(console, context, config);
    }

    public Result execute (ConfigVars configVars)
    {
        try
        {
            configVars.printConfig(console, getPluginLogPrefix());
            DockerCommand.setConfigVars(configVars);
            return runCommand(new Config(configVars));
        }
        catch (Exception e)
        {
            return new Result(false, "Failed while running the task", e);
        }
    }

    public Result runCommand (Config taskConfig) throws Exception
    {
        try
        {
            DockerCommand.setPrefix(getPluginLogPrefix());

            if (taskConfig.cleanBeforeTask)
            {
                ICommand cmd = new DockerCleanBefore(context, taskConfig);
                cmd.run();
            }

            if (!(taskConfig.registryUsername.toString().isEmpty()) && !(taskConfig.registryPassword.toString().isEmpty()))
            {
                ICommand cmd = new DockerLoginCommand(context, taskConfig);
                cmd.run();
            }

            DockerBuildCommand build = new DockerBuildCommand(context, taskConfig);
            build.run();

            for (String tag : build.imageAndTag)
            {
                if (tag != null)
                {
                    ICommand cmd = new DockerPushCommand(context, taskConfig, tag);
                    cmd.run();
                }
            }
        }

        finally
        {
            if (taskConfig.cleanAfterTask)
            {
                ICommand cmd = new DockerRemoveAllImages(context, taskConfig);
                cmd.run();
            }
        }

        return new Result(true, "Finished");
    }

    @Override
    protected String getPluginLogPrefix()
    {
        return "[Docker] ";
    }
}
