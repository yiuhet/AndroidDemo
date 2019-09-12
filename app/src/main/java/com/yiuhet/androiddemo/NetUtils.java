package com.yiuhet.androiddemo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import static android.telephony.TelephonyManager.NETWORK_TYPE_GSM;

/**
 * Created by yiuhet on 2019/7/8.
 * <p>
 * 网络工具类
 */
public class NetUtils {

    /**
     * 网络类型
     */
    public enum NetworkType {
        NETWORK_ETHERNET, //以太网
        NETWORK_WIFI, //wifi
        NETWORK_2G, //2g
        NETWORK_3G, //3g
        NETWORK_4G, //4g
        NETWORK_OTHER, //其他网络
        NETWORK_NULL //无网络连接
    }

    /**
     * 获取当前的网络信息
     *
     * @param context
     * @return
     */
    private NetworkInfo getActiveNetworkInfo(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo();
        }
        return null;
    }

    /**
     * 网络是否连接 (isConnected)
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = cm.getActiveNetworkInfo();
            return mNetworkInfo != null && mNetworkInfo.isConnected();
        }
        return false;
    }

    /**
     * 判断网络是否联通(能连接外网)
     *
     * @param context
     * @return
     */
    public static boolean isNetworkValidated(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        if (network != null) {
            NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(network);
            //这里返回的是网络是否真实可用
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        }
        return false;
    }

    /**
     * 获取网络信息
     *
     * @param context
     * @return
     */
    public static NetworkType getNetworkType(Context context) {
        NetworkType netType = NetworkType.NETWORK_NULL;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                netType = NetworkType.NETWORK_WIFI;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {
                    case NETWORK_TYPE_GSM:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        netType = NetworkType.NETWORK_2G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        netType = NetworkType.NETWORK_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_IWLAN:
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        netType = NetworkType.NETWORK_4G;
                        break;
                    default:
                        String subtypeName = info.getSubtypeName();
                        if (subtypeName.equalsIgnoreCase("TD-SCDMA")
                                || subtypeName.equalsIgnoreCase("WCDMA")
                                || subtypeName.equalsIgnoreCase("CDMA2000")) {
                            netType = NetworkType.NETWORK_3G;
                        } else {
                            netType = NetworkType.NETWORK_OTHER;
                        }
                        break;
                }
            } else if (info.getType() == ConnectivityManager.TYPE_ETHERNET) {
                netType = NetworkType.NETWORK_ETHERNET;
            } else {
                netType = NetworkType.NETWORK_OTHER;
            }
        }
        return netType;
    }

    /**
     * 获取网络能力
     *
     * @param context
     * @return
     */
    public static String getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        if (network != null) {
            NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(network);
            //这里返回的是网络是否真实可用
            return networkCapabilities.toString();
        }
        return "";
    }


}
