package com.yiuhet.multimedia.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by yiuhet on 2019/8/19.
 * <p>
 * 简单封装AudioRecord的使用（录制PMC格式的音频）
 */
public class AudioRecordImpl {

    private static final String TAG = "AudioRecordImpl";

    public static final int DEFAULT_SOURCE = MediaRecorder.AudioSource.MIC; //音频采集的输入源 麦克风
    public static final int DEFAULT_SAMPLE_RATE = 44100;//采样率 44.1kHz
    public static final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;//通道数 双通道
    public static final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;//数据位宽 16bit

    private AudioRecord mAudioRecord; //AudioRecord实例

    private int mMinBufferSize = 0;//AudioRecord 内部的音频缓冲区的大小

    private Thread mAudioRecordThread; //录制音频的工作线程
    private boolean mIsRecordStarted = false;//是否正在录制音频
    private volatile boolean mIsLoopExit = false;//录制的工作线程停止的标志位

    private OnAudioFrameRecordListener mAudioFrameRecordListener;

    public interface OnAudioFrameRecordListener {
        public void onAudioFrameRecord(byte[] audioData);
    }

    public boolean isRecordStarted() {
        return mIsRecordStarted;
    }

    public void setOnAudioFrameRecordListener(OnAudioFrameRecordListener listener) {
        mAudioFrameRecordListener = listener;
    }

    /**
     * 开始收集音频
     *
     * @return
     */
    public boolean startRecord() {
        return startRecord(DEFAULT_SOURCE, DEFAULT_SAMPLE_RATE, DEFAULT_CHANNEL_CONFIG,
                DEFAULT_AUDIO_FORMAT);
    }

    /**
     * 开始收集音频
     *
     * @param audioSource    音频采集的输入源
     * @param sampleRateInHz 采样率
     * @param channelConfig  通道数
     * @param audioFormat    数据位宽
     * @return
     */
    public boolean startRecord(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat) {

        if (mIsRecordStarted) {
            Log.e(TAG, "Record already started !");
            return false;
        }

        mMinBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        if (mMinBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(TAG, "Invalid parameter !");
            return false;
        }
        Log.d(TAG, "采样率 x 位宽 x 通道数 = " + sampleRateInHz * 2 * audioFormat + " bytes !");
        Log.d(TAG, "getMinBufferSize = " + mMinBufferSize + " bytes !");

        mAudioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, mMinBufferSize);
        if (mAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
            Log.e(TAG, "AudioRecord initialize fail !");
            return false;
        }

        mAudioRecord.startRecording();

        mIsLoopExit = false;
        mAudioRecordThread = new Thread(new AudioCaptureRunnable());
        mAudioRecordThread.start();

        mIsRecordStarted = true;

        Log.d(TAG, "Start audio record success !");

        return true;
    }

    /**
     * 停止收集并释放资源
     */
    public void stopRecord() {

        if (!mIsRecordStarted || mAudioRecord == null) {
            return;
        }

        mIsLoopExit = true;
        try {
            mAudioRecordThread.interrupt();
            mAudioRecordThread.join(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            mAudioRecord.stop();
        }

        mAudioRecord.release();

        mIsRecordStarted = false;
        mAudioFrameRecordListener = null;

        Log.d(TAG, "Stop audio record success !");
    }

    private class AudioCaptureRunnable implements Runnable {

        @Override
        public void run() {

            while (!mIsLoopExit) {

                byte[] buffer = new byte[mMinBufferSize];
                Log.e(TAG, "time 0 : " + System.currentTimeMillis());
                int ret = mAudioRecord.read(buffer, 0, mMinBufferSize);
                Log.e(TAG, "time 1 : " + System.currentTimeMillis());
                if (ret == AudioRecord.ERROR_INVALID_OPERATION) {
                    Log.e(TAG, "Error ERROR_INVALID_OPERATION");
                } else if (ret == AudioRecord.ERROR_BAD_VALUE) {
                    Log.e(TAG, "Error ERROR_BAD_VALUE");
                } else {
                    if (mAudioFrameRecordListener != null) {
                        mAudioFrameRecordListener.onAudioFrameRecord(buffer);
                    }
                    Log.d(TAG, "OK, Record " + ret + " bytes !");
                }

                SystemClock.sleep(10);
            }
        }
    }
}
