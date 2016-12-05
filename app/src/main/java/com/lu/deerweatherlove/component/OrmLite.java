package com.lu.deerweatherlove.component;

import com.litesuits.orm.LiteOrm;
import com.lu.deerweatherlove.BuildConfig;
import com.lu.deerweatherlove.base.BaseApplication;
import com.lu.deerweatherlove.base.Constant;
import com.lu.deerweatherlove.common.utils.RxUtils;
import com.lu.deerweatherlove.common.utils.SimpleSubscriber;
import com.lu.deerweatherlove.common.utils.ULog;
import com.lu.deerweatherlove.modules.main.domain.CityORM;

import rx.Observable;

/**
 * Created by L on 16/12/5.
 * <p>
 * Info: Orm 数据库的封装
 */
public class OrmLite {
    static LiteOrm liteOrm;

    public static LiteOrm getInstance() {
        getOrmHolder();
        return liteOrm;
    }

    private static OrmLite getOrmHolder() {
        return OrmHolder.sInstance;
    }

    private OrmLite() {
        if (liteOrm == null) {
            liteOrm = LiteOrm.newSingleInstance(BaseApplication.getmAppContext(), Constant.ORM_NAME);

        }
        liteOrm.setDebugged(BuildConfig.DEBUG);
    }

    private static class OrmHolder {
        private static final OrmLite sInstance = new OrmLite();
    }

    public static <T> void OrmTest(Class<T> t) {
        Observable.from(getInstance().query(t))
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(new SimpleSubscriber<T>() {
                    @Override
                    public void onNext(T t) {
                        if (t instanceof CityORM) {
                            ULog.w(((CityORM) t).getName());
                        }
                    }
                });
    }
}
