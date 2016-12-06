package com.lu.deerweatherlove.base;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.lu.deerweatherlove.common.utils.ULog;


/**
 * Created by L on 16/11/19.
 */

public class BaseApplication extends Application {

    public static String cacheDir;

    public  static Context sAppContext= null;




    @Override
    public void onCreate() {
        super.onCreate();


        sAppContext =getApplicationContext();


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


    public  static Context getmAppContext() {
        return sAppContext;
    }

    public static String getCachedir(){
        return cacheDir;
    }
}
