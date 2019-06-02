package com.persiandesigners.freeze;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by navid on 6/21/2016.
 */
public class ListAdapterCat extends BaseAdapter {
    Activity activity;
    public ArrayList<HashMap<String, String>> list;
    Typeface typeface ;
    HashMap<String, String> item;

    public ListAdapterCat(Activity activity, ArrayList<HashMap<String, String>> list, Typeface iranSans) {
        super();
        this.activity = activity;
        this.list = list;
        typeface=iranSans;
    }

    public HashMap<String, String> geting(int position) {
        return list.get(position);
    }

    public void addAll(ArrayList<HashMap<String, String>> result) {
        if (this.list == null) {
            this.list = result;
        } else {
            this.list.addAll(result);
        }
        notifyDataSetChanged();
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int arg0) {
        return 0;
    }

    private class ViewHolder {
        TextView onvan;
        ImageView img;
        CardView card_view ;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.cat_row, null);
            holder = new ViewHolder();
            holder.onvan = (TextView) convertView.findViewById(R.id.onvan);
            holder.onvan.setTypeface(typeface);
            holder.img = (ImageView) convertView.findViewById(R.id.image);
            holder.card_view=(CardView)convertView.findViewById(R.id.card_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        item = list.get(position);
        holder.onvan.setText(item.get("name"));
        String imgurl = item.get("img");
        if (imgurl.length() > 5)
            Glide.with(activity).load(activity.getString(R.string.url) + "Opitures/" + imgurl).into(holder.img);
        else
            holder.img.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_launcher));

        return convertView;
    }
}
