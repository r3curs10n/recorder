package com.example.shreyas.meeting;

/**
 * Created by shreyas on 17/07/16.
 */
public class CircularBuffer {
    short[] buffer;
    int pos;

    public CircularBuffer(int size) {
        buffer = new short[size];
        pos = 0;
    }

    public void add(short[] data, int offset, int size) {
        for (int i=0; i<size; i++) {
            buffer[pos] = data[offset+i];
            nextpos();
        }
    }

    public short[] getData() {
        short[] ret = new short[buffer.length];
        int iRet = 0;
        int iBuffer = pos;
        for (iRet = 0; iRet < ret.length; iRet++) {
            ret[iRet] = buffer[iBuffer];
            iBuffer++;
            if (iBuffer == buffer.length) iBuffer = 0;
        }
        return ret;
    }

    private void nextpos() {
        pos++;
        if (pos == buffer.length) pos = 0;
    }
}
