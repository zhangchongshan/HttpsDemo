package com.sonoptek.httpscheckdemo;

import android.content.Context;
import android.util.Log;

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

/**
 * Created by zhangchongshan on 2019/12/9.
 */
public class USLog {
    private String logFilePath ="";
    private String logPath="log.txt";
    private final String LOG_PREFERENCE="NEW_SMART_LOG";
    private final String REV="REV";
    private final String UUID="UID";
    private final String APP_ID="AID";
    private final String APC="APC";
    private final String LOG="LOG";
    private final String PROBE="P";
    private final String TIME="D";
    private final String OP="OP";
    private final String PARAM="PA";

    private static USLog instance=null;
    public static USLog getInstance(Context context){
        if (instance==null){
            instance=new USLog(context);
        }
        return instance;
    }

    private USLog(Context context){
        logFilePath= context.getFilesDir().getPath() +
                File.separator + "data" + File.separator + "UserLog";
        File file=new File(logFilePath);
        if (!file.exists()){
            boolean makdirs=file.mkdirs();
        }
    }

    public void writeLog(String probe,String op,String param){
        JSONArray jsonArray = null;
        String jsonArrayStr=getLogJsonArray();
        if (!jsonArrayStr.isEmpty()){
            try {
                jsonArray=new JSONArray(jsonArrayStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            jsonArray=new JSONArray();
        }
        if (jsonArray==null){
            jsonArray=new JSONArray();
        }
        JSONObject item=new JSONObject();
        try {
            item.put(PROBE,probe);
            item.put(TIME,System.currentTimeMillis()/1000);
            item.put(OP,op);
            item.put(PARAM,param);

            jsonArray.put(item);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ByteArrayInputStream inputStream=new ByteArrayInputStream(jsonArray.toString().getBytes());
        writerFile(logFilePath,inputStream);
//        USPreferences preferences=USPreferences.getInstance(null);
//        preferences.putString(LOG_PREFERENCE,jsonArray.toString());
    }

    public String getLog(){
        JSONObject log=new JSONObject();
//        USPreferences preferences=USPreferences.getInstance(null);
//        String logArray=preferences.getString(LOG_PREFERENCE,"");
        String logArray=readFile(logFilePath);
        try {
            log.put(REV,"1");
            log.put(UUID,"4DF1AEC0-938A-42FD-9214-870817AA4EE8");
            log.put(APP_ID,"65007558-39E2-4819-9078-F2197ACD7E11");
            log.put(APC,"5001");

            JSONArray array=new JSONArray(logArray);
            log.put("LOG",array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return log.toString();
    }

    private String getLogJsonArray(){
//        USPreferences preferences=USPreferences.getInstance(null);
//        return preferences.getString(LOG_PREFERENCE,"");
        return readFile(logFilePath);
    }

    public void clearLog(){
//        USPreferences preferences=USPreferences.getInstance(null);
//        preferences.putString(LOG_PREFERENCE,"");
        ByteArrayInputStream inputStream=new ByteArrayInputStream("".getBytes());
        writerFile(logFilePath,inputStream);
    }

    private String readFile(String path){

        StringBuffer buffer=new StringBuffer();
        File file=new File(path,logPath);
        FileInputStream fileInputStream=null;
        try {
            fileInputStream=new FileInputStream(file);
            int len=0;
            byte [] readArray=new byte[1024];
            while ((len=fileInputStream.read(readArray))!=-1){
                String str=new String(readArray);
                buffer.append(str);
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
        return buffer.toString();
    }

    private void writerFile(String path, InputStream inputStream){
        File file=new File(path,logPath);
        /*if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        FileOutputStream outputStream=null;
        try {
           outputStream=new FileOutputStream(file);
            byte [] cache=new byte[1024];
            int len=0;
            while ((len=inputStream.read(cache))!=-1){
                outputStream.write(cache);
                outputStream.flush();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
