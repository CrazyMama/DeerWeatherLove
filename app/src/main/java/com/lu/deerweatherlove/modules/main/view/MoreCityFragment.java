package com.lu.deerweatherlove.modules.main.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.litesuits.orm.db.assit.WhereBuilder;
import com.lu.deerweatherlove.R;
import com.lu.deerweatherlove.base.BaseFragment;
import com.lu.deerweatherlove.base.Constant;
import com.lu.deerweatherlove.common.utils.RxUtils;
import com.lu.deerweatherlove.common.utils.SimpleSubscriber;
import com.lu.deerweatherlove.common.utils.ULog;
import com.lu.deerweatherlove.common.utils.Util;
import com.lu.deerweatherlove.component.OrmLite;
import com.lu.deerweatherlove.component.RetrofitSingleton;
import com.lu.deerweatherlove.component.RxBus;
import com.lu.deerweatherlove.modules.main.adapter.MoreCityAdapter;
import com.lu.deerweatherlove.modules.main.domain.CityORM;
import com.lu.deerweatherlove.modules.main.domain.MoreUpdate;
import com.lu.deerweatherlove.modules.main.domain.Weather;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;

/**
 * Created by L on 16/12/5.
 */
public class MoreCityFragment extends BaseFragment {


    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.swiprefresh)
    SwipeRefreshLayout mSwiprefresh;
    @BindView(R.id.empty)
    LinearLayout linearLayout;

    private MoreCityAdapter mAdatper;
    private List<Weather> weatherArrayList;

    private View view;

    /**
     * 加载数据操作,在视图创建之前初始化
     */
    @Override
    protected void lazyLoad() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_morecity, container, false);
            ButterKnife.bind(this, view);
        }
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.getDefault().toObserverable(MoreUpdate.class).subscribe(new SimpleSubscriber<MoreUpdate>() {
            @Override
            public void onNext(MoreUpdate moreUpdate) {
                moreLoad();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        moreLoad();
    }

    private void initView() {
        weatherArrayList = new ArrayList<>();
        mAdatper = new MoreCityAdapter(weatherArrayList);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerview.setAdapter(mAdatper);
        mAdatper.setOnMoreCityLongClick(new MoreCityAdapter.onMoreCityLongClick() {
            @Override
            public void longClick(String city) {
                new AlertDialog.Builder(getActivity()).setMessage("是否删除该城市?")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                OrmLite.getInstance().delete(new WhereBuilder(CityORM.class).where("name=?", city));
                                OrmLite.OrmTest(CityORM.class);
                                moreLoad();
                                Snackbar.make(getView(), "已经将" + city + "删掉了", Snackbar.LENGTH_LONG).setAction("撤销",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                OrmLite.getInstance().save(new CityORM(city));
                                                moreLoad();
                                            }
                                        }).show();
                            }
                        })
                        .show();
            }
        });

        if (mSwiprefresh != null) {
            mSwiprefresh.setColorSchemeResources(
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light,
                    android.R.color.holo_green_light,
                    android.R.color.holo_blue_bright
            );
            mSwiprefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mSwiprefresh.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            moreLoad();
                        }
                    }, 1000);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    private void moreLoad() {
        weatherArrayList.clear();

        Observable.defer(() -> Observable.from(OrmLite.getInstance().query(CityORM.class)))
                .doOnRequest(aLong -> mSwiprefresh.setRefreshing(true))
                .map(cityORM -> Util.replaceCity(cityORM.getName()))
                .distinct()
                .flatMap(s -> {
                    return RetrofitSingleton.getInstance()
                            .getApiService()
                            .mWeatherAPI(s, Constant.KEY)
                            .map(weatherAPI -> weatherAPI.mHeWeather5.get(0))
                            .compose(RxUtils.rxSchedulerHelper());

                })
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .filter(weather -> !Constant.UNKNOW_CITY.equals(weather.status))
                .take(3)
                .doOnTerminate(() -> {
                    // 因为 flatmap 换了新的流,所以得在这里
                    mSwiprefresh.setRefreshing(false);
                })
                .subscribe(new Observer<Weather>() {
                    @Override
                    public void onCompleted() {
                        mAdatper.notifyDataSetChanged();
                        ULog.d("complete" + weatherArrayList.size() + "");
                        if (mAdatper.isEmpty()) {
                            linearLayout.setVisibility(View.VISIBLE);
                        } else {
                            linearLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mAdatper.isEmpty() && linearLayout != null) {
                            linearLayout.setVisibility(View.VISIBLE);
                        }
                        RetrofitSingleton.disposeFailureInfo(e);
                    }

                    @Override
                    public void onNext(Weather weather) {
                        weatherArrayList.add(weather);
                    }
                });
    }
}
