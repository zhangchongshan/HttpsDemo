package com.sonoptek.httpscheckdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GPSUtil {

    public static JSONObject getGPS(Activity context) {
        if (ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 2019);
            return null;
        }
        JSONObject object=new JSONObject();
        double latitude = 0.0;
        double longitude = 0.0;

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        List<String> list = locationManager.getProviders(true);
        for (String str:list){
            Log.e("TTTT", "getGPS provider: "+str );
        }
        //获取最佳的LocationProvider
        // 创建一个过滤条件对象
        // 需要加入权限
        // <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
        Criteria criteria = new Criteria();
        //设置为不收费的
        criteria.setCostAllowed(false);
        // 使用精度最准确的
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
         //设置中等耗电量
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
         //获取最佳的LocationProvider名称
        String provider = locationManager.getBestProvider(criteria, true);
        Log.e("TTTT", "getGPS zuijia: "+provider );

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)&&locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!=null) {

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null){
                Log.e("TTTT", "GPSTest: location !=null" );
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
//            String address=GetAddressUtil.getAddress(context,longitude,latitude);
            Log.e("TTTT", "GPSTest gps latitude: "+latitude );
            Log.e("TTTT", "GPSTest gps longitude: "+longitude );
//            Log.e("TTTT", "GPSTest gps address: "+address );
        }else{
            LocationListener locationListener = new LocationListener() {

                // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                // Provider被enable时触发此函数，比如GPS被打开
                @Override
                public void onProviderEnabled(String provider) {

                }

                // Provider被disable时触发此函数，比如GPS被关闭
                @Override
                public void onProviderDisabled(String provider) {

                }

                //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        Log.e("Map", "Location changed : Lat: "
                                + location.getLatitude() + " Lng: "
                                + location.getLongitude());
                    }
                }
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000, 10,locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location != null){
                Log.e("TTTT", "GPSTest: location !=null" );
                latitude = location.getLatitude(); //经度
                longitude = location.getLongitude(); //纬度
            }
//            String address=GetAddressUtil.getAddress(context,longitude,latitude);
            Log.e("TTTT", "GPSTest network latitude: "+latitude );
            Log.e("TTTT", "GPSTest network longitude: "+longitude );
//            Log.e("TTTT", "GPSTest network address: "+address );
        }
        try {
            object.put("latitude",latitude);
            object.put("longitude",longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

}
