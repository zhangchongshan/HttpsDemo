package com.sonoptek.httpscheckdemo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class USLogManager {

    private static USLogManager instance;
    private final String LOG_PREFERENCE="SMART_LOG";

    public static USLogManager getInstance(){
        if (instance==null){
            instance =new USLogManager();
        }
        return instance;
    }

    private USLogManager(){
    }

    public void writeLog(String value){
        String logStr=getLog();
        JSONObject logJson = null;
        if (logStr!=null){
            try {
                logJson=new JSONObject(logStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Date date=new Date();
        SimpleDateFormat formatDay=new SimpleDateFormat("yyyy-MM-dd");
        String dateStr=formatDay.format(date);
        SimpleDateFormat formatHour=new SimpleDateFormat("HH:mm:ss.SSS");
        String dateHourStr=formatHour.format(date);
        if (logJson==null){
            logJson=new JSONObject();
        }
        JSONObject logJsonDay=null;
        if (!logJson.isNull(dateStr)){
            try {
                logJsonDay=logJson.getJSONObject(dateStr);
                logJsonDay.put(dateHourStr,value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            logJsonDay=new JSONObject();
            try {
                logJsonDay.put(dateHourStr,value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            logJson.put(dateStr,logJsonDay);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        USPreferences preferences=USPreferences.getInstance(null);
        preferences.putString(LOG_PREFERENCE,logJson.toString());
    }

    public String getLog(){
        USPreferences preferences=USPreferences.getInstance(null);
        return preferences.getString(LOG_PREFERENCE,"");
    }

    public void clearLog(){
        USPreferences preferences=USPreferences.getInstance(null);
        preferences.putString(LOG_PREFERENCE,"");
    }

}
