package com.sonoptek.httpscheckdemo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Base64;
import android.util.Log;

import com.sonoptek.httpscheckdemo.ssl_util.Base64Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.security.cert.X509Certificate;

public class KeyUtil {


    public static void getKey() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, CertificateException {
        //1，获取cipher 对象
        Cipher cipher = Cipher.getInstance("RSA");

        //2，通过秘钥对生成器KeyPairGenerator 生成公钥和私钥
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        //使用公钥进行加密，私钥进行解密（也可以反过来使用）
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        //加密
        //3,使用公钥初始化密码器
        String key="-----BEGIN PUBLIC KEY-----\n" +
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDNl2Uq1bsP0TnmH/4YxDHOEloi\n" +
                "7/iSMChGQoX/GMHtkN3GkC2OmZN3fTSgCIdqmGbYjw1EfF2CZRyEVcI0QIpkVN15\n" +
                "gLyRpU2BKlximcTfcZ9K8jq0D0vDpxBt/oohD4JmO+au5ni+Y9GGl+DmtFmVN5tm\n" +
                "DrpZDOwrV6/y6mRSfQIDAQAB\n" +
                "-----END PUBLIC KEY-----";
        InputStream inputStream=new ByteArrayInputStream(key.getBytes());
        Certificate certificate=CertificateFactory.getInstance("X.509").generateCertificate(inputStream);
        cipher.init(Cipher.ENCRYPT_MODE, certificate);
        //4，执行加密操作 (如果要转成String,一定要用Base64转换，否则会乱码解密失败)
        String content="test";
        byte[] result = cipher.doFinal(content.getBytes());
        byte[] base64Rel = Base64.encode(result, Base64.DEFAULT);
        String strRel = new String(base64Rel);
        Log.d("xl", strRel);

        //解密
        //3.使用私钥初始化密码器
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        //4.执行解密操作 (如果要转成String,一定要用Base64转换，否则会乱码解密失败)
        byte[] decodeBytes = Base64.decode(strRel, Base64.DEFAULT);
        byte[] decodeRel = cipher.doFinal(decodeBytes);
        String decodeStr = new String(decodeRel);
        Log.d("xl", decodeStr);
    }

    static int MAX_ENCRYPT_BLOCK=128;
    /**
     * * <p>
     *     * 公钥加密     *
     * </p>     *
     * * @param data 源数据
     * * @param publicKey 公钥(BASE64编码)
     * * @return     * @throws Exception     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        byte[] keyBytes = Base64Utils.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("AndroidKeyStore");
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 将字符串转成MD5值
     * @param string 需要转换的字符串
     * @return 字符串的MD5值
     */
    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF)+":");
        }
        hex.delete(hex.length()-1,hex.length());
        return hex.toString();
    }

    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            StringBuffer buffer=new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                buffer.append(""+publicKey[i]+",");
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
//                hexString.append(":");
            }
            Log.e("TTTT", "sHA1: "+buffer.toString() );
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
