package com.lu.deerweatherlove.modules.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;

import com.lu.deerweatherlove.modules.main.view.MainActivity;

import java.lang.ref.WeakReference;

/**
 * mail: 1207660766@qq.com
 * <p>
 * /**
 * Created by L on 16/10/19.
 * 闪屏页
 * <p>
 * Activity在被结束之后，MessageQueue并不会随之被结束，如果这个消息队列中存在msg，则导致持有handler的引用，但是又
 * <p>
 * 由于Activity被结束了，msg无法被处理，从而导致永久持有handler对象，handler永久持有Activity对象，于是发生内存泄漏。但是为什么为static类型就
 * <p>
 * 会解决这个问题呢？因为在java中所有非静态的对象都会持有当前类的强引用，而静态对象则只会持有当前类的弱引用。声明为静态后，handler将会持
 * <p>
 * 有一个Activity的弱引用，而弱引用会很容易被gc回收，这样就能解决Activity结束后，gc却无法回收的情况。
 */

public class SplashActivity extends Activity {
    /**
     * 根据API中的定义：
     * Class.getName()：以String的形式，返回Class对象的“实体”名称；
     * Class.getSimpleName()：获取源代码中给出的“底层类”简称。
     * <p>
     * <p>
     * <p>
     * getName ----“实体名称” ---- com.se7en.test.Main
     * getSimpleName ---- “底层类简称” ---- Main
     */
    public static final String TAG = SplashActivity.class.getSimpleName();

    private SwitchHandler mHandler = new SwitchHandler(Looper.getMainLooper(), this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler.sendEmptyMessageDelayed(1, 2000);
    }

    class SwitchHandler extends Handler {


        private WeakReference<SplashActivity> mWeakReference;

        public SwitchHandler(Looper mLooper, SplashActivity activity) {
            super(mLooper);
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            SplashActivity.this.startActivity(intent);
            //activity的淡出效果
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }
}
