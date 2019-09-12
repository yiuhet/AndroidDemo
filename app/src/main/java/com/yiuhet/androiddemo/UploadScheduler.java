package com.yiuhet.androiddemo;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;


/**
 * Created by yiuhet on 2019/7/8.
 * <p>
 * 上传任务的调度器
 */
public class UploadScheduler {
    //上传异常订单的任务ID(paydetail serialVersionUID的前几位，确保唯一)
    public final static int JOB_UPLOAD_PAYMENTAIL = 427035462;

    private JobScheduler mJobScheduler;//系统定时任务使用。

    public UploadScheduler(Context context) {
        mJobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    /**
     * 设置上传任务
     *
     * @return
     */
    public int schedulerUpload() {
        int n = mJobScheduler.schedule(createUploadJob());//定时任务设置返回值，1表示成功
        Log.i("yiuhet", "scheduler upload, status : " + n);
        return n;
    }

    /**
     * 取消上传任务
     */
    public void cancelUpload() {
        mJobScheduler.cancel(JOB_UPLOAD_PAYMENTAIL);
        Log.i("yiuhet", "cancel : " + JOB_UPLOAD_PAYMENTAIL);
    }

    /**
     * 创建上传任务
     *
     * @return
     */
    private JobInfo createUploadJob() {
        JobInfo.Builder builder = new JobInfo.Builder(JOB_UPLOAD_PAYMENTAIL,
                new ComponentName("com.yiuhet.androiddemo", UploadJob.class.getName()));
        Random random = new Random();
        //设置周期为24小时
//        builder.setPeriodic(24 * 60 * 60 * 1000);
        //触发区间次日凌晨[0,4]点，创建个0-240分钟的随机数
        long delay = random.nextInt(241) * 60 * 1000;
        //距离次日凌晨还剩余的时间
        long time = getTomorrowZero();
        Log.i("yiuhet", "job perform time : " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(time + delay)));
        //设置任务的执行时间为次日的凌晨0点 + [0,240]min
        builder.setMinimumLatency(10 * 1000);
        //设置任务的最晚执行时间 凌晨4点
        builder.setOverrideDeadline(20 * 1000);
        //重启后仍保持定时任务 ps：需要RECEIVE_BOOT_COMPLETED权限
        builder.setPersisted(true);
        return builder.build();
    }

    /**
     * 获取次日0点的时间
     *
     * @return
     */
    private long getTomorrowZero() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime().getTime() + 1;
    }
}
