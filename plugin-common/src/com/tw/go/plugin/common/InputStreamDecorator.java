package com.tw.go.plugin.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class InputStreamDecorator extends InputStream
{
    private BufferedReader br;
    private LinkedList<Integer> outQueue = new LinkedList<>();
    private final byte[] prefix;
    private final byte[] eol = "\n".getBytes();

    public InputStreamDecorator(InputStream in, String prefix)
    {
        this.br = new BufferedReader(new InputStreamReader(in));
        this.prefix = prefix.getBytes();
    }

    @Override
    public int read() throws IOException
    {
        if (outQueue.isEmpty())
        {
            String line = br.readLine();

            if (line == null)
            {
            return -1;
            }

            for (byte b : prefix)
            {
            outQueue.add((int) b);
            }

            byte[] bytes = line.getBytes();

            for (byte b : bytes)
            {
                outQueue.add((int) b);
            }

            outQueue.add((int)eol[0]);
        }

        return outQueue.remove();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        int i = 0;

        for (; ; )
        {
            int c = read();

            if (c == -1)
            {
                break;
            }

            b[off + i++] = (byte)c;
        }

        return i > 0 ? i : -1;
    }

    @Override
    public int available() throws IOException
    {
        int available = super.available();
        return available > 0 ? available + prefix.length : 0;
    }
}
