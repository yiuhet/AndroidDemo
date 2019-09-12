package com.yiuhet.androiddemo;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.AudioManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmi.widget.skeleton.Skeleton;
import com.yiuhet.androiddemo.wifi.NetReceiver;

import java.util.Random;

public class MainActivity extends Activity {
    private WifiManager wifiManager;
    TextView textView;
    private Skeleton skeleton;

    private NetReceiver netReceiver;//网络变化监听器，只有下载的时候使用
    private JobScheduler mJobScheduler;//系统定时任务使用。

    private String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
    };

    public final class AudioSource {

        private AudioSource() {}

        /** @hide */
        public final static int AUDIO_SOURCE_INVALID = -1;

        /* Do not change these values without updating their counterparts
         * in system/media/audio/include/system/audio.h!
         */

        /** Default audio source **/
        public static final int DEFAULT = 0;

        /** Microphone audio source */
        public static final int MIC = 1;

        /** Voice call uplink (Tx) audio source.
         * <p>
         * Capturing from <code>VOICE_UPLINK</code> source requires the
         * {@link android.Manifest.permission#CAPTURE_AUDIO_OUTPUT} permission.
         * This permission is reserved for use by system components and is not available to
         * third-party applications.
         * </p>
         */
        public static final int VOICE_UPLINK = 2;

        /** Voice call downlink (Rx) audio source.
         * <p>
         * Capturing from <code>VOICE_DOWNLINK</code> source requires the
         * {@link android.Manifest.permission#CAPTURE_AUDIO_OUTPUT} permission.
         * This permission is reserved for use by system components and is not available to
         * third-party applications.
         * </p>
         */
        public static final int VOICE_DOWNLINK = 3;

        /** Voice call uplink + downlink audio source
         * <p>
         * Capturing from <code>VOICE_CALL</code> source requires the
         * {@link android.Manifest.permission#CAPTURE_AUDIO_OUTPUT} permission.
         * This permission is reserved for use by system components and is not available to
         * third-party applications.
         * </p>
         */
        public static final int VOICE_CALL = 4;

        /** Microphone audio source tuned for video recording, with the same orientation
         *  as the camera if available. */
        public static final int CAMCORDER = 5;

        /** Microphone audio source tuned for voice recognition. */
        public static final int VOICE_RECOGNITION = 6;

        /** Microphone audio source tuned for voice communications such as VoIP. It
         *  will for instance take advantage of echo cancellation or automatic gain control
         *  if available.
         */
        public static final int VOICE_COMMUNICATION = 7;

        /**
         * Audio source for a submix of audio streams to be presented remotely.
         * <p>
         * An application can use this audio source to capture a mix of audio streams
         * that should be transmitted to a remote receiver such as a Wifi display.
         * While recording is active, these audio streams are redirected to the remote
         * submix instead of being played on the device speaker or headset.
         * </p><p>
         * Certain streams are excluded from the remote submix, including
         * {@link AudioManager#STREAM_RING}, {@link AudioManager#STREAM_ALARM},
         * and {@link AudioManager#STREAM_NOTIFICATION}.  These streams will continue
         * to be presented locally as usual.
         * </p><p>
         * Capturing the remote submix audio requires the
         * {@link android.Manifest.permission#CAPTURE_AUDIO_OUTPUT} permission.
         * This permission is reserved for use by system components and is not available to
         * third-party applications.
         * </p>
         */
        @RequiresPermission(android.Manifest.permission.CAPTURE_AUDIO_OUTPUT)
        public static final int REMOTE_SUBMIX = 8;

        /** Microphone audio source tuned for unprocessed (raw) sound if available, behaves like
         *  {@link #DEFAULT} otherwise. */
        public static final int UNPROCESSED = 9;

        /**
         * Audio source for capturing broadcast radio tuner output.
         * @hide
         */
        @SystemApi
        public static final int RADIO_TUNER = 1998;

        /**
         * Audio source for preemptible, low-priority software hotword detection
         * It presents the same gain and pre processing tuning as {@link #VOICE_RECOGNITION}.
         * <p>
         * An application should use this audio source when it wishes to do
         * always-on software hotword detection, while gracefully giving in to any other application
         * that might want to read from the microphone.
         * </p>
         * This is a hidden audio source.
         * @hide
         */
        @SystemApi
        @RequiresPermission(android.Manifest.permission.CAPTURE_AUDIO_HOTWORD)
        public static final int HOTWORD = 1999;
    }
    UploadScheduler uploadScheduler;


    private Surface mSurface;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionManager mMediaProjectionManager;
    private int width, height;
    private int mScreenDensity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.tv);
