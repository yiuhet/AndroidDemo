package com.yiuhet.androiddemo;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import static com.yiuhet.androiddemo.UploadScheduler.JOB_UPLOAD_PAYMENTAIL;

/**
 * Created by yiuhet on 2019/7/8.
 * <p>
 * 定时上传异常订单的任务
 */
public class UploadService extends JobService {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("yiuhet", "UploadService Create");
        //注册网络监听
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.e(UploadService.class.getSimpleName(), "start job " + params.getJobId());
        switch (params.getJobId()) {
            case JOB_UPLOAD_PAYMENTAIL:
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
        //step1 判断网络，若没网络则自动开启wifi，并轮询？监听网络状态
        if (NetUtils.isNetworkConnected(UploadService.this)) {
            Log.i("yiuhet", "net is connect");
            //step2 判断网络是否联通，是则上传、否则放弃任务
            if (NetUtils.isNetworkValidated(UploadService.this)) {
                Log.i("yiuhet", "net is validated, start upload task");
                startUpload();
            } else {
                Log.i("yiuhet", "net is not validated, give up task");
                jobFinished(params, false);
            }
        } else {
            resloveNetNotConnect();
        }
    }

    /**
     * 正式开始上传
     */
    private void startUpload() {

    }

    /**
     * 解决没网的情况
     * 打开wifi 轮询网络 2分钟后仍没网则放弃任务
     */
    private void resloveNetNotConnect() {
        Log.i("yiuhet", "net is not connect, reslove it");
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
    }
}
