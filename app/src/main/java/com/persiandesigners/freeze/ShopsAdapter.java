package com.persiandesigners.freeze;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Navid on 4/18/2018.
 */

public class ShopsAdapter extends RecyclerView.Adapter<ShopsAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private List<Shops_Items> list;
    private Context ctx;
    private Typeface typeface;

    public ShopsAdapter(Context context, List<Shops_Items> feedItemList) {
        if (context != null) {
            inflater = LayoutInflater.from(context);
            this.list = feedItemList;
            this.ctx = context;
            this.typeface = Func.getTypeface((Activity)context);
        }
    }

    public void addAll(List<Shops_Items> catItems) {
        if (this.list == null) {
            this.list = catItems;
        } else {
            this.list.addAll(catItems);
        }
        notifyDataSetChanged();
        notifyItemInserted(list.size());
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parrent, int i) {
        View view = inflater.inflate(R.layout.shops_items, parrent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        Shops_Items item = list.get(position);
        viewHolder.onvan.setText(item.getname());
        viewHolder.tozih.setText(item.getabout());
        viewHolder.time_deliver.setText(item.getZaman_tahvil());
        viewHolder.hadeaghal.setText("حداقل سفارش "+item.getMinimum_order_amount() + " تومان");

        String imgurl = item.getlogo();
        if (imgurl.length() > 5)
            Glide.with(ctx).load(ctx.getString(R.string.url) + "Opitures/" + imgurl).into(viewHolder.img);
        else
            viewHolder.img.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.ic_launcher));

        if(item.getIsOpen()==0)
            viewHolder.tvIsClose.setVisibility(View.VISIBLE);
        else
            viewHolder.tvIsClose.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if (list == null)
            return 0;
        else
            return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView onvan,tozih, time_deliver ,hadeaghal,tvIsClose;
        ImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            onvan = (TextView) itemView.findViewById(R.id.onvan);
            onvan.setTypeface(typeface);
            time_deliver = (TextView) itemView.findViewById(R.id.time_deliver);
            time_deliver.setTypeface(typeface);
            hadeaghal = (TextView) itemView.findViewById(R.id.hadeaghal);
            hadeaghal.setTypeface(typeface);
            tozih = (TextView) itemView.findViewById(R.id.tozih);
            tozih.setTypeface(typeface);
            img = (ImageView) itemView.findViewById(R.id.img);
            tvIsClose = (TextView) itemView.findViewById(R.id.tvIsClose);
            tvIsClose.setTypeface(typeface);
        }

        @Override
        public void onClick(View v) {
            Shops_Items item = list.get(getAdapterPosition());
            Intent in = new Intent(ctx, Subcats.class);
            in.putExtra("catId",item.getCatId());
            in.putExtra("onvan", item.getname());
            ctx.startActivity(in);
        }
    }
}