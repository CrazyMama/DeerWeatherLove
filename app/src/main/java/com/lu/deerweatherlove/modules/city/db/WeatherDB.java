package com.lu.deerweatherlove.modules.city.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lu.deerweatherlove.common.utils.Util;
import com.lu.deerweatherlove.modules.city.domain.City;
import com.lu.deerweatherlove.modules.city.domain.Province;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

/**
 * Created by L on 16/12/11.
 * Info : 封装数据库操作
 */

public class WeatherDB {

    public WeatherDB() {
    }

    public static List<Province> loadProvinces(SQLiteDatabase database) {
        ArrayList<Province> list = new ArrayList<>();
        Cursor cursor = database.query("T_province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.ProSort = cursor.getInt(cursor.getColumnIndex("ProSort"));
                province.ProName = cursor.getString(cursor.getColumnIndex("ProName"));
                list.add(province);
            } while (cursor.moveToNext());
        }
        Util.closeQuietly(cursor);
        return list;

    }

    public static List<City> loadCities(SQLiteDatabase database, int ProID) {
        ArrayList<City> list = new ArrayList<>();
        Cursor cursor = database.query("T_City", null, "ProID = ?", new String[]{String.valueOf(ProID)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.CityName = cursor.getString(cursor.getColumnIndex("CityName"));
                city.ProID = ProID;
                city.CitySort = cursor.getInt(cursor.getColumnIndex("CitySort"));
                list.add(city);

            } while (cursor.moveToNext());

        }
        Util.closeQuietly(cursor);
        return list;
    }
}

