package com.yiuhet.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("yiuhet", "receive boot");
        //TODO 开机启动广播
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
            return;
        Log.e("yiuhet", "start activity");
        Intent intent1 = new Intent();
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.setClass(context, MainActivity.class);
        context.startActivity(intent1);
    }
}
