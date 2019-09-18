package com.yiuhet.multimedia.audio;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yiuhet.multimedia.R;
import com.yiuhet.multimedia.audio.wav.WavFileReader;
import com.yiuhet.multimedia.audio.wav.WavFileWriter;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by yiuhet on 2019/8/19.
 * <p>
 * 音频学习：
 * 使用AudioRecord的界面
 */
public class AudioActivity extends Activity {

    private WavFileWriter mWavFileWriter;
    private AudioRecordHelper mAudioRecord;
    private MediaRecordImpl mMediaRecord;
    private AudioPlayer mAudioPlayer;
    private Button mOptBtn;
    private Button mOptBtn1;
    private Button mOptBtn2;
    private Button mOptBtn3;
    private String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private final int REQUEST_CODE_PERMISSIONS = 10;


    private static final String DEFAULT_TEST_FILE = Environment.getExternalStorageDirectory() + "/sounds/test.wav";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        mWavFileWriter = new WavFileWriter();
        mAudioRecord = new AudioRecordHelper();
        mMediaRecord = new MediaRecordImpl();
        mAudioPlayer = new AudioPlayer();
        mOptBtn = findViewById(R.id.btn_start_record);
        mOptBtn1 = findViewById(R.id.btn_start_record_media);
        mOptBtn2 = findViewById(R.id.btn_start_record_save_wav);
        mOptBtn3 = findViewById(R.id.btn_start_record_play_wav);
        mOptBtn.setOnClickListener(v -> optRecord());
        mOptBtn1.setOnClickListener(v -> optRecord1());
        mOptBtn2.setOnClickListener(v -> startRecordAndSaveWav());
        mOptBtn3.setOnClickListener(v -> optWav());
    }

    /**
     * 开始/暂停录制声音到文件
     */
    private void optRecord1() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            return;
        }
        if (!mMediaRecord.isRecordStarted()) {
            mMediaRecord.startRecord();
            mOptBtn1.setText("停止录制(MediaRecorder)");
        } else {
            mMediaRecord.stopRecord();
            mOptBtn1.setText("开始录制(MediaRecorder)");
        }
    }

    /**
     * 开始/暂停回声
     */
    private void optRecord() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            return;
        }
        if (!mAudioRecord.isRecordStarted()) {
            mAudioRecord.setOnAudioFrameRecordListener(new AudioRecordHelper.OnAudioFrameRecordListener() {
                @Override
                public void onAudioFrameRecord(byte[] audioData) {
                    mAudioPlayer.play(audioData, 0, audioData.length);
                }
            });
            mAudioRecord.startRecord();
            mAudioPlayer.startPlayer();
            mOptBtn.setText("停止回声(AudioRecord)");
        } else {
            mAudioRecord.stopRecord();
            mAudioPlayer.stopPlayer();
            try {
                mWavFileWriter.closeFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mOptBtn.setText("开始回声(AudioRecord)");
        }
    }

    /**
     * 开始录制音频并保存为WAV格式
     */
    private void startRecordAndSaveWav() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            return;
        }
        if (!mAudioRecord.isRecordStarted()) {
            try {
                mWavFileWriter.openFile(DEFAULT_TEST_FILE, AudioRecordHelper.DEFAULT_SAMPLE_RATE, 2, 16);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mAudioRecord.setOnAudioFrameRecordListener(new AudioRecordHelper.OnAudioFrameRecordListener() {
                @Override
                public void onAudioFrameRecord(byte[] audioData) {
                    mWavFileWriter.writeData(audioData, 0, audioData.length);
                }
            });
            mAudioRecord.startRecord();
            mOptBtn2.setText("停止录制 - WAV文件 (AudioRecord)");
        } else {
            mAudioRecord.stopRecord();
            try {
                mWavFileWriter.closeFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mOptBtn2.setText("开始录制 - WAV文件 (AudioRecord)");
            Toast.makeText(this, "录制成功：" + DEFAULT_TEST_FILE, Toast.LENGTH_SHORT).show();
        }
    }

    private WavFileReader mWavFileReader; //WAV文件头

    private static final int SAMPLES_PER_FRAME = 1024;

    private volatile boolean mIsTestingExit = false;
    private boolean isPlayWav = false;

    /**
     * 停止/播放 WAV音频文件
     */
    private void optWav() {
        if (!isPlayWav) {
            isPlayWav = true;
            playWavFile();
            mOptBtn3.setText("停止播放 - WAV文件 (AudioTrack)");
        } else {
            isPlayWav = false;
            stopWavFile();
            mOptBtn3.setText("开始播放 - WAV文件 (AudioTrack)");
        }
    }

    /**
     * 播放wav格式的音频
     */
    private void playWavFile() {
        mWavFileReader = new WavFileReader();
        try {
            mWavFileReader.openFile(DEFAULT_TEST_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mAudioPlayer.startPlayer();

        new Thread(new AudioPlayRunnable()).start();

    }

    /**
     * 停止播放wav格式的音频
     */
    private void stopWavFile() {
        mIsTestingExit = true;
    }

    private Handler mHandler = new Handler();

    class AudioPlayRunnable implements Runnable {
        @Override
        public void run() {
            byte[] buffer = new byte[mAudioPlayer.getMinBufferSize()];
            while (!mIsTestingExit && mWavFileReader.readData(buffer, 0, buffer.length) > 0) {
                mAudioPlayer.play(buffer, 0, buffer.length);
            }
            mAudioPlayer.stopPlayer();
            isPlayWav = false;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mOptBtn3.setText("开始播放 - WAV文件 (AudioTrack)");
                }
            });
            try {
                mWavFileReader.closeFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private boolean allPermissionsGranted() {
        return Arrays.asList(REQUIRED_PERMISSIONS).stream()
                .allMatch(s -> ContextCompat.checkSelfPermission(getBaseContext(), s) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                Toast.makeText(AudioActivity.this, "权限获取，重新操作！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
