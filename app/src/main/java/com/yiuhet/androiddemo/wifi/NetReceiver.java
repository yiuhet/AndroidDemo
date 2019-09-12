package com.yiuhet.androiddemo.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

public class NetReceiver extends BroadcastReceiver {
    public interface NetCallback {
        public void wifiConnect();

        public void mobileConnect();

        public void netDisconnect();
    }

    public static final String ACTION = ConnectivityManager.CONNECTIVITY_ACTION;
    private NetCallback callback;

    public void setCallback(NetCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("NetReceiver", "onReceive  " + intent.getAction());
        Bundle bundle = intent.getExtras();
        for (String s : bundle.keySet()) {
            Log.d("NetReceiver", "key: " + s + " ;value: " + bundle.get(s));
        }

    }
}
