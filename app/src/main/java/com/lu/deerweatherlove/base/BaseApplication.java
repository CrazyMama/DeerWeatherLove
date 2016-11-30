package com.lu.deerweatherlove.base;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

/**
 * Created by L on 16/11/19.
 */

public class BaseApplication extends Application {


    public static Context mAppContext=null;
    public static String cacheDir;




    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();

        /**
         * 如果存在SD卡将缓存写入SD卡，否则写入手机内存
         */
        if (getApplicationContext().getExternalCacheDir() != null & ExistSDCard()) {
            cacheDir = getApplicationContext().getExternalCacheDir().toString();
        } else {
            cacheDir = getApplicationContext().getCacheDir().toString();
        }
    }

    private boolean ExistSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    public static Context getmAppContext() {
        return mAppContext;
    }
}
