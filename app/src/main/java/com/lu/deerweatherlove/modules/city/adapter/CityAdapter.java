package com.lu.deerweatherlove.modules.city.adapter;

import android.content.Context;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lu.deerweatherlove.R;
import com.lu.deerweatherlove.component.AnimRecyclerViewAdapter;


import java.util.ArrayList;

/**
 * Created by L on 16/12/11.
 * INfo: 创建适配器
 */

public class CityAdapter extends AnimRecyclerViewAdapter<CityAdapter.CityViewHolder> {

    private Context mContext;
    private ArrayList<String> dataList;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;



    public CityAdapter(Context context, ArrayList<String> dataList) {
        mContext = context;
        this.dataList = dataList;

    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CityViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_city, parent, false));
    }



    @Override
    public void onBindViewHolder(final CityViewHolder holder, final int position) {


        holder.bind(dataList.get(position));
        holder.cardView.setOnClickListener(v -> mOnItemClickListener.onItemClick(v, position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }




    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;

    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    class CityViewHolder extends RecyclerView.ViewHolder {
        TextView itemCity;
        CardView cardView;

        public CityViewHolder(View itemView) {
            super(itemView);

            itemCity = (TextView) itemView.findViewById(R.id.item_city);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
        }
        public  void bind(String name){itemCity.setText(name);}
    }
}
