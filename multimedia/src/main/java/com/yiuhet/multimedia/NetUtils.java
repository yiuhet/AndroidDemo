package com.yiuhet.multimedia;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.yiuhet.multimedia.tuple.Tuple4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.telephony.TelephonyManager.NETWORK_TYPE_GSM;


/**
 * 判断网络状态工具类
 *
 * @author longtao.li
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
     * 转换成其他样式的网络类型
     * <p>
     * 1 表示2g，2 表示 3g， 3 表示4g，4 表示wifi，5 表示有线
     *
     * @param networkType
     * @return
     */
    public static String convertType(NetworkType networkType) {
        switch (networkType) {
            case NETWORK_2G:
                return "1";
            case NETWORK_3G:
                return "2";
            case NETWORK_4G:
                return "3";
            case NETWORK_WIFI:
                return "4";
            case NETWORK_ETHERNET:
                return "5";
            default:
                return "0";
        }
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
        Network network = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            network = cm.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(network);
                //这里返回的是网络是否真实可用
                return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            }
        } else {
            //小于安卓6.0的直接返回true
            return true;
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
        if (info != null && info.isConnected()) {
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

    public static NetworkType getNetworkType(Context context, NetworkInfo info) {
        NetworkType netType = NetworkType.NETWORK_NULL;
        if (info != null && info.isConnected()) {
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
        Network network = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            network = cm.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(network);
                //这里返回的是网络是否真实可用
                return networkCapabilities.toString();
            }
        }
        //小于安卓6.0的直接返回空
        return "android version < 23, NetworkInfo is null";
    }

    /**
     * 判断当前网络是否是wifi网络
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 判断当前网络是否是3G网络
     */
    public static boolean is3G(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * 检查外网是否联通
     */
    public static boolean isExternalNetConnected() {
        try {
            final Runtime runtime = Runtime.getRuntime();
            int exitValue1;
            //使用公共域名解析服务DNS:114.114.114.114
            //执行Ping命令 格式为  ping -c 1 -w 2
            //其中参数-c 1是指ping的次数为1次，-w是指执行的最后期限,单位为秒，也就是执行的时间为2秒，超过2秒则失败.
            Process ipProcess = runtime.exec("ping -c 2 -w 2 114.114.114.114");
            exitValue1 = ipProcess.waitFor();
            InputStream input = ipProcess.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append("   " + line);
            }
            return (exitValue1 == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检查网络是否可用
     */
    public static boolean isNetworkUsable(Context context) {
        if (!isNetworkConnected(context)) {
            return false;
        }
//        if (isWifi(context) && !isExternalNetConnected()) {
//            return false;
//        }
        return true;
    }

    /**
     * 获取网络信息
     *
     * @param context
     * @return
     */
    public static Tuple4<Boolean, Boolean, NetworkType, String> getNetInfo(Context context) {
        StringBuilder netInfo = new StringBuilder(); //网络状态信息
        boolean isValidated = false; //网络是否联通外网
        boolean isConnect = false; //网络是否连接
        NetworkType netType = NetworkType.NETWORK_NULL; //网络类型
        if (context == null) {
            netInfo.append("context is null!");
            return new Tuple4(isConnect, isValidated, netType, netInfo.toString());
        }
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = cm.getActiveNetworkInfo();
        //没有连接的网络信息
        if (mNetworkInfo != null) {
            //网络连接状态
            isConnect = mNetworkInfo.isConnected();
            if (!mNetworkInfo.isConnected()) {
                netInfo.append("NetworkInfo.isConnected() return false!");
            }
        } else {
            netInfo.append("cm.getActiveNetworkInfo() return null!");
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Network mNetwork = cm.getActiveNetwork();
            if (mNetwork != null) {
                NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(mNetwork);
                //网络快照
                netInfo.append(networkCapabilities.toString());
                //这里返回的是网络是否真实可用
                isValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            } else {
                netInfo.append("mNetwork is null!");
            }
        } else {
            //低版本默认是联通状态
            isValidated = true;
            netInfo.append("android version < 23, NetworkInfo is null!");
        }
        //网络类型
        netType = getNetworkType(context, mNetworkInfo);
        return new Tuple4(isConnect, isValidated, netType, netInfo.toString());
    }

}
