package com.persiandesigners.freeze;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by navid on 5/29/2017.
 */
public class CatsAdapterDigi extends RecyclerView.Adapter<CatsAdapterDigi.MyViewHolder> {
    private LayoutInflater inflater;
    private List<CatItems> list;
    Typeface typeface;
    Context context;
    Boolean digikalaLike;

    public CatsAdapterDigi(Context context, List<CatItems> feedItemList) {
        inflater = LayoutInflater.from(context);
        this.list = feedItemList;
        typeface = Typeface.createFromAsset(context.getAssets(), "IRAN Sans Bold.ttf");
        this.context=context;
        digikalaLike=false;
    }

    public void addAll(List<CatItems> catItems) {
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
        View view = null;
        view=inflater.inflate(R.layout.row_list_digikala, parrent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        CatItems item = list.get(position);
        viewHolder.onvan.setText(item.getName());
        Glide.with(context).load(context.getString(R.string.url) + "Opitures/" + item.getThumb()).into(viewHolder.img);
    }

    @Override
    public int getItemCount() {
        if (list == null)
            return 0;
        else
            return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView onvan;
        ImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            onvan = (TextView) itemView.findViewById(R.id.text);
            onvan.setTypeface(typeface);
            img = (ImageView) itemView.findViewById(R.id.image);
        }

        @Override
        public void onClick(View v) {
            CatItems item = list.get(getAdapterPosition());
            Intent in = null;
            in = new Intent(context, Products.class);
            in.putExtra("catId", item.getId());
            in.putExtra("from", "cats");
            context.startActivity(in);
        }
    }
}
