package com.lu.deerweatherlove.base;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.lu.deerweatherlove.common.utils.ULog;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;


/**
 * Created by L on 16/11/19.
 */

public class BaseApplication extends Application {

    public static String cacheDir;

    public static Context sAppContext = null;


    @Override
    public void onCreate() {
        super.onCreate();
/**
 * 友盟推送
 */

        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                ULog.d("UUUMMM"+deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });


        sAppContext = getApplicationContext();


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
        return sAppContext;
    }

    public static String getCachedir() {
        return cacheDir;
    }
}
