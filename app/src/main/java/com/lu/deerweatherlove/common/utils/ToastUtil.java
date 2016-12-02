package com.lu.deerweatherlove.common.utils;

import android.widget.Toast;

import com.lu.deerweatherlove.base.BaseApplication;

/**
 * Created by L on 16/11/22.
 */
public class ToastUtil {

    public static void showShort(String msg) {
        Toast.makeText(BaseApplication.getmAppContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(String msg) {
        Toast.makeText(BaseApplication.getmAppContext(), msg, Toast.LENGTH_LONG).show();
    }
}
