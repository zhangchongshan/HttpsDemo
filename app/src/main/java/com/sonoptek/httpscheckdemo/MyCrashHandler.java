package com.sonoptek.httpscheckdemo;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangchongshan on 2019/12/9.
 */
public class MyCrashHandler implements Thread.UncaughtExceptionHandler {

    private Context mContext;
    private String crashLogFile="";
    private String crashLogPath="crashLog.txt";
    private static MyCrashHandler instance=null;
    public static MyCrashHandler getInstance(Context context){
        if (instance==null){
            instance=new MyCrashHandler(context);
        }
        return instance;
    }
    private MyCrashHandler(Context context){
        mContext=context;
        crashLogFile = mContext.getFilesDir().getPath() +
                File.separator + "data" + File.separator + "crashLog";
        File file=new File(crashLogFile);
        if (!file.exists()){
            file.mkdirs();
        }
    }
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.e("程序出现异常了", "Thread = " + t.getName() + "\nThrowable = " + e.getMessage());
        Log.e("程序出现异常了", "getStackTrace = " +e.getStackTrace()[0]);
        Log.e("程序出现异常了", "getStackTrace length = " +e.getStackTrace().length);
        Log.e("程序出现异常了", "getLocalizedMessage = " +e.getLocalizedMessage());
        String title=e.toString();
        title=title.substring(0,title.indexOf(": "));
        Log.e("程序出现异常了", "toString = " +title);
        String stackTraceInfo = getStackTraceInfo(e);
        Log.e("stackTraceInfo", stackTraceInfo);
        saveThrowableMessage(stackTraceInfo);

    }
    /**
     * 获取错误的信息
     *
     * @param throwable
     * @return
     */
    private String getStackTraceInfo(final Throwable throwable) {
        PrintWriter pw = null;
        Writer writer = new StringWriter();
        try {
            pw = new PrintWriter(writer);
            throwable.printStackTrace(pw);
        } catch (Exception e) {
            return "";
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
        return writer.toString();

        /*JSONObject jsonObject=new JSONObject();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=format.format(new Date());

        String name=throwable.toString();
        name=name.substring(0,name.indexOf(": "));

        String reason=throwable.getLocalizedMessage();

        StringBuffer buffer=new StringBuffer();
        buffer.append(throwable.getStackTrace()[0].toString());
        *//*for (StackTraceElement traceElement:throwable.getStackTrace()){
            buffer.append(traceElement.toString());
        }*//*
        try {
            jsonObject.put("crashTime",time);
            jsonObject.put("crashName",name);
            jsonObject.put("crashReason",reason);
            jsonObject.put("stack",buffer.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();*/
    }



    private void saveThrowableMessage(String errorMessage) {
        if (TextUtils.isEmpty(errorMessage)) {
            return;
        }

        String jsonArr=readFile(crashLogFile);
        JSONArray jsonArray = null;
        try {
            if (jsonArr.isEmpty()){
                jsonArray=new JSONArray();
            }else {
                jsonArray = new JSONArray(jsonArr);
            }
            StringBuffer buffer=new StringBuffer();

            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time=format.format(new Date());

            buffer.append(time+"  "+errorMessage);

            jsonArray.put(buffer.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonArray!=null) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonArray.toString().getBytes());
            writeStringToFile(inputStream,crashLogFile);
        }
    }

    private void writeStringToFile(final InputStream inputStream, final String filePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(new File(filePath, crashLogPath));
                    int len = 0;
                    byte[] bytes = new byte[1024];
                    while ((len = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, len);
                    }
                    outputStream.flush();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (inputStream!=null){
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    private String readFile(String path){
//        StringBuffer buffer=new StringBuffer();
        StringWriter writer=new StringWriter();
        File file=new File(path,crashLogPath);
        FileInputStream fileInputStream=null;
        try {
            fileInputStream=new FileInputStream(file);
            int len=0;
            byte [] readArray=new byte[1024];
            while ((len=fileInputStream.read(readArray))!=-1){
                String str=new String(readArray);
                writer.write(str,0,str.length());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fileInputStream!=null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e("TTTT", "readFile: "+writer.toString().length() );
        return writer.toString();
    }

    public String getCrashLog(){

        JSONObject jsonObject=new JSONObject();

        try {
            jsonObject.put("REV", "1");
            jsonObject.put("UID", "4DF1AEC0-938A-42FD-9214-870817AA4EE8");
            jsonObject.put("APC", "5001");
            jsonObject.put("VER", "3.5.0");
            jsonObject.put("DEV", "iPhone 11");
            jsonObject.put("OSV", "13.2.3");

            StringBuffer buffer=new StringBuffer();
            buffer.append(readFile(crashLogFile));
            JSONArray array=new JSONArray(checkString(buffer.toString()));
            jsonObject.put("LOG",array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public void clearCrashLog(){
        ByteArrayInputStream inputStream=new ByteArrayInputStream("".getBytes());
        writeStringToFile(inputStream,crashLogFile);
    }

    public static String replaceBlank(String src) {
        String dest = "";
        if (src != null) {
            Pattern pattern = Pattern.compile("\\s*|\t|\r|\n");
            Matcher matcher = pattern.matcher(src);
            dest = matcher.replaceAll("");
        }
        return dest;
    }

    public String checkString(String str) {
        if (TextUtils.isEmpty(str)) return "";
        int len = str.length();
        int i = 0, j = 0;
        char[] strChar = str.toCharArray();
        for (; i < len; i++) {
//            if (' ' == strChar[i] || '\t' == strChar[i] || '\n' == strChar[i])
            if ('\'' == strChar[i] )
                continue;
            if (i != j) strChar[j] = strChar[i];
            j++;
        }
        //strChar[j] = 0;//C/C++中0是结束标志位需要添加该语句，Java中会越界，需要注释该句；
        return new String(Arrays.copyOf(strChar, j));
    }
}
