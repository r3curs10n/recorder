package com.example.shreyas.meeting;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by shreyas on 17/07/16.
 */
public class CircularRecorder {

    private static int FREQUENCY = 44100;

    private AudioRecord mAudioRecord;
    private int mMemoryInSeconds;
    private CircularBuffer mCircularBuffer;
    private Context mContext;
    private boolean mIsRecording;
    private RecorderHandler mRecordHandler;

    public CircularRecorder(int memorySeconds, Context context) {
        mMemoryInSeconds = memorySeconds;
        mContext = context;
        mCircularBuffer = new CircularBuffer(mMemoryInSeconds * FREQUENCY);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, FREQUENCY, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, FREQUENCY * 10 * 2 /* 10 sec worth of data */);
        mRecordHandler = new RecorderHandler(mContext.getMainLooper());
    }

    public void startRecording() {
        mAudioRecord.startRecording();
        mIsRecording = true;
        mRecordHandler.sendMessage(mRecordHandler.obtainMessage());
    }

    public void stopRecording() {
        mIsRecording = false;
        mAudioRecord.stop();
    }

    public void playback() {
        int bufferSize = mMemoryInSeconds * FREQUENCY * 2;
        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, FREQUENCY, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STATIC);
        short[] data = mCircularBuffer.getData();
        track.write(data, 0, data.length);
        track.play();
    }

    private class RecorderHandler extends Handler {

        private short[] mData = new short[FREQUENCY * 2];    // 2 seconds worth of data.

        private RecorderHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (!mIsRecording) {
                return;
            }
            int nRead = mAudioRecord.read(mData, 0, mData.length);
            mCircularBuffer.add(mData, 0, nRead);

            sendMessageDelayed(obtainMessage(), 1000 /* ms */);
        }

    }

}
