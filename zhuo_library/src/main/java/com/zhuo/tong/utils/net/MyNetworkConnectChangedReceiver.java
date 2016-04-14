package com.zhuo.tong.utils.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

import com.zhuo.tong.utils.MyTestLog;


/*
1.XML中声明

        <receiver android:name=".NetworkConnectChangedReceiver" >
        <intent-filter>
        <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
        <action android:name="android.net.wifi.WIFI_STATE_CHANGED" />
        <action android:name="android.net.wifi.STATE_CHANGE" />
        </intent-filter>
        </receiver>



        2.代码中注册

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new NetworkConnectChangedReceiver(), filter);
*/

public class MyNetworkConnectChangedReceiver extends BroadcastReceiver {
    public MyNetworkConnectChangedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        MyTestLog.info(context.getPackageName());
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 这个监听wifi的打开与关闭，与wifi的连接无关
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            MyTestLog.info("wifiState:" + wifiState);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    break;
            }
        }
        // 这个监听wifi的连接状态即是否连上了一个有效无线路由，当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
        // 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，当然刚打开wifi肯定还没有连接到有效的无线
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                NetworkInfo.State state = networkInfo.getState();
                boolean isConnected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态
                MyTestLog.info("isConnected:" + isConnected);
                if (isConnected) {
                } else {

                }
            }
        }
        // 这个监听网络连接的设置，包括wifi和移动数据的打开和关闭。.
        // 最好用的还是这个监听。wifi如果打开，关闭，以及连接上可用的连接都会接到监听。见log
        // 这个广播的最大弊端是比上边两个广播的反应要慢，如果只是要监听wifi，我觉得还是用上边两个配合比较合适
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
           NetworkInfo gprs = manager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            	NetworkInfo wifi = manager
                   .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            MyTestLog.info("网络状态改变:wifi联通:" + wifi.isConnected() + "\t3g联通:" + gprs.isConnected());
                        NetworkInfo info = intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (info != null) {
                MyTestLog.info("getTypeName:" + info.getTypeName());
                MyTestLog.info("getSubtypeName:" + info.getSubtypeName());
                MyTestLog.info("getState:" + info.getState());
                MyTestLog.info("getDetailedState:"
                        + info.getDetailedState().name());
                MyTestLog.info("getExtraInfo:" + info.getExtraInfo());
                MyTestLog.info("getType" + info.getType());

                if (NetworkInfo.State.CONNECTED == info.getState()) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    }
                } else if (NetworkInfo.State.DISCONNECTING == info.getState()) {

                    }
                }

        }
    }
}
