package com.sonoptek.httpscheckdemo;

import android.app.Application;

/**
 * Created by zhangchongshan on 2019/12/9.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MyCrashHandler handler=MyCrashHandler.getInstance(this);
//        Thread.setDefaultUncaughtExceptionHandler(handler);
    }
}
