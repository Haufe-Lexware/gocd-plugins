package com.tw.go.plugin.common;

import java.util.Map;
import java.util.Set;

public class ConfigVars {
    protected Map<String, Map> config;
    protected Map<String, String> envVars;

    public ConfigVars(Map<String, Map> config, Map<String, String> envVars) {
        this.config = config;
        this.envVars = envVars;
    }

    public String getValue(String key) {
        String value;
        Map m = config.get(key);
        if (m != null) {
            value = m.get("value").toString();
        } else {
            value = envVars.get(key);
        }
        return value != null ? value : "";
    }

    public boolean isEmpty(String key) {
        return getValue(key).isEmpty();
    }

    public boolean isChecked(String key) {
        switch(getValue(key).toLowerCase()) {
            case "true":
            case "on":
            case "yes":
            case "active":
            case "enabled":
            case "1":
            case "y":
                return true;
        }
        return false;
    }

    public void setConfigValue(String key, String value) {
        Map prop = config.get(key);
        prop.put("value",value);
        // config.put(key,prop);
    }

    public Set<String> configKeys() {
        return config.keySet();
    }

    public Set<String> environmentKeys() {
        return envVars.keySet();
    }

    public Map environmentVars() {
        return envVars;
    }
}