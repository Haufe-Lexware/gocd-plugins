package com.tw.go.plugin.common;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by thomassc on 25.05.16.
 */
public class Selector {
    Map map;

    public Selector(Map map) {
        this.map = map;
    }

    public Map getMap() {
        return map;
    }

    // { "name":[ {"name":"value1"},{"name":"value2"} ]}
    // "name"
    // "name.name"
    // "name[1].name"
    public static <T> T select(Map map, String selector, Class<T> classType) {
        Object node = map;
        for (String fragment : selector.split("\\.")) {

            String[] parts = fragment.split("[\\[\\]]");

            if (node instanceof Map) {
                node = ((Map) node).get(parts[0]);
            } else {
                node = null;
            }

            if (node == null) {
                break;
            }

            if (parts.length > 1) {
                int idx = Integer.parseInt(parts[1]);
                node = ((ArrayList) node).get(idx);
            }
        }

        if (node != null) {
            if (node.getClass() == Double.class) {
                if (classType == Integer.class) {
                    node = ((Double) node).intValue();
                } else if (classType == Long.class) {
                    node = ((Double) node).longValue();
                }
            }
        }

        return classType.cast(node);
    }

    public static <T> T select(Map map, String selector, T defaultValue) {
        T result = (T) select(map, selector, defaultValue.getClass());
        return (result == null) ? defaultValue : result;
    }

    public static <T> T select(Map map, String selector) {
        return (T) select(map, selector, Object.class);
    }

    public <T> T select(String selector, Class<T> classType) {
        return Selector.select(map, selector, classType);
    }

    public <T> T select(String selector) {
        return select(map,selector);
    }

    public <T> T select(String selector, T defaultValue) {
        return select(map,selector,defaultValue);
    }
}
