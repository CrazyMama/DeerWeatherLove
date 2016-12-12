package com.lu.deerweatherlove.modules.main.view;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.lu.deerweatherlove.R;
import com.lu.deerweatherlove.base.BaseApplication;
import com.lu.deerweatherlove.base.BaseFragment;
import com.lu.deerweatherlove.common.utils.CheckVersion;
import com.lu.deerweatherlove.common.utils.SharedPreferenceUtil;
import com.lu.deerweatherlove.common.utils.SimpleSubscriber;
import com.lu.deerweatherlove.common.utils.ToastUtil;
import com.lu.deerweatherlove.common.utils.ULog;
import com.lu.deerweatherlove.common.utils.Util;
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

public class MainFragment extends BaseFragment implements AMapLocationListener {


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

    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = new AMapLocationClientOption();

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
            CheckVersion.checkVersion(getActivity());
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


        mSwiprefresh.setRefreshing(true);


        //初始化定位
        mLocationClient = new AMapLocationClient(BaseApplication.getmAppContext());
        //声明定位回调监听器

        //设置定位回调监听
        mLocationClient.setLocationListener(this);


        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔 单位毫秒
        int tempTime = SharedPreferenceUtil.getInstance().getAutoUpdate();
        if (tempTime == 0) {
            tempTime = 100;
        }
        mLocationOption.setInterval(tempTime * SharedPreferenceUtil.ONE_HOUR);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {


        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {

                amapLocation.getLocationType();

//                String city = amapLocation.getCity();
//                System.out.println(city.toString() + "...........................");
                SharedPreferenceUtil.getInstance().setCityName(Util.replaceCity(amapLocation.getCity()));


            } else {
                if (isAdded()) {

                    ToastUtil.showShort("定位失败，显示默认位置咯");
                }

            }
            load();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient = null;
        mLocationOption = null;


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
                //.setLargeIcon(BitmapFactory.decodeResource(getResources(),SharedPreferenceUtil.getInstance().getInt(weather.now.cond.txt, R.mipmap.none))
                .build();
        notification.flags = SharedPreferenceUtil.getInstance().getNotificationModel();
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        // tag和id都是可以拿来区分不同的通知的
        manager.notify(1, notification);
    }


}
