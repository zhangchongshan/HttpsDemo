package com.sonoptek.httpscheckdemo;

import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.Keep;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by zhangchongshan on 2019/4/30.
 */
public class bb {

    @Keep
    private void aa(String msg) {
        /*if (msg.toLowerCase().contains("error")) {
            Log.e("TTTT", "Native Err: " + msg);
        } else {
            Log.e("TTTT", "Native Msg: " + msg);
        }*/
    }

    @Keep
    private void aaa(String url, String packageName, String version, String revision){

//        UpdateManager.getInstance(null).checkUpdate(url,packageName,version,revision);
//        Log.e("TTTT", "startCheckApp url: "+url);
//        Log.e("TTTT", "startCheckApp packageName: "+packageName );
//        Log.e("TTTT", "startCheckApp version: " +version);
//        Log.e("TTTT", "startCheckApp revision: "+revision );
    }
    /*
     * Return OS build version: a static function
     */
    @Keep
    static public String bb() {

        Log.e("TTTT", "bb: onLoad" );
        return Build.VERSION.RELEASE;
    }

    /*
     * Return Java memory info
     */
    @Keep
    public long bbb() {
        return Runtime.getRuntime().freeMemory();
    }


    /**
     * 解密
     **/
    public static String a(String decodeFormat,String encryptedPwd) {
        try {
            DESKeySpec keySpec = new DESKeySpec(decodeFormat.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] encryptedWithoutB64 = Base64.decode(encryptedPwd, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] plainTextPwdBytes = cipher.doFinal(encryptedWithoutB64);
            return new String(plainTextPwdBytes);
        } catch (Exception e) {
        }
        return encryptedPwd;
    }

    /**
     * 加密
     **/
    public static byte[] b(String codeFormat,String clearText) {
        try {
            DESKeySpec keySpec = new DESKeySpec(
                    codeFormat.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypedPwd=cipher.doFinal(clearText.getBytes("UTF-8"));

            return encrypedPwd;
        } catch (Exception e) {
        }
        return null;
    }
    public static String aa(byte[] bytes){
        String encrypedPwd = Base64.encodeToString(bytes, Base64.DEFAULT);
        return encrypedPwd;
    }
}
