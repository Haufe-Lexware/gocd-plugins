package com.tw.go.plugin.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomassc on 07.06.16.
 */
public class LinesInputStream extends InputStream {
    BufferedReader br;
    ArrayList<String> lines = new ArrayList<>();
    String line = "";
    int lineIdx = 0;
    boolean cacheOnly = false;

    public LinesInputStream(InputStream is) {
        br = new BufferedReader(new InputStreamReader(is));
    }

    @Override
    public int read() throws IOException {
        if (line == null) {
            return -1;
        }

        if (lineIdx >= line.length()) {
            fill();

            if (line == null) {
                return -1;
            }
        }

        return line.charAt(lineIdx++);
    }

    void fill() {
        try {
            line = br.readLine();
        } catch (IOException e) {
//            e.printStackTrace();
        }
        if (line != null) {
            lines.add(line);
            line += '\n';
            lineIdx = 0;
        }
    }

    public List getLines() {
        return lines;
    }
}
