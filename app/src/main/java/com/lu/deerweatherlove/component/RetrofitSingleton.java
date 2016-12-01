package com.lu.deerweatherlove.component;

/**
 * Created by L on 16/12/1.
 */
public class RetrofitSingleton {
    private static Object instance;

    public static void disposeFailureInfo(Throwable e) {

    }

    public static Object getInstance() {
        return instance;
    }
}