//        registerNetReceiver();
        mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
//        uploadScheduler = new UploadScheduler(MainActivity.this);
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context;
                Dialog alertDialog = new Dialog(MainActivity.this, R.style.style_base_dialog);
                Button button = new Button(MainActivity.this);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show8Dialog();
                    }
                });
                LinearLayout linearLayout = new LinearLayout(MainActivity.this);
                linearLayout.setBackgroundColor(Color.BLUE);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(500, 500);
                linearLayout.setLayoutParams(lp);
                button.setText("显示0.8的dialog");
                linearLayout.addView(button);
                alertDialog.setContentView(linearLayout);
                alertDialog.show();
                return;
//                startActivity(new Intent(MainActivity.this, Main2Activity.class));
//                View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popuplayout, null);
//                PopupWindow popWnd = new PopupWindow(MainActivity.this.getApplicationContext());
//                popWnd.setContentView(contentView);
//                popWnd.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
//                popWnd.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//                popWnd.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
//                int n = mJobScheduler.schedule(createJobInfo());//定时任务设置返回值，1表示成功
//                Log.e("yiuhet", "schedule " + n);
//                ping();
//                Toast.makeText(MainActivity.this, isNetworkConnected(MainActivity.this) + "", Toast.LENGTH_SHORT).show();
//                uploadScheduler.schedulerUpload();
//                uploadScheduler.schedulerUpload();
            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, isWifiConnected(MainActivity.this) + "", Toast.LENGTH_SHORT).show();
//                NetInfo netInfo = NetInfo.createNetInfoSnap(MainActivity.this);
//                uploadScheduler.cancelUpload();
            }
        });
        init();
    }

    private TextureView mRecordSurfaceView; //录屏画面

    private void init() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mRecordSurfaceView = findViewById(R.id.sv_record);
        mRecordSurfaceView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Log.e("yiuhet", "0 : onSurfaceTextureAvailable : " + width + ", " + height + "，" + Thread.currentThread().getName());
                mSurface = new Surface(surface);
                MainActivity.this.width = width;
                MainActivity.this.height = height;
                startActivityForResult(
                        mMediaProjectionManager.createScreenCaptureIntent(),
                        1);
