package com.tw.go.task.dockercompose;

import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.tw.go.plugin.common.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Extension
public class DockerMachineTask extends BaseGoPlugin {

    public static final String VMNAME = "VMNAME";
    public static final String REMOVE = "REMOVE";

    public static final String MACHINE_STORAGE_PATH = "MACHINE_STORAGE_PATH";

    public static final String DRIVER = "DRIVER";
    public static final String ENGINE_ENV = "ENGINE_ENV";
    public static final String ENGINE_OPT = "ENGINE_OPT";

    public static final String GENERIC_ENGINE_PORT = "GENERIC_ENGINE_PORT";
    public static final String GENERIC_SSH_PORT = "GENERIC_SSH_PORT";
    public static final String GENERIC_IP_ADDRESS = "GENERIC_IP_ADDRESS";
    public static final String GENERIC_SSH_KEY = "GENERIC_SSH_KEY";
    public static final String GENERIC_SSH_USER = "GENERIC_SSH_USER";

    public static final String VSPHERE_USERNAME = "VSPHERE_USERNAME";
    public static final String VSPHERE_PASSWORD = "VSPHERE_PASSWORD";
    public static final String VSPHERE_CPU_COUNT = "VSPHERE_CPU_COUNT";
    public static final String VSPHERE_MEMORY_SIZE = "VSPHERE_MEMORY_SIZE";
    public static final String VSPHERE_DISK_SIZE = "VSPHERE_DISK_SIZE";
    public static final String VSPHERE_VCENTER = "VSPHERE_VCENTER";
    public static final String VSPHERE_DATACENTER = "VSPHERE_DATACENTER";
    public static final String VSPHERE_BOOT2DOCKER_URL = "VSPHERE_BOOT2DOCKER_URL";

    public static final String AZURE_SUBSCRIPTION_ID = "AZURE_SUBSCRIPTION_ID";
    public static final String AZURE_ENVIRONMENT = "AZURE_ENVIRONMENT";
    public static final String AZURE_IMAGE = "AZURE_IMAGE";
    public static final String AZURE_LOCATION = "AZURE_LOCATION";
    public static final String AZURE_RESOURCE_GROUP = "AZURE_RESOURCE_GROUP";
    public static final String AZURE_SIZE = "AZURE_SIZE";
    public static final String AZURE_SSH_USER = "AZURE_SSH_USER";
    public static final String AZURE_VNET = "AZURE_VNET";
    public static final String AZURE_SUBNET = "AZURE_SUBNET";
    public static final String AZURE_SUBNET_PREFIX = "AZURE_SUBNET_PREFIX";
    public static final String AZURE_AVAILABILITY_SET = "AZURE_AVAILABILITY_SET";
    public static final String AZURE_DOCKER_PORT = "AZURE_DOCKER_PORT";

    @Override
    protected GoPluginApiResponse handleGetConfigRequest(GoPluginApiRequest request) {
        return success(new ConfigDef()
                .add(VMNAME, "")
                .add(REMOVE, "", Required.NO)

                .add(ENGINE_ENV, "", Required.NO)
                .add(ENGINE_OPT, "", Required.NO)

                .add(DRIVER, "undefined", Required.NO)

                .add(GENERIC_IP_ADDRESS, "", Required.NO)
                .add(GENERIC_SSH_KEY, "", Required.NO)
                .add(GENERIC_SSH_USER, "root", Required.NO)
                .add(GENERIC_SSH_PORT, "22", Required.NO)
                .add(GENERIC_ENGINE_PORT, "2376", Required.NO)

                .add(VSPHERE_USERNAME, "", Required.NO)
                .add(VSPHERE_PASSWORD, "", Required.NO, Secure.YES)
                .add(VSPHERE_CPU_COUNT, "1", Required.NO)
                .add(VSPHERE_MEMORY_SIZE, "2048", Required.NO)
                .add(VSPHERE_DISK_SIZE, "20000", Required.NO)
                .add(VSPHERE_VCENTER, "", Required.NO)
                .add(VSPHERE_DATACENTER, "", Required.NO)
                .add(VSPHERE_BOOT2DOCKER_URL, "", Required.NO)

                .add(AZURE_SUBSCRIPTION_ID, "", Required.NO)
                .add(AZURE_ENVIRONMENT, "AzurePublicCloud", Required.NO)
                .add(AZURE_IMAGE, "canonical:UbuntuServer:15.10:latest", Required.NO)
                .add(AZURE_LOCATION, "westeurope", Required.NO)
                .add(AZURE_RESOURCE_GROUP, "docker-machine", Required.NO)
                .add(AZURE_SIZE, "Standard_A2", Required.NO)
                .add(AZURE_SSH_USER, "docker-user", Required.NO)
                .add(AZURE_VNET, "docker-machine", Required.NO)
                .add(AZURE_SUBNET, "docker-machine", Required.NO)
                .add(AZURE_SUBNET_PREFIX, "192.168.0.0/16", Required.NO)
                .add(AZURE_AVAILABILITY_SET, "docker-machine", Required.NO)
                .add(AZURE_DOCKER_PORT, "2376", Required.NO)

                .toMap());
    }

    @Override
    protected GoPluginApiResponse handleValidation(GoPluginApiRequest request) {
        return success(new HashMap<String, String>());
    }

    @Override
    protected GoPluginApiResponse handleTaskExecution(GoPluginApiRequest request) throws IOException, GeneralSecurityException {
        Map executionRequest = (Map) gson.fromJson(request.requestBody(), Object.class);

        Map<String, Map> config = (Map) executionRequest.get("config");

        Context context = new Context((Map) executionRequest.get("context"));

        try {
            Result result = new DockerMachineTaskExecutor(
                    MaskingJobConsoleLogger.getConsoleLogger(), context, config).execute();
            return success(result.toMap());
        } catch (Exception e) {
            Result result = new Result(false, e.getMessage());
            return success(result.toMap());
        }
    }

    @Override
    protected GoPluginApiResponse handleTaskView(GoPluginApiRequest request) {
        return getViewResponse("Docker Machine",
                getClass().getResourceAsStream("/views/task.template.html"));
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return new GoPluginIdentifier("task", Arrays.asList("1.0"));
    }

}