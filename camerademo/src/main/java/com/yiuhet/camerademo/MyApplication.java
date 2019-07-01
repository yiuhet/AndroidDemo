package com.yiuhet.camerademo;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by yiuhet on 2019/6/27.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
