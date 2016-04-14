package com.zhuo.tong.utils.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Proxy;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

public class MyNetworkCheck {
    public static String proxy;
    public static int port;
    public static Proxy mProxy;

    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {

            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是wifi连接 ,并未判断是否是连通
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return (ni != null && ni.isAvailable()
                && ni.getType() == ConnectivityManager.TYPE_WIFI);

    }

    /**
     * 判断是否是手机连接 ,并未判断是否是连通
     */
    public static boolean isMobile(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return (ni != null && ni.isAvailable()
                && ni.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * 检查代理，是否cnwap接入
     * 如果是手机上网并且是wap方式，则根据是否获取到apn信息判断是否需要设置apn代理，
     * 并如果需要设置代理则mProxy = new Proxy(Proxy.Type.HTTP, sa);
     * 返回false不需要设置代理，true则设置代理
     * HttpURLConnection通过以下方法设置代理
     * httpConnect = (HttpURLConnection) url.openConnection(mProxy);
     */
    @SuppressWarnings("deprecation")
    public static boolean detectProxy(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isAvailable()
                && ni.getType() == ConnectivityManager.TYPE_MOBILE) {
            String proxyHost = android.net.Proxy.getDefaultHost();
            int port = android.net.Proxy.getDefaultPort();
            if (proxyHost != null) {
                final InetSocketAddress sa = new InetSocketAddress(proxyHost,
                        port);
                mProxy = new Proxy(Proxy.Type.HTTP, sa);
                return true;
            }
        }
        return false;
    }

    /**
     * 如果是手机上网并且是wap方式，则根据是否获取到apn信息判断是否需要设置apn代理，
     * 并如果需要设置代理则读出设置的apn的值,proxy是代理地址，port是端口
     * 返回false不需要设置代理，true则设置代理
     * ------------
     * HttpClient设置代理的方法：
     * HttpClient client=new DefaultHttpClient();
     * HttpHost host = new HttpHost(proxy, port);
     * client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, host);
     *
     * @param context
     */
    public static boolean hasAPN(Context context) {
        if (!isMobile(context))
            return false;
        Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");//4.0模拟器屏蔽掉该权限

        // 操作联系人类似
        ContentResolver resolver = context.getContentResolver();
        // 判断是哪个APN被选中了
        Cursor cursor = resolver.query(PREFERRED_APN_URI, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                proxy = cursor.getString(cursor.getColumnIndex("proxy"));
                port = cursor.getInt(cursor.getColumnIndex("port"));
            }
            cursor.close();
        }

        return !TextUtils.isEmpty(proxy) && port != 0;
    }


    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        /*Intent intent = new Intent("/");  
        ComponentName cm = new ComponentName("com.android.settings",  
                "com.android.settings.WirelessSettings");  
        intent.setComponent(cm);  
        intent.setAction("android.intent.action.VIEW");  
        activity.startActivityForResult(intent, 0); */

        Intent intent = null;
        //判断手机系统的版本  即API大于10 就是3.0或以上版本 
        if (android.os.Build.VERSION.SDK_INT > 10) {
            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        } else {
            intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        activity.startActivity(intent);
    }

