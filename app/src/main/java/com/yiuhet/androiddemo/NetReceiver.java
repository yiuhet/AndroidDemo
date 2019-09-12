package com.yiuhet.androiddemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by yiuhet on 2019/7/5.
 * <p>
 * 网络监听广播
 */
public class NetReceiver extends BroadcastReceiver {
    public interface NetCallback {
        void netChange();
    }

    /**
     * 网络变化的Action
     */
    public static final String ACTION = ConnectivityManager.CONNECTIVITY_ACTION;
    private NetCallback mCallback;

    public void setCallback(NetCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("yiuhet", "onReceive  " + intent.getAction());
        Bundle bundle = intent.getExtras();
        for (String s : bundle.keySet()) {
            Log.d("NetReceiver", "key: " + s + " ;value: " + bundle.get(s));
        }
        if (mCallback == null)
            return;
        //网络变化
        mCallback.netChange();
    }
}
