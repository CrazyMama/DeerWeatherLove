package com.lu.deerweatherlove.modules.main.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L on 16/12/2.
 */
public class WeatherAPI {

    @SerializedName("HeWeather5") @Expose
    public List<Weather> mHeWeather5
            = new ArrayList<>();
}
