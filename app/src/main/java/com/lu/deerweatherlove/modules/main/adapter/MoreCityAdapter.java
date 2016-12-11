package com.lu.deerweatherlove.modules.main.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lu.deerweatherlove.R;
import com.lu.deerweatherlove.common.utils.SharedPreferenceUtil;
import com.lu.deerweatherlove.common.utils.ULog;
import com.lu.deerweatherlove.common.utils.Util;
import com.lu.deerweatherlove.modules.main.domain.Weather;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by L on 16/12/5.
 */
public class MoreCityAdapter extends RecyclerView.Adapter<MoreCityAdapter.MultiCityViewHolder> {

    private Context mContext;
    private List<Weather> mWeatherList;
    private onMoreCityLongClick onMoreCityLongClick = null;

    public void setOnMoreCityLongClick(onMoreCityLongClick onMultiCityLongClick) {
        this.onMoreCityLongClick = onMultiCityLongClick;
    }


    public MoreCityAdapter(List<Weather> weatherList) {
        mWeatherList = weatherList;
    }

    @Override
    public MultiCityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new MultiCityViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_more_city, parent, false));
    }

    @Override
    public void onBindViewHolder(MultiCityViewHolder holder, int position) {

        holder.invoke(mWeatherList.get(position));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            /**
             * 　在Android　App应用中，OnLongClick事件表示长按2秒以上触发的事件，本章我们通过长按图像设置为墙纸来理解其具体用法。

             　　知识点：OnLongClickListener
             　　OnLongClickListener接口与之前介绍的OnClickListener接口原理基本相同，只是该接口为View长按事件的捕捉接口，即当长时间按下某个View时触发的事件，该接口对应的回调方法签名如下。
             　　public boolean onLongClick(View v)
             　　参数v：参数v为事件源控件，当长时间按下此控件时才会触发该方法。
             　　返回值：该方法的返回值为一个boolean类型的变量，当返回true时，表示已经完整地处理了这个事件，并不希望其他的回调方法再次进行处理；当返回false时，表示并没有完全处理完该事件，更希望其他方法继续对其进行处理。

             * @param v
             * @return
             */
            @Override
            public boolean onLongClick(View v) {
                onMoreCityLongClick.longClick(mWeatherList.get(holder.getAdapterPosition()).basic.city);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWeatherList.size();
    }

    public boolean isEmpty() {
        return 0 == mWeatherList.size();
    }

    class MultiCityViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.dialog_city)
        TextView mDialogCity;
        @BindView(R.id.dialog_icon)
        ImageView mDialogIcon;
        @BindView(R.id.dialog_temp)
        TextView mDialogTemp;

        @BindView(R.id.cardView)
        CardView mCardView;

        public MultiCityViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void invoke(Weather mWeather) {


            try {
                mDialogCity.setText(Util.safeText(mWeather.basic.city));
                mDialogTemp.setText(String.format("%s℃", mWeather.now.tmp));
            } catch (NullPointerException e) {
                ULog.e(e.getMessage());
            }


            Glide.with(mContext).load(SharedPreferenceUtil.getInstance().getInt(mWeather.now.cond.txt, R.mipmap.none
            )).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    mDialogIcon.setImageBitmap(resource);
                    mDialogIcon.setColorFilter(Color.WHITE);
                }
            });

            int code = Integer.valueOf(mWeather.now.cond.code);
            if (code == 100) {
                mCardView.setBackground(ContextCompat.getDrawable(mContext, R.mipmap.dialog_bg_sunny));
            } else if (code >= 300 && code < 408) {
                mCardView.setBackground(ContextCompat.getDrawable(mContext, R.mipmap.dialog_bg_rainy));
            } else {
                mCardView.setBackground(ContextCompat.getDrawable(mContext, R.mipmap.dialog_bg_cloudy));
            }

            ULog.d(mWeather.now.cond.txt + " " + mWeather.now.cond.code);
        }
    }

    public interface onMoreCityLongClick {
        void longClick(String city);
    }
}