//        }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });

    }


    private int mResultCode;
    private static final int REQUEST_MEDIA_PROJECTION = 1;
    private Intent mResultData;

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                Log.i("yiuhet", "User cancelled");
                return;
            }
            Log.i("yiuhet", "Starting screen capture");
            ThreadPoolUtils.executeInCachePool(new Runnable() {
                @Override
                public void run() {
                    mResultCode = resultCode;
                    mResultData = data;
                    setUpMediaProjection();
                    setUpVirtualDisplay();
                }
            });

        }
    }

    /**
     * 新线程中投屏
     */
    private void setUpVirtualDisplay() {
        Log.i("yiuhet", "Setting up a VirtualDisplay: " +
                width + "x" + height +
                " (" + mScreenDensity + ")");
        Log.d("yiuhet", Thread.currentThread().getName());
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                1024, 600, 1,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC | 0x8000,
                mSurface, null, null);
    }

    /**
     * 创建MediaProjection实例
     */
    private void setUpMediaProjection() {
        mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData);
    }

    void show8Dialog() {
        Dialog alertDialog = new Dialog(MainActivity.this, R.style.style_base_dialog_8);
        TextView textView = new TextView(MainActivity.this);
        textView.setText("0.8的dialog");
        LinearLayout linearLayout = new LinearLayout(MainActivity.this);
        linearLayout.setBackgroundColor(Color.GREEN);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(300, 300);
        linearLayout.setLayoutParams(lp);
        linearLayout.addView(textView);
        alertDialog.setContentView(linearLayout);
        alertDialog.show();
    }


    /**
     * 获取当前的网络这状态
     *
     * @param context
     * @return
     */
    private NetInfo getCurNetStatus(Context context) {
        NetInfo netInfo = new NetInfo();
        //获取系统的网络服务
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        //网络是否连接
        if (networkInfo != null) {
            netInfo.isConnect = networkInfo.isAvailable();
        }
        if (network != null) {
            NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(network);
            netInfo.isValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            netInfo.netInfo = networkCapabilities.toString();
        }
        return netInfo;
    }

    /**
     * 获取当前的网络状态 ：没有网络-0：WIFI网络1：4G网络-4：3G网络-3：2G网络-2
     * 自定义
     *
     * @param context
     * @return
     */
    public static int getAPNType(Context context) {
        //结果返回值
        int netType = 0;
        //获取手机所有连接管理对象
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取NetworkInfo对象
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        //NetworkInfo对象为空 则代表没有网络
        if (networkInfo == null) {
            return netType;
        }
        //否则 NetworkInfo对象不为空 则获取该networkInfo的类型
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_WIFI) {
            //WIFI
            netType = 1;
        } else if (nType == ConnectivityManager.TYPE_MOBILE) {
            int nSubType = networkInfo.getSubtype();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //3G   联通的3G为UMTS或HSDPA 电信的3G为EVDO
            if (nSubType == TelephonyManager.NETWORK_TYPE_LTE
                    && !telephonyManager.isNetworkRoaming()) {
                netType = 4;
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
                    || nSubType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || nSubType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    && !telephonyManager.isNetworkRoaming()) {
                netType = 3;
                //2G 移动和联通的2G为GPRS或EGDE，电信的2G为CDMA
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_GPRS
                    || nSubType == TelephonyManager.NETWORK_TYPE_EDGE
                    || nSubType == TelephonyManager.NETWORK_TYPE_CDMA
                    && !telephonyManager.isNetworkRoaming()) {
                netType = 2;
            } else {
                netType = 2;
            }
        }
        Log.e("Avalible", "netType：" + netType);
        return netType;
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    private void ping() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process p = runtime.exec("ping -c 3 www.baidu.com");
            int ret = p.waitFor();
            Log.e("Avalible", "Process:" + ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isWifiConnected(Context context) {
        if (!isNetworkConnected(context)) {
            return false;
        }
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = mConnectivityManager.getNetworkCapabilities(mConnectivityManager.getActiveNetwork());
        Log.i("Avalible", "NetworkCapalbilities:" + networkCapabilities.toString());
        textView.setText(networkCapabilities.toString());
        Log.i("Avalible", "NetworkCapalbilities:");
        Log.i("Avalible", "是否可用： :" + networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED));
//        if (context != null) {
//            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
//                    .getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
//                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//            if (mWiFiNetworkInfo != null) {
//                return mWiFiNetworkInfo.isAvailable();
//            }
//        }
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }


    /**
     * 释放录屏资源
     */
    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    /**
     * 停止录屏
     */
    private void stopScreenCapture() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
    }

    @Override
    protected void onDestroy() {
        stopScreenCapture();
        mRecordSurfaceView.setSurfaceTextureListener(null);
        mRecordSurfaceView = null;
        tearDownMediaProjection();
        super.onDestroy();
    }

    private JobInfo createJobInfo() {
        JobInfo.Builder builder = new JobInfo.Builder(1,
                new ComponentName(getPackageName(), UploadService.class.getName()));
        Random random = new Random();
        long time = random.nextInt(61) - 30;
        time = 15 * 1000;
        builder.setMinimumLatency(time);
        builder.setOverrideDeadline(time + 10 * 60 * 1000);
        builder.setPersisted(false);
        return builder.build();
    }

    private void registerNetReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(NetReceiver.ACTION);
        if (netReceiver == null) {
            netReceiver = new NetReceiver();
            registerReceiver(netReceiver, filter);
        }
    }
}
