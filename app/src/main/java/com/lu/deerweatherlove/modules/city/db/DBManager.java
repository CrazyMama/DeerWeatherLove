package com.lu.deerweatherlove.modules.city.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.lu.deerweatherlove.R;
import com.lu.deerweatherlove.base.BaseApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by L on 16/12/11.
 * Info: 数据库的管理类
 */

public class DBManager {
    private static String TAG = DBManager.class.getSimpleName();
    private final int BUFFER_SIZE = 400000;
    public static final String DB_NAME = "china_city.db";
    public static final String PACKAGE_NAME = "com.lu.deerweatherlove";

    public static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath()

            + "/" + PACKAGE_NAME;

    private SQLiteDatabase database;
    private Context context;

    public static DBManager getInstane() {
        return DBManagerHolder.sInstance;
    }

    private static final class DBManagerHolder {
        public static final DBManager sInstance = new DBManager();

    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void openDatabase() {
        this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
    }

    @Nullable
    private SQLiteDatabase openDatabase(String dbfile) {
        try {
            /**
             * 判断数据库是否存在 不存在倒入，存在打开
             */

            if (!(new File(dbfile).exists())) {
                //导入
                InputStream inputStream = BaseApplication.getmAppContext()
                        .getResources()
                        .openRawResource(R.raw.china_city);

                FileOutputStream fos = new FileOutputStream(dbfile);

                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = inputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);

                }
                fos.close();
                inputStream.close();
            }
            //打开
            return SQLiteDatabase.openOrCreateDatabase(dbfile, null);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public void closeDatabase() {
        if (this.database != null) {
            this.database.close();
        }
    }
}