    /**
     * 检测wifi是否开启
     *
     * @param context
     * @return
     */
    public static boolean isWifiEnabled(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    /**
     * 开启wifi
     *
     * @param context
     */
    public static void setWifiEnabled(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    /**
     * 关闭wifi
     *
     * @param context
     */
    public static void setWifiDisabled(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
    }

    /**
     * 切换wifi状态
     * @param context
     * @return true:当前已经开启了wifi;false:当前已经关闭了wifi;
     */
    public static boolean autoSetWifiEnabled(Context context) {
        if (!isWifiEnabled(context)) {
//            wifi_TextView.setText("wifi:未开启");
            setWifiEnabled(context);
            return true;
        } else {
//            wifi_TextView.setText("wifi:已开启");
            setWifiDisabled(context);
            return false;
        }
    }

    /**
     * 获取wifi连接的ip地址
     * @param context
     * @return
     */
    public static int getIntWifiIp(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getIpAddress();
    }

    /**
     * 获取wifi连接的ip地址
     * @param context
     * @return
     */
    public static String getStringWifiIp(Context context){
        return iintToIp(getIntWifiIp(context));
    }

    /**
     * 得到本机Mac地址,不过有可能得到的是默认的mac地址，参见：
     * private String mMacAddress = DEFAULT_MAC_ADDRESS;//"02:00:00:00:00:00"
     * 所以要不要判断不好说。
     * @param context
     * @return
     */
    public static String getMacAddress(Context context)
    {
        return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress();
    }

    /**
     * 设置mac地址,看源码好像只是
     * public void setMacAddress(String macAddress) { this.mMacAddress = macAddress; }
     * 不确定是否有效。
     * @param context
     * @param macAddress
     */
    public static void setMacAddress(Context context, String macAddress) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Class<? extends WifiInfo> wifiInfoClass = wifiInfo.getClass();
        try {
            Method setMacAddress = wifiInfoClass.getMethod("setMacAddress", String.class.getClass());
            setMacAddress.invoke(wifiInfo, macAddress);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }



        /**
         * 把int->ip地址
         *
         * @param ipInt
         * @return String
         */
    public static String intToIp(int ipInt) {
        return (ipInt & 0xFF) + "." +
                ((ipInt >> 8) & 0xFF) + "." +
                ((ipInt >> 16) & 0xFF) + "." +
                (ipInt >> 24 & 0xFF);
    }


    /**
     * 地址转换后是反的，但是网上在线转换和这个一样，可能是安卓本身的问题。这个转换或许是pc用的？
     *
     * @param ipInt
     * @return
     */
    public static String iintToIp(int ipInt) {
        return new StringBuilder().append(((ipInt >> 24) & 0xff)).append('.')
                .append((ipInt >> 16) & 0xff).append('.').append(
                        (ipInt >> 8) & 0xff).append('.').append((ipInt & 0xff))
                .toString();
    }

    /**
     * 貌似有问题，获取到的地址还有可能是usb共享网络给电脑的ip
     * @return
     */
    public static String getIpAddress(){
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()) {
                    InetAddress ipp = inet.nextElement();
                    if (!ipp.isLoopbackAddress()
                            /*&& InetAddressUtils.isIPv4Address(ipp
                            .getHostAddress())*/
                            && ipp instanceof Inet4Address) {
                        ipaddress = ipp.getHostAddress();
                        return ipaddress;
                    }
                }

            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipaddress;
    }

    //根据busybox获取本地Mac
    public static String getLocalMacAddressFromBusybox(){
        String result = "";
        String Mac = "";
        result = callCmd("busybox ifconfig","HWaddr");

        //如果返回的result == null，则说明网络不可取
        if(result==null){
            return "网络出错，请检查网络";
        }

        //对该行数据进行解析
        //例如：eth0      Link encap:Ethernet  HWaddr 00:16:E8:3E:DF:67
        if(result.length()>0 && result.contains("HWaddr")==true){
            Mac = result.substring(result.indexOf("HWaddr")+6, result.length()-1);
            Log.i("test","Mac:"+Mac+" Mac.length: "+Mac.length());

             /*if(Mac.length()>1){
                 Mac = Mac.replaceAll(" ", "");
                 result = "";
                 String[] tmp = Mac.split(":");
                 for(int i = 0;i<tmp.length;++i){
                     result +=tmp[i];
                 }
             }*/
            result = Mac;
            Log.i("test",result+" result.length: "+result.length());
        }
        return result;
    }

    private static String callCmd(String cmd,String filter) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader (is);

            //执行命令cmd，只取结果中含有filter的这一行
            while ((line = br.readLine ()) != null && line.contains(filter)== false) {
                //result += line;
                Log.i("test","line: "+line);
            }

            result = line;
            Log.i("test","result: "+result);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //根据IP获取本地Mac
    public static String getLocalMacAddressFromIp(Context context) {
        String mac_s= "";
        try {
            byte[] mac;
            NetworkInterface ne=NetworkInterface.getByInetAddress(InetAddress.getByName(getIpAddress()));
            mac = ne.getHardwareAddress();
            mac_s = byte2hex(mac);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mac_s;
    }

    public static  String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer(b.length);
        String stmp = "";
        int len = b.length;
        for (int n = 0; n < len; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs = hs.append("0").append(stmp);
            else {
                hs = hs.append(stmp);
            }
        }
        return String.valueOf(hs);
    }

    private String getMac()
    {
        String macSerial = null;
        String str = "";

        try
        {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str;)
            {
                str = input.readLine();
                if (str != null)
                {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;
    }
}
