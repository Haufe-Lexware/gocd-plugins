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
public class MaskingInputStream extends InputStream {
    BufferedReader br;
    MaskingJobConsoleLogger masker;
    String line = "";
    int lineIdx = 0;

    public MaskingInputStream(InputStream is, MaskingJobConsoleLogger masker) {
        br = new BufferedReader(new InputStreamReader(is));
        this.masker = masker;
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
            line = null;
//            e.printStackTrace();
        }
        if (line != null) {
            line = masker.getMaskedLine(line) + '\n';
            lineIdx = 0;
        }
    }
}
