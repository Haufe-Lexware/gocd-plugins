package com.tw.go.task.dockerpipeline;
import com.tw.go.plugin.common.Context;

import java.util.ArrayList;
import java.util.List;

public abstract class DockerMultipleCommand implements ICommand
{
    protected List<ICommand> commands = new ArrayList<>();

    public DockerMultipleCommand(Context taskContext, Config taskConfig)
    {
        setupCommands(taskContext, taskConfig);
    }

    protected abstract void setupCommands(Context taskContext, Config taskConfig);

    public DockerMultipleCommand addCommand(ICommand command)
    {
        commands.add(command);
        return this;
    }

    public DockerMultipleCommand runCommand(ICommand command)
    {
        return addCommand(command);
    }

    public DockerMultipleCommand then(ICommand command)
    {
        return addCommand(command);
    }

    public void run() throws Exception
    {
        for (ICommand command : commands)
        {
            command.run();
        }
    }
}
