package com.lu.deerweatherlove.component;

import com.lu.deerweatherlove.modules.about.domain.VersionAPI;
import com.lu.deerweatherlove.modules.main.domain.WeatherAPI;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by L on 16/11/22.
 * Info:
 */
public interface ApiInterface {

    String HOST = "https://free-api.heweather.com/v5/";

    @GET("weather")
    Observable<WeatherAPI> mWeatherAPI(@Query("city") String city, @Query("key") String key);

    //而且在Retrofit 2.0中我们还可以在@Url里面定义完整的URL：这种情况下Base URL会被忽略。
    @GET("http://api.fir.im/apps/latest/deaa1dbc0b11ba80e0895f3b7fcbe841")
    Observable<VersionAPI> mVersionAPI(
            @Query("api_token") String api_token);
}
