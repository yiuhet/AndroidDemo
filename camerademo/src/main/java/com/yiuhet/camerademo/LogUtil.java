package com.yiuhet.camerademo;

import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 打印Log.点击可以直接连接到打印log所在行
 *
 * @author YC
 * 2016年7月27日
 */
public class LogUtil {

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;
    public static int LEVEL = VERBOSE;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.getDefault());

    private static void setLevel(int Level) {
        LEVEL = Level;
    }

    public static boolean WRITE_TO_FILE = true;
    private static boolean logStarted = false;
    private static String logType = null;

    public static void v(String TAG, String msg) {
        if (LEVEL <= VERBOSE && !TextUtils.isEmpty(msg)) {
            MyLog(VERBOSE, TAG, msg);
        }
    }

    public static void d(String TAG, String msg) {
        if (LEVEL <= DEBUG && !TextUtils.isEmpty(msg)) {
            MyLog(DEBUG, TAG, msg);
        }
    }

    public static void i(String TAG, String msg) {
        if (LEVEL <= INFO && !TextUtils.isEmpty(msg)) {
            MyLog(INFO, TAG, msg);
        }
    }

    public static void w(String TAG, String msg) {
        if (LEVEL <= WARN && !TextUtils.isEmpty(msg)) {
            MyLog(WARN, TAG, msg);
        }
    }

    public static void e(String TAG, String msg) {
        if (LEVEL <= ERROR && !TextUtils.isEmpty(msg)) {
            MyLog(ERROR, TAG, msg);
        }
    }

    private static void MyLog(int type, String TAG, String msg) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        int index = 4;
        String className = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();

        methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[ (").append(className).append(":").append(lineNumber).append(")#").append(methodName).append(" ] ");
        stringBuilder.append(msg);
        String logStr = stringBuilder.toString();
        switch (type) {
            case VERBOSE:
                Log.v(TAG, logStr);
                if (logStarted && WRITE_TO_FILE) {
                }
                break;
            case DEBUG:
                Log.d(TAG, logStr);
                if (logStarted && WRITE_TO_FILE) {
                }
                break;
            case INFO:
                Log.i(TAG, logStr);
                if (logStarted && WRITE_TO_FILE) {
                }
                break;
            case WARN:
                Log.w(TAG, logStr);
                if (logStarted && WRITE_TO_FILE) {
                }
                break;
            case ERROR:
                Log.e(TAG, logStr);
                if (logStarted && WRITE_TO_FILE) {
                }
                break;
            default:
                break;
        }

    }

    public static synchronized void startLogger(int level, boolean write, Context context) {
        if (logStarted && LEVEL == level) {//已开始且log类型未改变
            return;
        }
        setLevel(level);
        stopLogger();
        WRITE_TO_FILE = write;
        if (WRITE_TO_FILE) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS", Locale.getDefault());
            String fileName = sdf.format(new Date());
        }
        logStarted = true;
    }

    public static synchronized void stopLogger() {
        if (logStarted) {
            if (WRITE_TO_FILE) {
            }
            logStarted = false;
        }
    }

    private static String getLogString(String level, String tag, String message) {
        return "[Pid=" + Process.myPid() + ",Tid=" + Thread.currentThread().getId() + "] " + level + " " + getTime() + "  " + tag + "  " + message;
    }

    private static String getTime() {
        return SDF.format(new Date());
    }
}
