package com.yiuhet.multimedia.audio;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by yiuhet on 2019/8/19.
 */
public class MediaRecordHelper {
    private static final String TAG = "MediaRecordHelper";

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
        File soundFile = new File(dir, System.currentTimeMillis() + ".3gp");
        if (!soundFile.exists()) {
            try {
                soundFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);  //音频输入源
        //设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default
        //THREE_GPP(3gp格式，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
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
