package com.sonoptek.httpscheckdemo;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.TimeZone;

public class DeviceUtil {


    /**
     * 获取当前设备的时区信息
     * @return
     */
    public static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        return tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID();
    }


    /**
     * 获取系统语言
     * @return
     */
    public static String getSystemLanguage(Context context) {
        String language = null;
        try{
            language = context.getResources().getConfiguration().locale.getLanguage();
        }catch (Exception e){
            e.printStackTrace();
        }
        return language;
    }

    /**
     * 获得系统亮度
     *
     * @return
     */
    public static int getSystemBrightness(Context context) {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }

    /**
     * 获取屏幕分辨率
     *
     * @return
     */
    public static String getScreenRes(Context context) {
        try {
            // 获取屏幕分辨率
            WindowManager wm = (WindowManager)(context.getSystemService(Context.WINDOW_SERVICE));
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            int screenWidth = dm.widthPixels;
            int screenHeight = dm.heightPixels;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("screenWidth", screenWidth);
            jsonObject.put("screenHeight", screenHeight);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 应用版本号
     * @return
     */
    public static String getApkVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }


    /**
     * 获取当前apk的签名信息
     * @return
     */
    public static String getAPKSignMd5Str(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            String signStr = encryptionMD5(sign.toByteArray());
            return signStr;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * MD5加密
     * @param byteStr 需要加密的内容
     * @return 返回 byteStr的md5值
     */
    public static String encryptionMD5(byte[] byteStr) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString();
    }


    /**
     * 基带版本
     * @return
     */
    public static String getBasebandVer() {
        String Version = "";
        try {
            Class cl = Class.forName("android.os.SystemProperties");
            Object invoker = cl.newInstance();
            Method m = cl.getMethod("get", new Class[] { String.class,String.class });
            Object result = m.invoke(invoker, new Object[]{"gsm.version.baseband", "no message"});
            Version = (String)result;
        } catch (Exception e) {
        }
        return Version;
    }

    /**
     * cpu最大频率
     * @return
     */
    public static String getCPUFreQuency() {
        // 获取CPU最大频率（单位KHZ）
        // "/system/bin/cat" 命令行
        // "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" 存储最大频率的文件的路径
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        return result.trim();
    }

    /**
     * 获取当前内存信息对象
     *
     * @return 当前内存信息对象。
     */
    public static ActivityManager.MemoryInfo getMemoryInfo(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long totalMemory = mi.totalMem; //总内存字节
        long availableMemory = mi.availMem;//可用内存字节
        return mi;
    }

    /**
     * 获取系统存储空间信息
     * @return
     */
    public static StatFs getSystemStatFs(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
            long blockCount = sf.getBlockCount();
            long availCount = sf.getAvailableBlocks();
            long anailableSize = sf.getAvailableBytes(); //可用存储空间字节
            long  size= sf.getTotalBytes(); //总存储空间字节
            Log.i("TTTT", "block大小:"+ blockSize+",block数目:"+ blockCount+",总大小:"+blockSize*blockCount/1024+"KB");
            Log.i("TTTT", "可用的block数目：:"+ availCount+",剩余空间:"+ availCount*blockSize/1024+"KB");
            return sf;
        }
        return null;
    }

    /**
     * 是否允许位置模拟
     * @return
     */
    public static boolean isAllowMockLocation(Context context) {
        boolean isAllowMock = false;
        try {
            isAllowMock = Settings.Secure.getInt(context.getContentResolver(),Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0;
        }catch (Exception e){
            e.printStackTrace();
        }
        return isAllowMock;
    }

    /**
     * CPU型号或平台号
     * @return
     */
    public static String getCpu(){
        String cpuType = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            String[] cpuTypes = Build.SUPPORTED_ABIS;
            if(cpuTypes != null){
                for (String item : cpuTypes){
                    if(!item.isEmpty()){
                        cpuType += item + ",";
                    }

                }
                if(cpuType.length() > 1){
                    cpuType.substring(0, cpuType.length() - 2);
                }
            }
        }
        return cpuType;
    }

    /**
     * 获取当前的网络类型
     * @return
     */
    public static String getNetWorkType(Context context) {
        try{
            //获取系统的网络服务
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //如果当前没有网络
            if (null == connManager)
                return "NO";
            //获取当前网络类型，如果为空，返回无网络
            NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
            if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
                return "NO";
            }
            // 判断是不是连接的是不是wifi
            NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (null != wifiInfo) {
                NetworkInfo.State state = wifiInfo.getState();
                if (null != state)
                    if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                        return "WIFI";
                    }
            }
            // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
            NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (null != networkInfo) {
                NetworkInfo.State state = networkInfo.getState();
                String strSubTypeName = networkInfo.getSubtypeName();
                if (null != state)
                    if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                        switch (activeNetInfo.getSubtype()) {
                            //如果是2g类型
                            case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                            case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                            case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                            case TelephonyManager.NETWORK_TYPE_1xRTT:
                            case TelephonyManager.NETWORK_TYPE_IDEN:
                                return "2G";
                            //如果是3g类型
                            case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                            case TelephonyManager.NETWORK_TYPE_UMTS:
                            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            case TelephonyManager.NETWORK_TYPE_HSDPA:
                            case TelephonyManager.NETWORK_TYPE_HSUPA:
                            case TelephonyManager.NETWORK_TYPE_HSPA:
                            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                            case TelephonyManager.NETWORK_TYPE_EHRPD:
                            case TelephonyManager.NETWORK_TYPE_HSPAP:
                                return "3G";
                            //如果是4g类型
                            case TelephonyManager.NETWORK_TYPE_LTE:
                                return "4G";
                            default:
                                //中国移动 联通 电信 三种3G制式
                                if (strSubTypeName.equalsIgnoreCase("TD-SCDMA")
                                        || strSubTypeName.equalsIgnoreCase("WCDMA")
                                        || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                    return "3G";
                                } else {
                                    return "MOBILE";
                                }
                        }
                    }
            }
            return "NO";
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 权限
     * <uses-permission android:name="android.permission.INTERNET" />
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     */


    /**
     * 获取真实对外可用的ip列表
     * @return
     */
    public static String getIpAddressString() {
        String ipAddresses = "";
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        ipAddresses += inetAddress.getHostAddress() + ",";
                    }
                }
            }
            if(ipAddresses.length() > 1){
                ipAddresses.substring(0, ipAddresses.length() - 2);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipAddresses;
    }


    public static void SimInfo(Activity context){
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context,new String[]{Manifest.permission.READ_PHONE_STATE},2019);
                return;
            }
            String imei = telephonyManager.getDeviceId();
            Log.i("TTTT","imei ->" + imei);

            String imsi = telephonyManager.getSubscriberId();
            Log.i("TTTT","imsi ->" + imsi);

            String phoneNumber = telephonyManager.getLine1Number();
            Log.i("TTTT","SIM卡中存储本机号码 phoneNumber ->" + phoneNumber);

            String voiceMail = telephonyManager.getVoiceMailNumber();
            Log.i("TTTT","语音邮件号码 voiceMail ->" + voiceMail);

            String simSerial = telephonyManager.getSimSerialNumber();
            Log.i("TTTT","SIM卡序列号 simSerial ->" + simSerial);

            String countryIso = telephonyManager.getNetworkCountryIso();
            Log.i("TTTT","SIM卡提供商的国家代码 countryIso ->" + countryIso);

            String carrier = telephonyManager.getNetworkOperatorName();
            Log.i("TTTT","当前移动网络运营商 carrier ->" + carrier);
            String mcc="";
            String mnc="";
            if (!imsi.isEmpty() && imsi.length() == 15) {
                mcc = imsi.substring(0, 3);
                mnc = imsi.substring(3, 5);
            }
            Log.i("TTTT","mcc ->" + mcc);
            Log.i("TTTT","mnc ->" + mnc);

            String simOperator = telephonyManager.getSimOperator();
            Log.i("TTTT","SIM的移动运营商的名称 simOperator ->" + simOperator);

            int phoneType = telephonyManager.getPhoneType();
            Log.i("TTTT","移动终端的类型 phoneType ->" + phoneType);

            int radioType = telephonyManager.getNetworkType();
            Log.i("TTTT","当前使用的网络制式 radioType ->" + radioType);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 权限
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
     */



    /**
     * 获取从sim卡得到的基站经纬度
     * @return
     */
    public static String getCellLocation(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            return tm.getCellLocation().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 权限
     * <uses-permission android:name="android.permission.INTERNET" />
     * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
     */
}
