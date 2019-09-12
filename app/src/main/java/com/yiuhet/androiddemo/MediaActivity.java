package com.yiuhet.androiddemo;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;

/**
 * 播放音视频的界面
 */
public class MediaActivity extends Activity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private Button play, pause, stop;
    private MediaPlayer player;
    private VideoView videoPlayer;
    private SeekBar mSeekBar;
    private TextView tv, tv2;
    private Handler mHandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(this, 1000);
            int currentTime = Math
                    .round(player.getCurrentPosition() / 1000);
            String currentStr = String.format("%s%02d:%02d", "当前时间 ",
                    currentTime / 60, currentTime % 60);
            tv.setText(currentStr);
            mSeekBar.setProgress(player.getCurrentPosition());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        stop = findViewById(R.id.stop);
        videoPlayer = findViewById(R.id.videoview);
        mSeekBar = findViewById(R.id.seekbar);
        tv = findViewById(R.id.tv);
        tv2 = findViewById(R.id.tv2);
        mSeekBar.setOnSeekBarChangeListener(this);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        stop.setOnClickListener(this);
        player = new MediaPlayer();
        initMediaplayer();
        initVideoPlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            mHandler.removeCallbacksAndMessages(null);
            player.release();
        }

    }

    private void initMediaplayer() {
        try {
            File file = new File(Environment.getExternalStorageDirectory()
                    + "/Download/", "aiqiu.mp3");
            Log.e("yiuhet", "file : " + file.getPath());
            Log.e("yiuhet", "file : " + file.isFile());

            player.setDataSource(file.getPath());
            Log.e("播放器", file.toString());
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initVideoPlayer() {
        File file = new File(Environment.getExternalStorageDirectory()
                + "/Download/", "sample.mp4");
        videoPlayer.setVideoPath(file.getPath());// 指定视频文件的路径

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (player != null) {
            player.seekTo(seekBar.getProgress());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
//                if (!player.isPlaying()) {
//                    player.start();
//                    int totalTime = Math.round(player.getDuration() / 1000);
//                    String str = String.format("%02d:%02d", totalTime / 60,
//                            totalTime % 60);
//                    tv2.setText(str);
//                    mSeekBar.setMax(player.getDuration());
//                    mHandler.postDelayed(runnable, 1000);
//                }
                startActivity(new Intent(MediaActivity.this, MainActivity.class));
//                if (!videoPlayer.isPlaying()) {
//                    videoPlayer.start();
//                }
                break;
            case R.id.pause:
//                if (player.isPlaying()) {
//                    player.pause();
//                }
                if (videoPlayer.isPlaying()) {
                    videoPlayer.pause();
                }
                break;
            case R.id.stop:
//                if (player.isPlaying()) {
//                    player.reset();
//                    initMediaplayer();
//                }
                if (videoPlayer.isPlaying()) {
                    videoPlayer.resume();
                }
                break;

            default:
                break;
        }
    }
}
