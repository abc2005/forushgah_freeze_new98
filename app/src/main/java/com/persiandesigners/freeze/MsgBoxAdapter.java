package com.persiandesigners.freeze;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MsgBoxAdapter extends RecyclerView.Adapter<MsgBoxAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private List<MsgBox_Items> list;
    private Context ctx;
    private Typeface typeface;

    public MsgBoxAdapter(Context context, List<MsgBox_Items> feedItemList) {
        if(context!=null) {
            inflater= LayoutInflater.from(context);
            this.list= feedItemList;
            this.ctx= context;
            this.typeface= Func.getTypeface((Activity)context);
        }
    }

    public void addAll(List<MsgBox_Items> catItems) {
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
        View view = inflater.inflate(R.layout.msgbox_items, parrent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        MsgBox_Items item = list.get(position);
        viewHolder.onvan.setText(item.getonvan());
        viewHolder.msg.setText(item.gettext());
        viewHolder.dates.setText(item.getdates());
    }

    @Override
    public int getItemCount() {
        if(list==null)
            return 0;
        else
            return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView onvan,dates,msg;

        public MyViewHolder(View itemView) {
            super(itemView);
            onvan = (TextView) itemView.findViewById(R.id.tv_onvan);
            onvan.setTypeface(typeface);
            dates = (TextView) itemView.findViewById(R.id.tv_date);
            dates.setTypeface(typeface);
            msg = (TextView) itemView.findViewById(R.id.tv_msg);
            msg.setTypeface(typeface);
        }

    }
}