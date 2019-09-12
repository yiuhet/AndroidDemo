package com.yiuhet.multimedia;

import android.content.Context;
import android.util.Log;

import com.yiuhet.multimedia.tuple.Tuple4;

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
    public NetUtils.NetworkType netType;
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

    public NetUtils.NetworkType getNetType() {
        return netType;
    }

    public void setNetType(NetUtils.NetworkType netType) {
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


    public static String createNetInfoSnap(Context context) {
        if (context == null) {
            return null;
        }
        long start = System.currentTimeMillis();
        Tuple4 net = NetUtils.getNetInfo(context);
        NetInfo netInfo = new NetInfo();
        netInfo.isConnect = (boolean) net.a;
        netInfo.isValidated = (boolean) net.b;
        netInfo.netType = (NetUtils.NetworkType) net.c;
        netInfo.netInfo = (String) net.d;
//        NetInfo netInfo = new NetInfo();
//        netInfo.isConnect = NetUtils.isNetworkConnected(context);
//        netInfo.isValidated = NetUtils.isNetworkValidated(context);
//        netInfo.netType = NetUtils.getNetworkType(context);
//        netInfo.netInfo = NetUtils.getNetworkInfo(context);
        long end = System.currentTimeMillis();
        Log.i("yiuhet", "create netInfo snap: " + netInfo.toString());
        Log.i("yiuhet", "use time: " + (end - start) + "ms");
        return "";
    }
}
