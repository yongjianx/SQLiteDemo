package com.example.skyworthclub.sqlitedatabase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import SQLite.Order;

/**
 * Created by skyworthclub on 2018/7/14.
 */

public class OrderListAdapter extends BaseAdapter {
    private Context context;
    private List<Order> orderList;

    public OrderListAdapter(Context context, List<Order> orderList){
        this.context = context;
        this.orderList = orderList;
    }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Order order = orderList.get(position);
        if (order == null){
            return null;
        }

        ViewHolder holder = null;
        if (view != null){
            holder = (ViewHolder) view.getTag();
        }else {
            view = LayoutInflater.from(context).inflate(R.layout.sql_item, null);

            holder = new ViewHolder();
            holder.idTextView = view.findViewById(R.id.dateIdTextView);
            holder.customTextView = view.findViewById(R.id.dateCustomTextView);
            holder.orderPriceTextView = view.findViewById(R.id.dateOrderPriceTextView);
            holder.countoryTextView = view.findViewById(R.id.dateCountoryTextView);

            view.setTag(holder);
        }

        holder.idTextView.setText(order.id + "");
        holder.customTextView.setText(order.customName);
        holder.orderPriceTextView.setText(order.orderPrice + "");
        holder.countoryTextView.setText(order.country);

        return view;
    }


    public class ViewHolder{
        public TextView idTextView;
        public TextView customTextView;
        public TextView orderPriceTextView;
        public TextView countoryTextView;
    }
}
