package com.yiuhet.widget;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProxyTNHandler extends Handler {
    private static final String TAG = ProxyTNHandler.class.getSimpleName();
    private Object tnObject;
    private Method handleShowMethod;
    private Method handleHideMethod;

    ProxyTNHandler(Object tnObject) {
        this.tnObject = tnObject;
        try {
            this.handleShowMethod = tnObject.getClass().getDeclaredMethod("handleShow", IBinder.class);
            this.handleHideMethod = tnObject.getClass().getDeclaredMethod("handleHide");
            this.handleHideMethod.setAccessible(true);
            this.handleShowMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 0: {
                //SHOW
                IBinder token = (IBinder) msg.obj;
                if (handleShowMethod != null) {
                    try {
                        handleShowMethod.invoke(tnObject, token);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (WindowManager.BadTokenException e) {
                        //显示Toast时添加BadTokenException异常捕获
                        e.printStackTrace();
                    }
                }
                break;
            }

            case 1: {
                //HIDE
                if (handleHideMethod != null) {
                    try {
                        handleHideMethod.invoke(tnObject);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case 2: {
                //CANCEL
                if (handleHideMethod != null) {
                    try {
                        handleHideMethod.invoke(tnObject);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }

        }
        super.handleMessage(msg);
    }
}
