package com.yiuhet.androiddemo;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static com.yiuhet.androiddemo.UploadScheduler.JOB_UPLOAD_PAYMENTAIL;

/**
 * Created by yiuhet on 2019/7/5.
 * <p>
 * 异常订单定时上传的任务 0-4点
 */
public class UploadJob extends JobService {

    private JobParameters mJobParameters;
    private NetReceiver mNetReceiver;//网络变化监听器
    private boolean isRunning; //任务是否正在进行
    private volatile boolean isWaitingNet; //是否正在等待网络连接
    private Semaphore mSemaphore = new Semaphore(1); //等待网络时阻塞线程的信号量
    private WifiManager mWifiManager;

    //网络变化回调
    private NetReceiver.NetCallback mNetCallback = new NetReceiver.NetCallback() {
        @Override
        public void netChange() {
            //如果任务正在等待网络则重新尝试执行判断网络
            if (isWaitingNet && mJobParameters != null && isUpload()) {
                Log.i("yiuhet", "callback : net is validated");
                mSemaphore.release();
                performUploadTask(mJobParameters);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("yiuhet", "UploadService Create");
        //注册网络监听
        registerNetReceiver();
    }

    /**
     * 注册网络监听广播
     */
    private void registerNetReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(NetReceiver.ACTION);
        if (mNetReceiver == null) {
            mNetReceiver = new NetReceiver();
            registerReceiver(mNetReceiver, filter);
            mNetReceiver.setCallback(mNetCallback);
        }
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.e(UploadJob.class.getSimpleName(), "start job " + params.getJobId());
        switch (params.getJobId()) {
            case JOB_UPLOAD_PAYMENTAIL:
                mJobParameters = params;
                performUploadTask(params);
                break;
        }
        //返回true，保持job仍在活动状态
        return true;
    }

    /**
     * 执行上传任务
     */
    private void performUploadTask(JobParameters params) {
        Log.i("yiuhet", "performUploadTask");
        isRunning = true;
        //step1 判断网络，若没网络则自动开启wifi，并监听网络状态
        if (NetUtils.isNetworkConnected(this)) {
            Log.i("yiuhet", "net is connect");
            //step2 判断当前网络是否是WIFI/以太网 且 联通，是则上传、否则放弃任务
            if (isUpload()) {
                Log.i("yiuhet", "net is validated, start upload task");
                startUpload(params);
            } else {
                Log.i("yiuhet", "net is not validated, give up task");
                //结束任务
                jobFinished(params, false);
                isRunning = false;
                isWaitingNet = false;
            }
        } else {
            resloveNetNotConnect();
        }
    }

    /**
     * 确定网络状况是否可以上传
     *
     * @return
     */
    private boolean isUpload() {
        NetUtils.NetworkType networkType = NetUtils.getNetworkType(this);
        return NetUtils.isNetworkValidated(this) &&
                networkType == NetUtils.NetworkType.NETWORK_WIFI
                || networkType == NetUtils.NetworkType.NETWORK_ETHERNET;
    }

    /**
     * 正式开始上传（新线程），完成后调用jobFinished(params, false);
     */
    private void startUpload(JobParameters params) {

    }

    /**
     * 解决没网的情况
     * 打开wifi 轮询网络 2分钟后仍没网则放弃任务
     */
    private void resloveNetNotConnect() {
        Log.i("yiuhet", "net is not connect, reslove it");
        try {
            mSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
            mSemaphore.release();
            //结束任务
            jobFinished(mJobParameters, false);
            isRunning = false;
            isWaitingNet = false;
        }
        isWaitingNet = true;
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean isSuccess = mWifiManager.setWifiEnabled(true);
        Log.i("yiuhet", "setWifiEnabled, isSuccess: " + isSuccess);
        try {
            //阻塞两分钟，若网络仍未连接，则放弃任务
            if (mSemaphore.tryAcquire(2, TimeUnit.MINUTES)) {
                Log.e("yiuhet", "wait success,net is validated");
                isWaitingNet = false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e("yiuhet", "after wait 2 minutes,net is not validated, give up task");
            mSemaphore.release();
            //结束任务
            jobFinished(mJobParameters, false);
            isRunning = false;
            isWaitingNet = false;
        }
        ;

    }


    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("yiuhet", "onStopJob");
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("yiuhet", "UploadService Destroy");
        //注销网络监听
        if (mNetReceiver != null) {
            unregisterReceiver(mNetReceiver);
            mNetReceiver = null;
        }
    }


}
