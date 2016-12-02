package com.lu.deerweatherlove.modules.main.domain;

/**
 * Created by L on 16/11／22.
 */
public class ChangeCityEvent {

    String city;
    boolean isSetting;

    public ChangeCityEvent() {
    }

    public ChangeCityEvent(boolean isSetting) {
        this.isSetting = isSetting;
    }

    public ChangeCityEvent(String city) {
        this.city = city;
    }
}
