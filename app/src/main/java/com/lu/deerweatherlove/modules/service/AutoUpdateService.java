package com.lu.deerweatherlove.modules.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.lu.deerweatherlove.R;
import com.lu.deerweatherlove.common.utils.SharedPreferenceUtil;
import com.lu.deerweatherlove.common.utils.Util;
import com.lu.deerweatherlove.component.RetrofitSingleton;
import com.lu.deerweatherlove.modules.main.domain.Weather;
import com.lu.deerweatherlove.modules.main.view.MainActivity;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by L on 16/12/5.
 * Info：自动请求更新服务。哈哈
 *
 */
public class AutoUpdateService extends Service {

    private final String TAG = AutoUpdateService.class.getSimpleName();
    private SharedPreferenceUtil mSharedPreferenceUtil;
    // http://blog.csdn.net/lzyzsd/article/details/45033611
    // 在生命周期的某个时刻取消订阅。一个很常见的模式就是使用CompositeSubscription来持有所有的Subscriptions，
    // 然后在onDestroy()或者onDestroyView()里取消所有的订阅
    private CompositeSubscription mCompositeSubscription;
    private Subscription mNetSubcription;

    private boolean isUnsubscribed = true;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        synchronized (this) {
            unSubscribed();
            if (isUnsubscribed) {
                unSubscribed();
                if (mSharedPreferenceUtil.getAutoUpdate() != 0) {
                    //interval 时间间隔
                    mNetSubcription = Observable.interval(mSharedPreferenceUtil.getAutoUpdate(), TimeUnit.HOURS)
                            .subscribe(aLong -> {
                                isUnsubscribed = false;
                                //PLog.i(TAG, SystemClock.elapsedRealtime() + " 当前设置" + mSharedPreferenceUtil.getAutoUpdate());
                                fetchDataByNetWork();
                            });
                    mCompositeSubscription.add(mNetSubcription);
                }
            }
        }
        return START_REDELIVER_INTENT;
    }

    private void unSubscribed() {
        isUnsubscribed = true;
        mCompositeSubscription.remove(mNetSubcription);
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    private void fetchDataByNetWork() {
        String cityName = mSharedPreferenceUtil.getCityName();
        if (cityName != null) {
            cityName = Util.replaceCity(cityName);
        }
        RetrofitSingleton.getInstance().fetchWeather(cityName)      //fetch 获取
                .subscribe(weather -> {
                    normalStyleNotification(weather);
                });
    }

    private void normalStyleNotification(Weather weather) {
        Intent intent = new Intent(AutoUpdateService.this, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        /**
         * PendingIntent作用
         根据字面意思就知道是延迟的intent，主要用来在某个事件完成后执行特定的Action。PendingIntent包含了Intent及Context，所以就算Intent所属程序结束，PendingIntent依然有效，可以在其他程序中使用。
         常用在通知栏及短信发送系统中。
         PendingIntent一般作为参数传给某个实例，在该实例完成某个操作后自动执行PendingIntent上的Action，也可以通过PendingIntent的send函数手动执行，并可以在send函数中设置OnFinished表示send成功后执行的动作。
         */
        PendingIntent pendingIntent =
                PendingIntent.getActivity(AutoUpdateService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(AutoUpdateService.this);
        Notification notification = builder.setContentIntent(pendingIntent)
                .setContentTitle(weather.basic.city)
                .setContentText(String.format("%s 当前温度: %s℃ ", weather.now.cond.txt, weather.now.tmp))

                .setSmallIcon(mSharedPreferenceUtil.getInt(weather.now.cond.txt, R.mipmap.none))
                .build();
        notification.flags = mSharedPreferenceUtil.getNotificationModel();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // tag和id都是可以拿来区分不同的通知的
        manager.notify(1, notification);
    }
}
