package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.InputStreamDecorator;

import java.util.ArrayList;
import java.util.List;

public abstract class DockerCommand implements ICommand
{
    protected List<String> command = new ArrayList<>();
    protected List<String> imageAndTag = new ArrayList<>();

    protected static JobConsoleLogger logger = JobConsoleLogger.getConsoleLogger();

    public static void setPrefix(String prefix)
    {
        DockerCommand.prefix = prefix;
    }

    private static String prefix;

    public static void setConfigVars(ConfigVars configVars)
    {
        DockerCommand.configVars = configVars;
    }

    protected static ConfigVars configVars;

    @Override
    public void run() throws Exception
    {
        logger.printLine(prefix + "Run " + configVars.mask(renderDisplay()));

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        logger.readErrorOf(new InputStreamDecorator(process.getErrorStream(), prefix));
        logger.readOutputOf(new InputStreamDecorator(process.getInputStream(), prefix));

//        logger.readErrorOf(process.getErrorStream());
//        logger.readOutputOf(process.getInputStream());

        int exitCode = process.waitFor();
        process.destroy();

        if (exitCode != 0)
        {
            throw new ExitCodeNotZeroException("Failed while running task");
        }
    }

    public void add(String setting)
    {
        command.add(setting);
    }

    protected String renderDisplay()
    {
        StringBuilder sb = new StringBuilder();

        for (String s : command)
        {
            if (sb.length() > 0)
            {
                sb.append(' ');
            }

            if (sb.length() == 0 || s.charAt(0) == '-')
            {
                sb.append(s);
            }
            else
            {
                sb.append('"');
                sb.append(s);
                sb.append('"');
            }
        }

        return sb.toString();
    }

    public class ExitCodeNotZeroException extends Exception
    {
        public ExitCodeNotZeroException(String s)
        {
            super(s);
        }
    }
}
