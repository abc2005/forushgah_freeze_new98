package com.persiandesigners.freeze;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.persiandesigners.freeze.Util.CountDown;

import java.util.List;

/**
 * Created by Navid on 4/5/2018.
 */

public class PishnahadVijeAdp extends RecyclerView.Adapter<PishnahadVijeAdp.MyViewHolder> {
    private LayoutInflater inflater;
    private List<PishnahadVijeItems> list;
    private Context ctx;
    private Typeface typeface;

    public PishnahadVijeAdp(Context context, List<PishnahadVijeItems> feedItemList) {
        if (context != null) {
            inflater = LayoutInflater.from(context);
            this.list = feedItemList;
            this.ctx = context;
            this.typeface = Func.getTypeface((Activity) context);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parrent, int i) {
        View view = inflater.inflate(R.layout.pishnahadvije_row, parrent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        PishnahadVijeItems item = list.get(position);

        viewHolder.onvan.setText(item.getName());
        String imgurl = item.getIcon();

        if (imgurl.length() > 5)
            Glide.with(ctx).load( imgurl).into(viewHolder.img);
        else
            viewHolder.img.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.ic_launcher));

        CountDown timer = new CountDown(item.getTimer(), 1000,viewHolder.timer);
        timer.start();
        if(item.getTimer()<0)
            viewHolder.timer.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if (list == null)
            return 0;
        else
            return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView onvan, timer;
        ImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            onvan = (TextView) itemView.findViewById(R.id.onvan);
            onvan.setTypeface(typeface);
            timer = (TextView) itemView.findViewById(R.id.timer);
            timer.setTypeface(typeface);
            img = (ImageView) itemView.findViewById(R.id.img);
        }

        @Override
        public void onClick(View v) {
            PishnahadVijeItems item = list.get(getAdapterPosition());
            Intent in = new Intent(ctx, Detailss.class);

            if(item.getLink().contains("http")){
                Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse(item.getLink()));
                ctx.startActivity(i);
            }else if (item.getLink().length() > 0) {
                in.putExtra("productid", item.getLink());
                ctx.startActivity(in);
            }
        }
    }
}
