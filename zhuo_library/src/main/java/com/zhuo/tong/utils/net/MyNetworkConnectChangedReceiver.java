package com.zhuo.tong.utils.net;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

import com.zhuo.tong.application.MyApplication;
import com.zhuo.tong.constant.DevelopState;
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
        listener = adapter;
    }

    public static int con;
    public static int wifiCon;
    public static int netCon;
    public static int conCon;

    public static boolean dynamic;


/*  小米2测试:开启wifi和连接路由的过程，手动连接路由和自动连接路由一致。
    连接路由会触发4次net广播，最后一次才连上，前三次都是isConnected:false；可能是无线网四次握手包产生的广播？之后是一次con广播，不管该路由器是否能正常上网，只要联通路由就触发该广播。
    如果密码不正确会触发一次net广播isConnected:false; 如果密码正确但是ip地址不正确时会触发4次net广播，最后一次才连上，前三次都是isConnected:false;但不会触发con广播。
    联想a366t 2.3.6测试结果：
机器存在一些问题，所以仅供参考，可能并不准确。
连接路由只触发一次net广播
    04-15 10:18:53.584 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:47): 广播总数:0
            04-15 10:18:53.584 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:49): wifi广播总数:0
            04-15 10:18:53.584 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:51): wifiState:2
            04-15 10:18:54.966 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:47): 广播总数:1
            04-15 10:18:54.966 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:49): wifi广播总数:1
            04-15 10:18:54.966 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:51): wifiState:3
            04-15 10:19:05.557 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:47): 广播总数:2
            04-15 10:19:05.557 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:68): net广播总数:0
            04-15 10:19:05.557 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:75): isConnected:false
            04-15 10:19:05.657 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:47): 广播总数:3
            04-15 10:19:05.667 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:68): net广播总数:1
            04-15 10:19:05.667 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:75): isConnected:false
            04-15 10:19:07.880 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:47): 广播总数:4
            04-15 10:19:07.880 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:68): net广播总数:2
            04-15 10:19:07.890 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:75): isConnected:false
            04-15 10:19:07.910 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:47): 广播总数:5
            04-15 10:19:07.910 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:68): net广播总数:3
            04-15 10:19:07.920 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:75): isConnected:true
            04-15 10:19:11.834 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:47): 广播总数:6
            04-15 10:19:11.844 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:86): con广播总数:0
            04-15 10:19:11.864 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:93): 网络状态改变:wifi联通:true	3g联通:false
            04-15 10:19:11.864 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:97): getTypeName:WIFI
    04-15 10:19:11.864 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:98): getSubtypeName:
            04-15 10:19:11.864 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:99): getState:CONNECTED
    04-15 10:19:11.864 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:100): getDetailedState:CONNECTED
    04-15 10:19:11.874 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:102): getExtraInfo:null
            04-15 10:19:11.874 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:103): getType:1
*/
/*
    小米2测试:关闭路由会触发1次net广播，isConnected:false；1次con广播
    连着路由的时候关闭wifi会触发1次net广播，isConnected:false；2次wifi广播; 1次con广播
    a366t:关闭wifi，可能是机器wifi模块响应慢，wifi已关闭广播排最后

    04-15 10:22:58.866 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:47): 广播总数:7
            04-15 10:22:58.866 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:68): net广播总数:4
            04-15 10:22:58.866 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:75): isConnected:false
            04-15 10:22:59.517 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:47): 广播总数:8
            04-15 10:22:59.517 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:49): wifi广播总数:2
            04-15 10:22:59.517 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:51): wifiState:0
            04-15 10:22:59.747 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:47): 广播总数:9
            04-15 10:22:59.747 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:49): wifi广播总数:3
            04-15 10:22:59.747 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:51): wifiState:1
            04-15 10:23:01.389 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:47): 广播总数:10
            04-15 10:23:01.389 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:86): con广播总数:1
            04-15 10:23:01.409 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:93): 网络状态改变:wifi联通:false	3g联通:false
            04-15 10:23:01.409 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:97): getTypeName:WIFI
    04-15 10:23:01.409 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:98): getSubtypeName:
            04-15 10:23:01.409 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:99): getState:DISCONNECTED
    04-15 10:23:01.419 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:100): getDetailedState:DISCONNECTED
    04-15 10:23:01.419 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:102): getExtraInfo:null
            04-15 10:23:01.429 15534-15534/com.zhuo.tong.wifi.adb E/onReceive(MyNetworkConnectChangedReceiver.java:103): getType:1
*/

    @Override
    public void onReceive(Context context, Intent intent) {
//        MyTestLog.info(this.toString()+(adapter==null));
        if (dynamic && !(context instanceof Activity))
            return;
        this.context = context;
        MyTestLog.info("广播总数:" + con++ + "调用者:" + context.getClass().getName());
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 这个监听wifi的打开与关闭，与wifi的连接无关
            MyTestLog.info("wifi广播总数:" + wifiCon++);
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            MyTestLog.info("wifiState:" + wifiState);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    listener.onWIFI_DISABLED();
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    listener.onWIFI_DISABLING();
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    listener.onWIFI_ENABLED();
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    listener.onWIFI_ENABLING();
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    listener.onWIFI_UNKNOWN();
                    break;
            }
        }
        //他人说法
        // 这个监听wifi的连接状态即是否连上了一个有效无线路由，当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
        // 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，当然刚打开wifi肯定还没有连接到有效的无线

        //我的测试结果：如果没有自动连接的路由器，那么WifiManager.WIFI_STATE_ENABLED和WifiManager.WIFI_STATE_ENABLING是不会触发这个广播接收的，即打开wifi并不会触发该广播。
        //在没有连接任何路由器的情况下关闭wifi，会触发一次该广播。应该说顺序是先触发该广播，然后是正在关闭和已经关闭。
        //
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            MyTestLog.info("net广播总数:" + netCon++);
            Parcelable parcelableExtra = intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                NetworkInfo.State state = networkInfo.getState();
                boolean isConnected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态
                MyTestLog.info("isConnected:" + isConnected);
                switch (state) {

                    case CONNECTED:
                        listener.onCONNECTED();
                        break;
                    case CONNECTING:
                        listener.onCONNECTING();
                        break;
                    case DISCONNECTED:
                        listener.onDISCONNECTED();
                        break;
                    case DISCONNECTING:
                        listener.onDISCONNECTING();
                        break;
                    case SUSPENDED:
                        listener.onSUSPENDED();
                        break;
                    case UNKNOWN:
                        listener.onUNKNOWN();
                        break;
                }
            }
        }
        // 这个监听网络连接的设置，包括wifi和移动数据的打开和关闭。.
        // 最好用的还是这个监听。wifi如果打开，关闭，以及连接上可用的连接都会接到监听。见log
        // 这个广播的最大弊端是比上边两个广播的反应要慢，如果只是要监听wifi，我觉得还是用上边两个配合比较合适
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            MyTestLog.info("con广播总数:" + conCon++);
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo gprs = manager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifi = manager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            listener.onCONNECTIVITY();
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
                MyTestLog.info("getType:" + info.getType());

                if (NetworkInfo.State.CONNECTED == info.getState()) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    }
                } else if (NetworkInfo.State.DISCONNECTING == info.getState()) {

                }
            }

        }
    }

    MyNetworkConnectChangedListener listener;
    MyNetworkConnectChangedListenerAdapter adapter = new MyNetworkConnectChangedListenerAdapter();
    Context context;

    public void setNetworkConnectChangedListener(MyNetworkConnectChangedListener listener) {
        if(listener != null)
            this.listener = listener;
        else
            this.listener = adapter;
    }

    public interface MyNetworkConnectChangedListener {

        void onWIFI_DISABLING();

        void onWIFI_DISABLED();

        void onWIFI_ENABLING();

        void onWIFI_ENABLED();

        void onWIFI_UNKNOWN();

        void onCONNECTING();

        void onCONNECTED();

        void onSUSPENDED();

        void onDISCONNECTING();

        void onDISCONNECTED();

        void onUNKNOWN();

        void onCONNECTIVITY();
    }

    protected class MyNetworkConnectChangedListenerAdapter implements MyNetworkConnectChangedListener{

        @Override
        public void onWIFI_DISABLING() {
            MyTestLog.info("WIFI_DISABLING");
        }

        @Override
        public void onWIFI_DISABLED() {
            MyTestLog.info("WIFI_DISABLED");
        }

        @Override
        public void onWIFI_ENABLING() {
            MyTestLog.info("WIFI_ENABLING");
        }

        @Override
        public void onWIFI_ENABLED() {
            MyTestLog.info("WIFI_ENABLED");
        }

        @Override
        public void onWIFI_UNKNOWN() {
            MyTestLog.info("WIFI_UNKNOWN");
        }

        @Override
        public void onCONNECTING() {
            MyTestLog.info("CONNECTING");
        }

        @Override
        public void onCONNECTED() {
            MyTestLog.info("CONNECTED");
        }

        @Override
        public void onSUSPENDED() {
            MyTestLog.info("SUSPENDED");
        }

        @Override
        public void onDISCONNECTING() {
            MyTestLog.info("DISCONNECTING");
        }

        @Override
        public void onDISCONNECTED() {
            MyTestLog.info("DISCONNECTED");
        }

        @Override
        public void onUNKNOWN() {
            MyTestLog.info("UNKNOWN");
        }

        @Override
        public void onCONNECTIVITY() {
            MyTestLog.info("CONNECTIVITY:"+MyNetworkCheck.getStringWifiIp(context));
        }
    }
}
