package com.lu.deerweatherlove.modules.main.view;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.lu.deerweatherlove.R;
import com.lu.deerweatherlove.base.BaseFragment;
import com.lu.deerweatherlove.common.utils.SharedPreferenceUtil;
import com.lu.deerweatherlove.common.utils.SimpleSubscriber;
import com.lu.deerweatherlove.common.utils.ToastUtil;
import com.lu.deerweatherlove.common.utils.ULog;
import com.lu.deerweatherlove.component.RetrofitSingleton;
import com.lu.deerweatherlove.component.RxBus;
import com.lu.deerweatherlove.modules.main.adapter.WeatherAdapter;
import com.lu.deerweatherlove.modules.main.domain.ChangeCityEvent;
import com.lu.deerweatherlove.modules.main.domain.Weather;
import com.tbruyelle.rxpermissions.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by L on 16/10/20.
 */

public class MainFragment extends BaseFragment {


    private static Weather mWeather = new Weather();
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.swiprefresh)
    SwipeRefreshLayout mSwiprefresh;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.iv_erro)
    ImageView mIvErro;
    private WeatherAdapter mAdapter;
   private Observer<Weather> observer;


    private View view;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.content_acitvity_main, container, false);
            ButterKnife.bind(this, view);
        }
        isCreateView = true;
        ULog.d("onCreateView");
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        /**
         * 参考于
         *  https://github.com/tbruyelle/RxPermissions
         */

        RxPermissions.getInstance(getActivity()).request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        location();
                    } else {
                       load();
                    }
                });
   //     CheckVersion.checkVersion(getActivity());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ULog.d("onCreate");
        RxBus.getDefault().toObserverable(ChangeCityEvent.class)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                new SimpleSubscriber<ChangeCityEvent>() {
                    @Override
                    public void onNext(ChangeCityEvent changeCityEvent) {
                        if (mSwiprefresh != null) {
                            mSwiprefresh.setRefreshing(true);
                        }
                        load();
                        ULog.d("MainRxBus");
                    }
                });
    }

    private void initView() {
        if (mSwiprefresh != null) {
            mSwiprefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
           mSwiprefresh.setOnRefreshListener(
                    () -> mSwiprefresh.postDelayed(this::load, 1000));
        }

        mRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
       mAdapter = new WeatherAdapter(mWeather);
       mRecyclerview.setAdapter(mAdapter);
    }

    private void load() {
        fetchDataByNetWork()
                .doOnRequest(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        mSwiprefresh.setRefreshing(true);
                    }
                })
                .doOnError(throwable -> {
                    mIvErro.setVisibility(View.VISIBLE);
                    mRecyclerview.setVisibility(View.GONE);
                    SharedPreferenceUtil.getInstance().setCityName("保定");
                    safeSetTitle("找不到城市啦");
                })
                .doOnNext(weather -> {
                    mIvErro.setVisibility(View.GONE);
                    mRecyclerview.setVisibility(View.VISIBLE);
                })
                .doOnTerminate(() -> {
                    mSwiprefresh.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);
                }).subscribe(new Subscriber<Weather>() {
            @Override
            public void onCompleted() {
                ToastUtil.showShort(getString(R.string.complete));
            }

            @Override
            public void onError(Throwable e) {
                ULog.e(e.toString());
                RetrofitSingleton.disposeFailureInfo(e);
            }

            @Override
            public void onNext(Weather weather) {
                mWeather.status = weather.status;
                mWeather.aqi = weather.aqi;
                mWeather.basic = weather.basic;
                mWeather.suggestion = weather.suggestion;
                mWeather.now = weather.now;
                mWeather.dailyForecast = weather.dailyForecast;
                mWeather.hourlyForecast = weather.hourlyForecast;
                //mActivity.getToolbar().setTitle(weather.basic.city);
                safeSetTitle(weather.basic.city);
                mAdapter.notifyDataSetChanged();
                normalStyleNotification(weather);
            }
        });
    }

    /**
     * 从网络获取
     */
    private Observable<Weather> fetchDataByNetWork() {
        String cityName = SharedPreferenceUtil.getInstance().getCityName();
        return RetrofitSingleton.getInstance()
                .fetchWeather(cityName)
                .compose(this.bindToLifecycle());

    }

    /**
     * 高德定位
     */
    private void location() {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    /**
     * 加载数据操作,在视图创建之前初始化
     */
    @Override
    protected void lazyLoad() {






    }

    private void normalStyleNotification(Weather weather) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(getActivity());
        Notification notification = builder.setContentIntent(pendingIntent)
                .setContentTitle(weather.basic.city)
                .setContentText(String.format("%s 当前温度: %s℃ ", weather.now.cond.txt, weather.now.tmp))
                // 这里部分 ROM 无法成功
                .setSmallIcon(SharedPreferenceUtil.getInstance().getInt(weather.now.cond.txt, R.mipmap.none))
                .build();
        notification.flags = SharedPreferenceUtil.getInstance().getNotificationModel();
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        // tag和id都是可以拿来区分不同的通知的
        manager.notify(1, notification);
    }
}
