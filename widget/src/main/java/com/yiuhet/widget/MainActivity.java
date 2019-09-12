package com.yiuhet.widget;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.Util;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Predicate;

public class MainActivity extends Activity {

    private EditText mIpEdt;
    private Button mOptBtn;
    private boolean isRunning = false;
    private static final String DOWNLOAD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyDownload";
    private final int REQUEST_CODE_PERMISSIONS = 10;
    private String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private int mDownLoadCount; //下载总次数
    private int mSuccessCount;//成功次数
    private int mMD5Count;//md5校验失败次数
    private int mErrorCount;//请求发送失败错误
    private int mRebootCount;//请求发送失败错误
    private TextView tv1, tv2, tv3, tv4, tv5;
    private int curCount;//当前执行次数 到100次时关机

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
        mIpEdt = findViewById(R.id.edt_ip);
        tv1 = findViewById(R.id.tv_count1);
        tv2 = findViewById(R.id.tv_count2);
        tv3 = findViewById(R.id.tv_count3);
        tv4 = findViewById(R.id.tv_count4);
        tv5 = findViewById(R.id.tv_count5);
        mIpEdt.setText("http://192.168.100.199/test.zip");
        mOptBtn = findViewById(R.id.btn_opt);
        mOptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                reboot();
                isRunning = !isRunning;
                if (isRunning) {
                    startDownload();
                    mOptBtn.setText("Stop");
                } else {
                    mOptBtn.setText("Start");
                    stopDownload();
                }
            }
        });
        getStatus();
        startDownload();
        isRunning = true;
        mOptBtn.setText("Stop");
    }

    /**
     * 获取缓存的数据
     */
    private void getStatus() {
        SharedPreferences spf = getSharedPreferences("test", Context.MODE_PRIVATE);
        mDownLoadCount = spf.getInt("count1", 0);
        mSuccessCount = spf.getInt("count2", 0);
        mMD5Count = spf.getInt("count3", 0);
        mErrorCount = spf.getInt("count4", 0);
        mRebootCount = spf.getInt("count5", 0);


        tv1.setText("下载总次数：" + mDownLoadCount);
        tv2.setText("下载成功次数：" + mSuccessCount);
        tv3.setText("MD5校验失败次数：" + mMD5Count);
        tv4.setText("下载失败次数：" + mErrorCount);
        tv5.setText("重启次数：" + mRebootCount);
    }


    @TargetApi(Build.VERSION_CODES.N)
    private boolean allPermissionsGranted() {
        Arrays.asList(REQUIRED_PERMISSIONS).stream()
                .allMatch(new Predicate<String>() {
                    @Override
                    public boolean test(String s) {
                        return ContextCompat.checkSelfPermission(MainActivity.this.getBaseContext(), s) == PackageManager.PERMISSION_GRANTED;
                    }
                });
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private DownloadTask mTask;

    private void stopDownload() {
        if (mTask != null) {
            mTask.cancel();
        }
    }

    private void startDownload() {
        curCount++;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        Log.e("yiuhet", "ip: " + mIpEdt.getText().toString());
        Util.enableConsoleLog();
        mTask = new DownloadTask.Builder(mIpEdt.getText().toString(), storageDir)
                .setFilename(System.currentTimeMillis() + ".zip")
                .setPassIfAlreadyCompleted(true)
                .setFlushBufferSize(DownloadTask.Builder.DEFAULT_FLUSH_BUFFER_SIZE * 10)
                .setReadBufferSize(DownloadTask.Builder.DEFAULT_READ_BUFFER_SIZE * 10)
                .setSyncBufferSize(DownloadTask.Builder.DEFAULT_SYNC_BUFFER_SIZE * 10)
                .setConnectionCount(1)
                .build();
        mTask.enqueue(new DownloadListener1() {
            @Override
            public void taskStart(@NonNull DownloadTask task, @NonNull Listener1Assist.Listener1Model model) {
                Log.e("yiuhet", "taskStart : ");
            }

            @Override
            public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {
                Log.e("yiuhet", "retry : ");
            }

            @Override
            public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {
                Log.e("yiuhet", "connected : ");
            }

            @Override
            public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {

            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull Listener1Assist.Listener1Model model) {
                Log.e("yiuhet", "taskEnd : " + cause.name());
                Log.e("yiuhet", "task.getFile().getName() : " + task.getFile().getAbsolutePath());
                if (!cause.equals(EndCause.CANCELED))
                    mDownLoadCount++;
                if (cause.equals(EndCause.COMPLETED)) {
                    String md5 = md5sum(task.getFile().getAbsolutePath());
                    Log.e("yiuhet", "md5 : " + md5);
                    MToast.showToast(MainActivity.this, "success:" + task.getFilename() + "    md5：" + md5, Toast.LENGTH_SHORT);
                    if (md5.equals("2ae0204c25c010275f363491b071767a")) {
                        mSuccessCount++;
                        task.getFile().delete();
                    } else {
                        mMD5Count++;
                    }
                } else if (cause.equals(EndCause.ERROR)) {

                    mErrorCount++;
                    if (realCause != null) {
                        Log.e("yiuhet", "realCause : " + realCause);
                        MToast.showToast(MainActivity.this, "cause:" + realCause.getMessage(), Toast.LENGTH_SHORT);
                    }
                }
                tv1.setText("下载总次数：" + mDownLoadCount);
                tv2.setText("下载成功次数：" + mSuccessCount);
                tv3.setText("MD5校验失败次数：" + mMD5Count);
                tv4.setText("下载失败次数：" + mErrorCount);
//                tv5.setText("取消：" + mErrorCount);
                if (isRunning) {
                    if (curCount >= 10) {
                        reboot();
                        return;
                    }
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startDownload();
                        }
                    }, 1000);
                }
            }
        });
    }

    /**
     * 重启
     */
    private void reboot() {
        mRebootCount++;
        //保存当前数据
        SharedPreferences spf = getSharedPreferences("test", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spf.edit();
        editor.putInt("count1", mDownLoadCount);
        editor.putInt("count2", mSuccessCount);
        editor.putInt("count3", mMD5Count);
        editor.putInt("count4", mErrorCount);
        editor.putInt("count5", mRebootCount);
        editor.commit();
        try {
            Log.v("yiuhet", "root Runtime -> reboot");
            Process proc = Runtime.getRuntime().exec(new String[]{"reboot"});
//            Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","reboot now"});
            proc.waitFor();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.v("yiuhet", "root error" + "-> reboot" + ex.getMessage());
        }
    }

    private Handler mHandler = new Handler();


    public static String md5sum(String path) {
        InputStream fis = null;
        byte[] buffer = new byte[1024];
        int numRead;
        MessageDigest md5;
        try {
            fis = new FileInputStream(path);
            md5 = MessageDigest.getInstance("MD5");
            while ((numRead = fis.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            return toHexString(md5.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        // 转化成小写
        return sb.toString().toLowerCase();
    }


    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
}
