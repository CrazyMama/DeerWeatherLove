package com.lu.deerweatherlove.component;

import android.support.v4.media.MediaBrowserCompat;

import com.lu.deerweatherlove.BuildConfig;
import com.lu.deerweatherlove.base.BaseApplication;
import com.lu.deerweatherlove.base.Constant;
import com.lu.deerweatherlove.common.utils.RxUtils;
import com.lu.deerweatherlove.common.utils.ToastUtil;
import com.lu.deerweatherlove.common.utils.ULog;
import com.lu.deerweatherlove.common.utils.Util;
import com.lu.deerweatherlove.modules.about.domain.VersionAPI;
import com.lu.deerweatherlove.modules.main.domain.Weather;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by L on 16/11/22.
 * <p>
 * 对 Retrofit 和 okHttp的封装
 */
public class RetrofitSingleton {
    private static ApiInterface apiService = null;
    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient = null;

    private void init() {
        initOkHttp();
        initRetrofit();
        apiService = retrofit.create(ApiInterface.class);
    }

    private RetrofitSingleton() {
        init();
    }

    public static RetrofitSingleton getInstance() {
        return SingletonHolder.INSTANCE;
    }



    private static class SingletonHolder {
        private static final RetrofitSingleton INSTANCE = new RetrofitSingleton();
    }

    private static void initOkHttp() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            /**
             *
             * 参考：https://drakeet.me/retrofit-2-0-okhttp-3-0-config
             */
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.addInterceptor(loggingInterceptor);
        }
        /**
         *   缓存 http://www.jianshu.com/p/93153b34310e
          */


        File cacheFile = new File(BaseApplication.cacheDir, "/NetCache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);
        Interceptor cacheInterceptor = chain -> {
            Request request = chain.request();
            if (!Util.isNetworkConnected(BaseApplication.getmAppContext())) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response response = chain.proceed(request);
            if (Util.isNetworkConnected(BaseApplication.getmAppContext())) {
                int maxAge = 0;
                // 有网络时 设置缓存超时时间0个小时
                response.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                // 无网络时，设置超时为4周
                int maxStale = 60 * 60 * 24 * 28;
                response.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            return response;
        };
        builder.cache(cache).addInterceptor(cacheInterceptor);
        //设置超时
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);
        okHttpClient = builder.build();
    }

    private static void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface.HOST)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public static void disposeFailureInfo(Throwable t) {
        if (t.toString().contains("GaiException") || t.toString().contains("SocketTimeoutException") ||
                t.toString().contains("UnknownHostException")) {
            ToastUtil.showShort("网络问题");
        } else if (t.toString().contains("API没有")) {
          //  OrmLite.getInstance().delete(new WhereBuilder(CityORM.class).where("name=?", Util.replaceInfo(t.getMessage())));
            ULog.w(Util.replaceInfo(t.getMessage()));
            ToastUtil.showShort("错误: " + t.getMessage());
        }
        ULog.w(t.getMessage());
    }

    public ApiInterface getApiService() {
        return apiService;
    }

    public Observable<Weather> fetchWeather(String city) {

        return apiService.mWeatherAPI(city, Constant.KEY).flatMap(weatherAPI -> {
            String status = weatherAPI.mHeWeather5.get(0).status;
            if ("no more requests".equals(status)) {
                return Observable.error(new RuntimeException("/(ㄒoㄒ)/~~,API免费次数已用完"));
            } else if ("unknown city".equals(status)) {
                return Observable.error(new RuntimeException(String.format("API没有%s", city)));
            }
            return Observable.just(weatherAPI);
        })
                .map(weatherAPI -> weatherAPI.mHeWeather5.get(0))
                .compose(RxUtils.rxSchedulerHelper());
    }

    public Observable<VersionAPI> fetchVersion() {
        return apiService.mVersionAPI(Constant.API_TOKEN).compose(RxUtils.rxSchedulerHelper());
    }
}