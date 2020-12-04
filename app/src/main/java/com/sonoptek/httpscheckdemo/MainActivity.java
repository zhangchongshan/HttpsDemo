package com.sonoptek.httpscheckdemo;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sonoptek.httpscheckdemo.https_util.MyHttpsUtil;
import com.sonoptek.httpscheckdemo.ssl_util.AESUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class MainActivity extends AppCompatActivity {

    private final int RESULT_REQUESTCODE=10008;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    private PublicKey publicKey;
    private String encodeStr;
//    private final String url="https://api.sonoptek.com/da/test.php";
    private final String url="https://api.sonoptek.com/da/expro_win.php";
    private int probeIndex=1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RESULT_REQUESTCODE:{

                if (resultCode== Activity.RESULT_OK&&data!=null){


                    alterDocument(data.getData());
                    Log.e("TTTT", "onActivityResult: "+data.toString() );
                }
            }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        USPreferences.getInstance(this);

        Log.e("TTTT", "onCreate: "+ Environment.getExternalStorageState());

        Log.e("TTTT", "onCreate the log: "+USLogManager.getInstance().getLog() );

        USLogManager.getInstance().writeLog("onCreate");

//        USLog.getInstance(this).clearLog();
        USLog.getInstance(this).writeLog("SV-2 BGCA001","Connect","");

        Log.e("TTTT", "onCreate probeList[0]: "+getSupportProbeList(this)[0] );


        Button btnEncode=findViewById(R.id.btn_encode);
        btnEncode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                createFile("image/png", "mypicture.png");


                byte[] encode=encryptPasswordByte(KEY, "ASCII");
                StringBuilder builder=new StringBuilder();
                for (byte i:encode){
                    builder.append(i+",");
                }
                Log.e("TTTT", "onClick str to bytes: "+builder.toString()+" --" );
                String decode=decryptPassword(KEY,bytes2String(encode));
                Log.e("TTTT", "onClick decode result: "+decode +" ---");

                /*String cSrc = "1234567890";
                System.out.println("加密前的字串是："+cSrc);
                // 加密
                String enString = AESUtil.getInstance().encrypt(cSrc,"utf-8",AESUtil.sKey,AESUtil.ivParameter);
                System.out.println("加密后的字串是："+ enString);

                // 解密
                String DeString = AESUtil.getInstance().decrypt(enString,"utf-8",AESUtil.sKey,AESUtil.ivParameter);
                System.out.println("解密后的字串是：" + DeString);*/

                /*SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date=format.parse("2020-10-01");
                    Date now=new Date();
                    Log.e("TTTT", "onClick: "+((date.getTime()-now.getTime())/1000/3600/24) );
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/
                /*Log.e("TTTT", "onClick: "+getPackageName() );
                byte[] encode=encryptPasswordByte(KEY, "rsa_public_key.pem");
                StringBuilder builder=new StringBuilder();
                for (byte i:encode){
                    builder.append(i+",");
                }
                Log.e("TTTT", "onClick str to bytes: "+builder.toString() );
                String decode=decryptPassword(KEY,bytes2String(encode));
                Log.e("TTTT", "onClick decode result: "+decode +"-");



                Log.e("DDDD", "onClick: "+Build.VERSION.SDK_INT );
                //设备Android版本
                Log.e("DDDD", "onClick: "+Build.VERSION.RELEASE );
                //设备型号
                Log.e("DDDD", "onClick: "+Build.MODEL );
                //手机品牌
                Log.e("DDDD", "onClick: "+Build.BRAND );
                //出厂时设备名称
                Log.e("DDDD", "onClick: "+Build.DEVICE );
                //硬件平台信息
                Log.e("DDDD", "onClick: "+Build.HARDWARE );*/

                /* AlertDialog .Builder dialog=new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("Key");
                dialog.setMessage(getSha());
                dialog.setCancelable(true);
                dialog.show();*/

                // TODO: 2020/8/17

//                DeviceUtil.SimInfo(MainActivity.this);

                // TODO: 2020/8/17
//                String mesage=getSignature(MainActivity.this);
//                mesage=KeyUtil.stringToMD5(mesage);
//                Log.e("TTTT", "onClick: "+mesage );
//                String signStr = encryptionMD5(mesage.getBytes());
//                Log.e("DDDD", "onClick: "+signStr);


                //获取签名文件SHA1
                Log.e("TTTT", "onClick: "+KeyUtil.sHA1(MainActivity.this));

                /*Map<String,String> data=new HashMap<>();
                data.put("REV","1");
                data.put("APC","5101");
                data.put("VER","app_version");
                data.put("PRC","100");
                data.put("DATE","2019-10-11");
                data.put("OS", "智能B超");
                data.put("OSV","13");
                data.put("UID","uuid");*/


                /*data.put("REV","1");
                data.put("APP","智能B超");
                data.put("VER",getVersionName());
                data.put("BID",getPackageName());
                data.put("PBS",OutputUtil.getProbes());
                data.put("DEV",Build.BRAND+" "+Build.MODEL);
                data.put("OS","Android");
                data.put("OSV",""+Build.VERSION.RELEASE);


                JSONObject l=GPSUtil.getGPS(MainActivity.this);
                double latitude = 0;
                double longitude=0;
                try {
                    latitude=l.getDouble("latitude");
                    longitude=l.getDouble("longitude");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                data.put("LOC",""+latitude+","+longitude);
//                data.put("ADR",GetAddressUtil.getAddress(MainActivity.this,longitude,latitude));
                data.put("ADR","北京");
                data.put("UID",GetDeviceId.getDeviceId(MainActivity.this));*/

//                String result=MyHttpsUtil.submitPostData(MainActivity.this,url,data,"utf-8");
//                Log.e("DDDD", "onClick: "+result );
//                OutputUtil.clearProbes();
            }
        });
        Button btnDecode=findViewById(R.id.btn_decode);
        btnDecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OutputUtil.setProbes("SV-2 GMBGCA00"+probeIndex);
                probeIndex++;

                //私钥解密
                if (TextUtils.isEmpty(encodeStr)) {
                    Toast.makeText(MainActivity.this, "请先加密", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    byte[] decryptBytes = AndroidKeyStoreRSAUtils.decryptByPrivateKeyForSpilt(
                            Base64Decoder.decodeToBytes(encodeStr));
                    Log.e("DDDD", "onClick decode result: "+new String(decryptBytes));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        TextView textView=new TextView(this);
        textView.setText("MainActivity自定义View");
        textView.setTextColor(Color.parseColor("#ff0000"));
        textView.setTextSize(40.0f);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0,1);
        params.setMargins(30,10,30,0);
        TestLayout.getTheLayout().addView(textView,params);

    }

    @Override
    protected void onStart() {
        super.onStart();
//        MyCrashHandler.getInstance(null).clearCrashLog();
        new Thread(){
            @Override
            public void run() {
                super.run();
//                int i=0;
//                int error=100/i;
//
//                error();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                USLog.getInstance(null).writeLog("SV-2 BGCA002","DisConnect","100");
                String json=USLog.getInstance(null).getLog();
                Log.e("TTTT", "onStart: "+ json);
//                String url="https://api.sonoptek.com/da/log.php";
//                String result=MyHttpsUtil.submitPostJsonData(url,json);
//                Log.e("TTTT", "onStart: "+result );



            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                super.run();
                String json=MyCrashHandler.getInstance(null).getCrashLog();
                Log.e("TTTT", "run crashLog: "+ json);
                String url="https://api.sonoptek.com/da/crash.php";
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                String result=MyHttpsUtil.submitPostJsonData(url,json);
//                Log.e("TTTT", "run result: "+result );
            }


        }.start();
        USLogManager.getInstance().writeLog("onStart");


    }

    private void error(){
        String str=null;
        str.toCharArray();
    }
    @Override
    protected void onResume() {
        super.onResume();
        USLogManager.getInstance().writeLog("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        USLogManager.getInstance().writeLog("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        USLogManager.getInstance().writeLog("onStop");
    }

    @Override
    protected void onDestroy() {
        USLogManager.getInstance().writeLog("onDestroy");
        super.onDestroy();
    }

    /**
     * 在应用安装后第一次运行时，生成一个随机密钥，并存入 KeyStore
     * 当你想存储一个数据，便从 KeyStore 中取出之前生成的随机密钥，对你的数据进行加密，
     * 加密完成后，已完成加密的数据可以随意存储在任意地方，比如 SharePreferences，
     * 此时即使它被他人读取到，也无法解密出你的原数据，因为他人取不到你的密钥
     * 当你需要拿到你的原数据，只需要从 SharePreferences 中读取你加密后的数据，
     * 并从 KeyStore 取出加密密钥，使用加密密钥对 “加密后的数据” 进行解密即可
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.e("DDDD", "onWindowFocusChanged: start" );
        if (AndroidKeyStoreRSAUtils.isHaveKeyStore()) {//是否有秘钥
            Log.e("DDDD", "onWindowFocusChanged: is have key" );
            publicKey = (RSAPublicKey) AndroidKeyStoreRSAUtils.getLocalPublicKey();
            if (publicKey != null) {
                Toast.makeText(MainActivity.this, "已经生成过密钥对", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        try {//在项目中放在application或启动页中
            KeyPair keyPair = AndroidKeyStoreRSAUtils.generateRSAKeyPair(MainActivity.this);
            // 公钥
            publicKey = (RSAPublicKey) keyPair.getPublic();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    public static String getSignature(Context context)
    {
        try {
            /** 通过包管理器获得指定包名包含签名的包信息 **/
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            /******* 通过返回的包信息获得签名数组 *******/
            Signature[] signatures = packageInfo.signatures;
            byte[] bs=signatures[0].toByteArray();
            /******* 循环遍历签名数组拼接应用签名 *******/
            StringBuilder stringBuilder=new StringBuilder();
            return signatures[0].toCharsString();
            /************** 得到应用签名 **************/
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getVersionName()
    {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        return version;
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
            StringBuffer buffer=new StringBuffer();
            for (int i = 0; i < byteArray.length; i++) {
                buffer.append(""+byteArray[i]+",");
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
            Log.e("TTTT", "encryptionMD5: "+buffer.toString() );
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString();
    }


    private final String KEY="6CfOTxbD";
    /**
     * 解密
     **/
    private String decryptPassword(String decodeFormat,String encryptedPwd) {
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
    private byte[] encryptPasswordByte(String codeFormat,String clearText) {
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
    private String bytes2String(byte[] bytes){
        String encrypedPwd = Base64.encodeToString(bytes, Base64.DEFAULT);
        return encrypedPwd;
    }

    public native String getCourseKeyFromC();
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public native String getSha();

    public native byte[] toBytes();

    public native void init();


    public native String getImageData(int id);

    public native String[] getSupportProbeList(Context context);

//    public native User getUser(String name);


    private void createFile(String mimeType,String fileName){
        Intent intent=new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE,fileName);
        startActivityForResult(intent,RESULT_REQUESTCODE);

    }

    private void alterDocument(Uri uri) {
        try {
            ParcelFileDescriptor pfd =getContentResolver().
                    openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream =
                    new FileOutputStream(pfd.getFileDescriptor());
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.bg);
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
//            byte[] data=baos.toByteArray();
//            fileOutputStream.write(data);

            ByteArrayInputStream bais=new ByteArrayInputStream(baos.toByteArray());
            byte [] len=new byte[1024];
            int index=0;
            while ((index=bais.read(len))!=-1){
                fileOutputStream.write(len,0,index);
                fileOutputStream.flush();
            }
            // Let the document provider know you're done by closing the stream.
            fileOutputStream.close();
            pfd.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
