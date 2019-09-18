package com.yiuhet.multimedia.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by yiuhet on 2019/8/19.
 * <p>
 * 简单封装AudioRecord的使用（录制PMC格式的音频）
 */
public class AudioRecordHelper {

    private static final String TAG = "AudioRecordHelper";

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

    //录音状态
    private Status mStatus = Status.STATUS_NO_READY;

    //文件名
    private String mFileName;

    //录音文件
    private List<String> mFilesNameList = new ArrayList<>();

    //线程池
    private ExecutorService mExecutorService;


    /**
     * 录音对象的状态
     */
    public enum Status {
        //未开始
        STATUS_NO_READY,
        //预备
        STATUS_READY,
        //录音
        STATUS_START,
        //暂停
        STATUS_PAUSE,
        //停止
        STATUS_STOP
    }

    public interface OnAudioFrameRecordListener {
        public void onAudioFrameRecord(byte[] audioData);
    }

    public boolean isRecordStarted() {
        return mIsRecordStarted;
    }

    public void setOnAudioFrameRecordListener(OnAudioFrameRecordListener listener) {
        mAudioFrameRecordListener = listener;
    }

    public void setFileName(String fileName) {
        this.mFileName = fileName;
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
        mStatus = Status.STATUS_READY;
        if (mAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
            Log.e(TAG, "AudioRecord initialize fail !");
            return false;
        }

        mAudioRecord.startRecording();
        String currentFileName = mFileName;
        if (mStatus == Status.STATUS_PAUSE) {
            //假如是暂停录音 将文件名后面加个数字,防止重名文件内容被覆盖
            currentFileName += mFilesNameList.size();
        }

        mFilesNameList.add(currentFileName);

        final String finalFileName = currentFileName;
        //将录音状态设置成正在录音状态
        mStatus = Status.STATUS_START;

        mIsLoopExit = false;
//        mAudioRecordThread = new Thread(new AudioCaptureRunnable());
        mAudioRecordThread = new Thread(new AudioSaveRunnable(finalFileName));
        mAudioRecordThread.start();


        mIsRecordStarted = true;

        Log.d(TAG, "Start audio record success !");

        return true;
    }


    /**
     * 暂停录音
     */
    public void pauseRecord() {
        Log.d("AudioRecorder", "===pauseRecord===");
        if (mStatus != Status.STATUS_START) {
            throw new IllegalStateException("没有在录音");
        } else {
            mAudioRecord.stop();
            mStatus = Status.STATUS_PAUSE;
        }
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

//                SystemClock.sleep(10);
            }
        }
    }

    private class AudioSaveRunnable implements Runnable {
        public String mFileName;

        public AudioSaveRunnable(String mFileName) {
            this.mFileName = mFileName;
        }

        @Override
        public void run() {
            FileOutputStream fos = null;
            try {
                File file = new File(mFileName);
                if (file.exists()) {
                    file.delete();
                }
                fos = new FileOutputStream(file);// 建立一个可存取字节的文件
            } catch (IllegalStateException e) {
                Log.e("AudioRecorder", e.getMessage());
                throw new IllegalStateException(e.getMessage());
            } catch (FileNotFoundException e) {
                Log.e("AudioRecorder", e.getMessage());

            }
            while (mStatus == Status.STATUS_START) {
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
                    if (fos != null) {
                        try {
                            fos.write(buffer);
                        } catch (IOException e) {
                            Log.e("IOException : ", e.getMessage());
                        }
                    }
                    Log.d(TAG, "OK, Record " + ret + " bytes !");
                }
            }
            try {
                if (fos != null) {
                    fos.close();// 关闭写入流
                }
            } catch (IOException e) {
                Log.e("AudioRecorder", e.getMessage());
            }
        }
    }

    /**
     * 合并Pcm文件
     *
     * @param recordFile 输出文件
     * @param files      多个文件源
     * @return 是否成功
     */
    private boolean mergePcmFiles(File recordFile, List<File> files) {
        if (recordFile == null || files == null || files.size() <= 0) {
            return false;
        }

        FileOutputStream fos = null;
        BufferedOutputStream outputStream = null;
        byte[] buffer = new byte[1024];
        try {
            fos = new FileOutputStream(recordFile);
            outputStream = new BufferedOutputStream(fos);

            for (int i = 0; i < files.size(); i++) {
                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(files.get(i)));
                int readCount;
                while ((readCount = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, readCount);
                }
                inputStream.close();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //3. 合并后记得删除缓存文件并清除list
        for (int i = 0; i < files.size(); i++) {
            files.get(i).delete();
        }
        files.clear();
        return true;
    }
}
