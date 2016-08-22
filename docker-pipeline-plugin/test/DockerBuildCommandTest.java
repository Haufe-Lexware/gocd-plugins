import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.ConfigVars;
import com.tw.go.task.dockerpipeline.DockerBuildCommand;
import com.tw.go.task.dockerpipeline.DockerTask;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DockerBuildCommandTest {

    DockerBuildCommand buildCommand;
    ConfigVars configVars = Mockito.mock(ConfigVars.class);

    static List<String> command;

    @Before
    public void init() {

        Mockito.when(configVars.getValue(DockerTask.IMAGE_NAME)).thenReturn("imagename");
        Mockito.when(configVars.getValue(DockerTask.DOCKER_FILE_NAME)).thenReturn("dockerfilename");
        Mockito.when(configVars.getValue(DockerTask.USERNAME)).thenReturn("username");
        Mockito.when(configVars.getValue(DockerTask.IMAGE_TAG)).thenReturn("imagetag");
        Mockito.when(configVars.getValue(DockerTask.REGISTRY_USERNAME)).thenReturn("registryusername");
        Mockito.when(configVars.getValue(DockerTask.REGISTRY_PASSWORD)).thenReturn("registrypassword");
        Mockito.when(configVars.getValue(DockerTask.REGISTRY_URL_FOR_LOGIN)).thenReturn("registry.example.io");
        Mockito.when(configVars.getValue(DockerTask.BUILD_ARGS)).thenReturn("buildargs");
        Mockito.when(configVars.getValue(DockerTask.IMAGE_TAG_POSTFIX)).thenReturn("postfix");

        buildCommand = new DockerBuildCommand(Mockito.mock(JobConsoleLogger.class), configVars);

        command = new ArrayList();
        command.add("docker");
        command.add("build");
        command.add("--pull=true");
        command.add("--force-rm");
        command.add("-f");
        command.add(buildCommand.getDockerfileAbsolutePath(configVars));
        command.add("--build-arg");
        command.add("buildargs");
        command.add("-t");
        command.add(configVars.getValue(DockerTask.REGISTRY_URL_FOR_LOGIN) + "/" + configVars.getValue(DockerTask.USERNAME)
                + "/" + configVars.getValue(DockerTask.IMAGE_NAME) + ":" + configVars.getValue(DockerTask.IMAGE_TAG)
                + "-" + configVars.getValue(DockerTask.IMAGE_TAG_POSTFIX));
    }

    @Test
    public void testGetUsername() {
        assertEquals(buildCommand.getUsername(configVars), configVars.getValue(DockerTask.USERNAME));
    }

    @Test
    public void testMakeBaseName() {
        assertEquals(buildCommand.makeBaseName(configVars.getValue(DockerTask.REGISTRY_URL_FOR_LOGIN),
                                                configVars.getValue(DockerTask.USERNAME),
                                                configVars.getValue(DockerTask.IMAGE_NAME)),
                configVars.getValue(DockerTask.REGISTRY_URL_FOR_LOGIN) + "/"
                + configVars.getValue(DockerTask.USERNAME) + "/"
                + configVars.getValue(DockerTask.IMAGE_NAME));

        assertEquals(buildCommand.makeBaseName("", configVars.getValue(DockerTask.USERNAME),
                                                configVars.getValue(DockerTask.IMAGE_NAME)),
                configVars.getValue(DockerTask.USERNAME) + "/"
                        + configVars.getValue(DockerTask.IMAGE_NAME));

        assertEquals(buildCommand.makeBaseName("", "", configVars.getValue(DockerTask.IMAGE_NAME)),
                configVars.getValue(DockerTask.IMAGE_NAME));
    }

    @Test
    public void testGetRegistryName() {
        assertEquals(buildCommand.getRegistryName(configVars.getValue(DockerTask.REGISTRY_URL_FOR_LOGIN)),
                configVars.getValue(DockerTask.REGISTRY_URL_FOR_LOGIN));
    }

    @Test
    public void testCommandBuildup() {
        List<String> list = buildCommand.getCommand();
        list.remove(list.size() - 1);

        assertEquals(buildCommand.getCommand(), command);

        assertEquals(buildCommand.getCommand().contains("docker"), true);
        assertEquals(buildCommand.getCommand().contains("build"), true);
        assertEquals(buildCommand.getCommand().contains("-t"), true);
        assertEquals(buildCommand.getCommand().contains(buildCommand.getDockerfileAbsolutePath(configVars)), true);
    }
}