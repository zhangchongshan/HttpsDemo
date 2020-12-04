package com.sonoptek.httpscheckdemo;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;

public class OutputUtil {

    public static void writeToTxt(Context context){
        File file=context.getExternalFilesDir("probe");
        if (!file.exists()){
            file.mkdir();
        }
        File txtFile=new File(file,"");
    }

    public static String getProbes(){
        String probesStr=USPreferences.getInstance(null).getString("probes","");
        Log.e("TTTT", "getProbes str: "+probesStr );
        JSONObject probes= null;
        try {
            probes = new JSONObject(probesStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder=new StringBuilder();
        if (probes!=null) {
            Iterator<String> iterator=probes.keys();
            while (iterator.hasNext()){
                builder.append(iterator.next()+",");
            }
            builder.delete(builder.length()-1,builder.length());
        }
        return builder.toString();

    }

    public static void setProbes(String probeSSID){
        String probesStr=USPreferences.getInstance(null).getString("probes","");
        Log.e("TTTT", "setProbes : "+probesStr );
        JSONObject probesJson = null;
        if (!probesStr.isEmpty()){
            try {
                probesJson=new JSONObject(probesStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            probesJson=new JSONObject();
        }
        try {
            probesJson.put(probeSSID,"");
            if (probesJson.length()>5){
                Iterator<String> iterator=probesJson.keys();
                String removeName=iterator.next();
                probesJson.remove(removeName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        USPreferences.getInstance(null).putString("probes", probesJson.toString());
    }

    public static void clearProbes(){
        USPreferences.getInstance(null).putString("probes", "");
    }


}
