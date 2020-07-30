package com.example.patrolinspection.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import static android.telephony.CellSignalStrength.SIGNAL_STRENGTH_GOOD;
import static android.telephony.CellSignalStrength.SIGNAL_STRENGTH_GREAT;
import static android.telephony.CellSignalStrength.SIGNAL_STRENGTH_MODERATE;
import static android.telephony.CellSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
import static android.telephony.CellSignalStrength.SIGNAL_STRENGTH_POOR;

public  class NetworkUtil
{
    public static final int NETWORK_NONE = 0;
    public static final int NETWORK_WIFI = 1;
    public static final int NETWORK_2G = 2;
    public static final int NETWORK_3G = 3;
    public static final int NETWORK_4G = 4;
    public static final int NETWORK_MOBILE = 5;

    /**
     * 获得网络类型
     *
     * @param context
     * @return
     */
    public static int getNetworkState(Context context) {
        //获得 ConnectivityManager 对象
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == cm) {
            //对象为空为无网络
            return NETWORK_NONE;
        }
        //获取当前活跃的网络数据信息，该方法需要申请系统 ACCESS_NETWORK_STATE 权限
        NetworkInfo activeNetInfo = cm.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return NETWORK_NONE;
        }
        //查找 WI-FI 类型的网络信息
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null) {
            //得到网络状态信息
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state) {
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return NETWORK_WIFI;
                }
            }
        }
        //获取 TelephonyManager 对象
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //获得网络类型
        int networkType = telephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_2G;
            // 3G网络
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NETWORK_3G;
            // 4G网络
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_4G;
            default:
                return NETWORK_MOBILE;
        }
    }

    public static String getNetworkStateString(Context context)
    {
        int state = getNetworkState(context);
        switch (state){
            case NETWORK_NONE:
                return "无网络";
            case NETWORK_WIFI:
                return "WIFI";
            case NETWORK_2G:
                return "2G";
            case NETWORK_3G:
                return  "3G";
            case NETWORK_4G:
                Log.d("getNetworkState", "NETWORK_4G");
                return "4G";
        }
        return "";
    }

    /*
	1.大于-85时候，等级为SIGNAL_STRENGTH_GREAT，即为4
	2.大于-95时候，等级为SIGNAL_STRENGTH_GOOD，即为3
	3.大于-105时候，等级为SIGNAL_STRENGTH_MODERATE，即为2
	4.大于-115时候，等级为SIGNAL_STRENGTH_POOR，即为1
	5.大于-140时候，等级为SIGNAL_STRENGTH_NONE_OR_UNKNOWN，即为0
	6.大于-44时候，等级为-1
*/
    public static int getMobileSignalLevel(int mRsrp) {
        int levelRsrp = -1;
        if (mRsrp == Integer.MAX_VALUE) levelRsrp = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        else if (mRsrp >= -95) levelRsrp = SIGNAL_STRENGTH_GREAT;
        else if (mRsrp >= -105) levelRsrp = SIGNAL_STRENGTH_GOOD;
        else if (mRsrp >= -115) levelRsrp = SIGNAL_STRENGTH_MODERATE;
        else levelRsrp = SIGNAL_STRENGTH_POOR;
        return levelRsrp;
    }
}
