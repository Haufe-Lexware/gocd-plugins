package com.tw.go.task.dockerpipeline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DockerEngine {
    public static List<String> getIds(String[] params) {
        ProcessBuilder pb = new ProcessBuilder(params);
        Process p = null;
        try {
            p = pb.start();
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        ArrayList<String> ids = new ArrayList<>();
        for (; ; ) {
            try {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                ids.add(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ids;
    }
}