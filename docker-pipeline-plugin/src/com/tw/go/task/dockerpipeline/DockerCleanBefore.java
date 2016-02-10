package com.tw.go.task.dockerpipeline;

import com.tw.go.plugin.common.Context;

/**
 * Created by BradeaC on 08/02/2016.
 */
public class DockerCleanBefore extends DockerMultipleCommand
{
    public DockerCleanBefore(Context taskContext, Config taskConfig)
    {
        super(taskContext, taskConfig);
    }

    @Override
    protected void setupCommands(Context taskContext, Config taskConfig)
    {
        runCommand(new DockerCleanBeforeCommand1(taskContext, taskConfig))
                .then(new DockerCleanBeforeCommand2(taskContext, taskConfig))
                .then(new DockerCleanBeforeCommand3(taskContext, taskConfig))
                .then(new DockerCleanBeforeCommand4(taskContext, taskConfig));
    }


}
