package com.yiuhet.widget;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * @author :zdy
 * @Date: 2018/10/10
 */
public class MToast {
    private static Toast mToast;
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private static String preMsg;
    private static boolean isShowing;


    private static Runnable r = new Runnable() {
        @Override
        public void run() {
            if (mToast != null) {
                mToast.cancel();
            }
            isShowing = false;
            preMsg = null;
        }
    };

    public static void showToast(final Context context, final String text, final int duration) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    showToastOnUI(context, text, duration);
                }
            });
        } else {
            showToastOnUI(context, text, duration);
        }
    }

    private static void showToastOnUI(Context context, String text, int duration) {
        if (mToast != null) {
            mToast.setText(text);
        } else {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            int sdkInt = Build.VERSION.SDK_INT;
            if (sdkInt >= Build.VERSION_CODES.N && sdkInt < Build.VERSION_CODES.O) {
                reflectTNHandler(mToast);
            }
            mToast.setGravity(Gravity.CENTER, 0, 0);
        }
        if (!isShowing || !TextUtils.equals(preMsg, text)) {
            mToast.show();
            isShowing = true;
            preMsg = text;
            mHandler.postDelayed(r, 2000);
        }
    }

    public static void showToast(Context context, int resId, int duration) {
        showToast(context, context.getResources().getString(resId), duration);
    }

    public static void resetToast() {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = null;
    }

    private static void reflectTNHandler(Toast toast) {
        try {
            Field tNField = toast.getClass().getDeclaredField("mTN");
            if (tNField == null) {
                return;
            }
            tNField.setAccessible(true);
            Object TN = tNField.get(toast);
            if (TN == null) {
                return;
            }
            Field handlerField = TN.getClass().getDeclaredField("mHandler");
            if (handlerField == null) {
                return;
            }
            handlerField.setAccessible(true);
            handlerField.set(TN, new ProxyTNHandler(TN));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
