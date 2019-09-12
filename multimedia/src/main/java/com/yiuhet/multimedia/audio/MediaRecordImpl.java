package com.yiuhet.multimedia.audio;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by yiuhet on 2019/8/19.
 */
public class MediaRecordImpl {
    private static final String TAG = "MediaRecordImpl";

    private MediaRecorder mMediaRecorder = null;

    private boolean mIsRecordStarted = false;//是否正在录制音频

    public boolean isRecordStarted() {
        return mIsRecordStarted;
    }

    public boolean startRecord() {
        if (mIsRecordStarted) {
            Log.e(TAG, "Record already started !");
            return false;
        }
        File dir = new File(Environment.getExternalStorageDirectory(), "sounds");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File soundFile = new File(dir, System.currentTimeMillis() + ".amr");
        if (!soundFile.exists()) {
            try {
                soundFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);  //音频输入源
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);   //设置输出格式
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);   //设置编码格式
        mMediaRecorder.setOutputFile(soundFile.getAbsolutePath());
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();  //开始录制
        } catch (IOException e) {
            e.printStackTrace();
        }
        mIsRecordStarted = true;
        return true;
    }

    //停止录制，资源释放
    public void stopRecord() {
        if (!mIsRecordStarted) {
            return;
        }
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        mIsRecordStarted = false;

    }
}
