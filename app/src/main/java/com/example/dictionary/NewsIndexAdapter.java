package com.example.dictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.newsFetcher.NewsOverview;

import java.util.List;

public class NewsIndexAdapter extends BaseAdapter {
    private Context mContext;
    private List<NewsOverview> list;
    private int layoutId;
    private ViewHolder viewHolder = null;

    public NewsIndexAdapter(Context mContext, List<NewsOverview> list, int layoutId) {
        this.mContext = mContext;
        this.list = list;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(layoutId, null);

            viewHolder.txtTitle = view.findViewById(R.id.txt_title);
            viewHolder.txtDate = view.findViewById(R.id.txt_date);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        //Log.d("#123",list.get(position).getNewsTitle());
        viewHolder.txtTitle.setText(list.get(position).getNewsTitle());
        viewHolder.txtDate.setText(list.get(position).getNewsDate());

        return view;
    }

    public class ViewHolder {
        TextView txtTitle, txtDate;
    }
}
