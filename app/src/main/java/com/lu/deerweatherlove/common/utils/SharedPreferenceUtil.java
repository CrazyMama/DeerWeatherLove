package com.lu.deerweatherlove.common.utils;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;

import com.lu.deerweatherlove.base.BaseApplication;

/**
 * Created by L on 16/12/1.
 */

public class SharedPreferenceUtil {

    public static final String CITY_NAME = "城市";//选择城市
    public static final String HOUR = "current_hour";//当前小时

    public static final String CHANGE_ICONS = "change_icons";//切换图标
    public static final String CLEAR_CACHE = "clear_cache";//清空缓存
    public static final String AUTO_UPDATE = "change_update_time"; //自动更新时长
    public static final String NOTIFICATION_MODEL = "notification_model";
    public static final String ANIM_START = "animation_start";

    public static int ONE_HOUR = 1000 * 60 * 60;
    //danli
    private SharedPreferences mSP;

    public static SharedPreferenceUtil getInstance() {
        return SPHolder.sInstance;
    }

    private static class SPHolder {
        private static final SharedPreferenceUtil sInstance = new SharedPreferenceUtil();
    }

    private SharedPreferenceUtil() {
        mSP = BaseApplication.getmAppContext().getSharedPreferences("setting", Context.MODE_PRIVATE);
    }
    /**
     * java中return this 指的是返回类的当前对象。
     * return this; 语句一般都是用在类中非静态方法的末尾。
     * 我们知道java是面向对象的语言，在java语言中类是对象的抽象，而对象是类的具体实现。
     * 因此在java中类的非静态方法是属于对象的，this则代表当前这个非静态方法属于的对象，并且是当前类的对象。
     * 而对于return this;可能是由于根据需求需要将当前的这个对象作为返回值，以便接收，
     * 并且这个非静态方法的返回值类型据我所知只能为三种情况：父类，当前类，实现的接口。
     *
     * @param key
     * @param value
     * @return
     */

    /**
     * Int类型
     *
     * @param key
     * @param value
     * @return
     */

    public SharedPreferenceUtil putInt(String key, int value) {
        mSP.edit().putInt(key, value).apply();
        return this;
    }

    public int getInt(String key, int defValue) {

        return mSP.getInt(key, defValue);
    }

    /**
     * Stirng类型
     *
     * @param key
     * @param value
     * @return
     */
    public SharedPreferenceUtil putString(String key, String value) {
        mSP.edit().putString(key, value).apply();
        return this;
    }

    public String getString(String key, String defValue) {

        return mSP.getString(key, defValue);
    }

    /**
     * Boolean类型
     *
     * @param key
     * @param value
     * @return
     */

    public SharedPreferenceUtil putBoolean(String key, boolean value) {
        mSP.edit().putBoolean(key, value).apply();
        return this;
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mSP.getBoolean(key, defValue);
    }

    /**
     * 设置当前小时
     *
     * @param hour
     */
    public void setCurrentHour(int hour) {
        mSP.edit().putInt(HOUR, hour).apply();
    }

    public int getCurrentHour() {
        return mSP.getInt(HOUR, 0);
    }

    /**
     * 自动更新时间 单位小时
     *
     * @param time
     */
    public void setAutoUpdate(int time) {
        mSP.edit().putInt(AUTO_UPDATE, time).apply();

    }

    public int getAutoUpdate() {
        return mSP.getInt(AUTO_UPDATE, 3);
    }


    /**
     * 设置图标种类
     *
     * @param type
     */
    public void setIconType(int type) {
        mSP.edit().putInt(CHANGE_ICONS, type).apply();

    }

    public int getIconType() {
        return mSP.getInt(CHANGE_ICONS, 0);
    }

    /**
     * 当前城市
     *
     * @param cityName
     */
    public void setCityName(String cityName) {
        mSP.edit().putString(CITY_NAME, cityName).apply();
    }

    public String getCityName() {
        return mSP.getString(CITY_NAME, "保定");
    }

    /**
     * 通知栏模式，默认为常驻类型
     *
     * @param t Notification.FLAG_SHOW_LIGHTS              //三色灯提醒，在使用三色灯提醒时候必须加该标志符
     *          Notification.FLAG_ONGOING_EVENT          //发起正在运行事件（活动中）
     *          Notification.FLAG_INSISTENT   //让声音、振动无限循环，直到用户响应 （取消或者打开）
     *          Notification.FLAG_ONLY_ALERT_ONCE  //发起Notification后，铃声和震动均只执行一次
     *          Notification.FLAG_AUTO_CANCEL      //用户单击通知后自动消失
     *          Notification.FLAG_NO_CLEAR          //只有全部清除时，Notification才会清除 ，不清楚该通知(QQ的通知无法清除，就是用的这个)
     *          Notification.FLAG_FOREGROUND_SERVICE    //表示正在运行的服务
     */

    public void setNotificationModel(int t) {
        mSP.edit().putInt(NOTIFICATION_MODEL, t).apply();
    }

    public int getNotificationModel() {
        return mSP.getInt(NOTIFICATION_MODEL, Notification.FLAG_ONGOING_EVENT);
    }

    /**
     * item 动画效果，默认关闭状态
     *
     * @param b
     */
    public void setMainAnim(boolean b) {
        mSP.edit().putBoolean(ANIM_START, b).apply();
    }

    public boolean getMainAnim() {
        return mSP.getBoolean(ANIM_START, false);
    }

}