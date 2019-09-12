package com.yiuhet.multimedia;

import android.app.Application;

/**
 * Created by yiuhet on 2019/8/21.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init();
    }
}
