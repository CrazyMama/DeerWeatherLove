package com.lu.deerweatherlove.base;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

/**
 * Created by L on 16/10/19.
 */

public class BaseActivity extends RxAppCompatActivity {

    private static String TAG = BaseActivity.class.getCanonicalName();


    /**
     * 设置状态栏颜色
     * 也就是所谓沉浸式状态栏
     */

    /**
     * 为4.4 设置状态栏颜色
     * @param color
     */
    public void setStatusBarColorKitkat(int color) {

        /**
         * Android 4.4
         */

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//全透明
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(color);

        }

    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 程序中进行主题的初始化。你需要调用 AppCompatDelegate.setDefaultNightMode() ，它有四个参数：

     MODE_NIGHT_NO. 使用亮色（light）主题

     MODE_NIGHT_YES. 使用暗色（dark）主题

     MODE_NIGHT_AUTO. 根据当前时间自动切换 亮色（light）/暗色（dark）主题

     MODE_NIGHT_FOLLOW_SYSTEM(默认选项). 设置为跟随系统，通常为 MODE_NIGHT_NO
     * @param activity
     */
    public static void setDayTheme(AppCompatActivity activity) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        activity.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //调用recrete() 设置生效
        activity.recreate();
    }
    public static void setNightTheme(AppCompatActivity activity) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        activity.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        //调用recrete() 设置生效
        activity.recreate();
    }


    public void setTheme(boolean isNights, AppCompatActivity activity) {
        if (isNights){
            setNightTheme(activity);
        }else {
            setDayTheme(activity);
        }
    }


    public void setTheme(AppCompatActivity activity) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        activity.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        activity.recreate();
    }
}
