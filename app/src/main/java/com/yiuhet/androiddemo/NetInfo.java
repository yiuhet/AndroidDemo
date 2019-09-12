package com.yiuhet.androiddemo;

import android.content.Context;
import android.util.Log;

/**
 * Created by yiuhet on 2019/7/8.
 * <p>
 * 网络的一些状态
 */
public class NetInfo {
    /**
     * 网络是否连接
     */
    public boolean isConnect;
    /**
     * 网络是否联调（通外网）
     */
    public boolean isValidated;
    /**
     * 网络类型
     * 以太网 WIFI 2G 3G 4G NUll（没网络） Other（蓝牙等其他方式）
     */
    public String netType;
    /**
     * 原始信息，网络能力
     * [ Transports: WIFI
     * Capabilities: INTERNET&NOT_RESTRICTED&TRUSTED&NOT_VPN&VALIDATED&FOREGROUND
     * LinkUpBandwidth>=1048576Kbps
     * LinkDnBandwidth>=1048576Kbps
     * SignalStrength: -21]
     */
    public String netInfo;

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    public boolean isValidated() {
        return isValidated;
    }

    public void setValidated(boolean validated) {
        isValidated = validated;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getNetInfo() {
        return netInfo;
    }

    public void setNetInfo(String netInfo) {
        this.netInfo = netInfo;
    }

    @Override
    public String toString() {
        return "NetInfo{" +
                "isConnect=" + isConnect +
                ", isValidated=" + isValidated +
                ", netType='" + netType + '\'' +
                ", netInfo='" + netInfo + '\'' +
                '}';
    }

    /**
     * 生成当前网络的信息实例
     *
     * @return
     */
    public static NetInfo createNetInfoSnap(Context context) {
        if (context == null) {
            return null;
        }
        long start = System.currentTimeMillis();
        NetInfo netInfo = new NetInfo();
        netInfo.isConnect = NetUtils.isNetworkConnected(context);
        netInfo.isValidated = NetUtils.isNetworkValidated(context);
        netInfo.netType = NetUtils.getNetworkType(context).name();
        netInfo.netInfo = NetUtils.getNetworkInfo(context);
        long end = System.currentTimeMillis();
        Log.i("yiuhet", "create netInfo snap: " + netInfo.toString());
        Log.i("yiuhet", "use time: " + (end - start) + "ms");
        return netInfo;
    }
}
