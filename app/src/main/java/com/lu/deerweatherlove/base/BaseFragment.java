package com.lu.deerweatherlove.base;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.trello.rxlifecycle.components.support.RxFragment;

/**
 * Created by L on 16/10/20.
 */
public abstract class BaseFragment extends RxFragment {

    protected boolean isCreateView=false;
    //此方法在控件初始化前调用
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser&& isCreateView){

            lazyLoad(); //延迟加载
        }
    }
    /**
     * 加载数据操作,在视图创建之前初始化
     */
    protected abstract void lazyLoad() ;//延迟加载


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //第一个fragment会调用
        if (getUserVisibleHint()) {
            lazyLoad();
        }
    }
    protected void safeSetTitle(String title) {
        ActionBar appBarLayout = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (appBarLayout != null) {

            appBarLayout.setTitle(title);
        }
    }

}
