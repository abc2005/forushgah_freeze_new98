package com.persiandesigners.freeze;

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
 * Created by navid on 12/29/2016.
 */
public class Blog_Adapter extends RecyclerView.Adapter<Blog_Adapter.MyViewHolder> {
    private LayoutInflater inflater;
    private List<Blog_items> list;
    private Context ctx;
    private Typeface typeface;

    public Blog_Adapter(Context context, List<Blog_items> Lists) {
        if(context!=null) {
            inflater= LayoutInflater.from(context);
            this.list= Lists;
            this.ctx= context;
            this.typeface= Typeface.createFromAsset(ctx.getAssets(), "IRAN Sans Bold.ttf");
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parrent, int i) {
        View view = inflater.inflate(R.layout.blog_row, parrent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        Blog_items item = list.get(position);
        viewHolder.title.setText(item.getOnvan());
        String imgurl = item.getImg();
        if (imgurl.length() >5)
            Glide.with(ctx).load(ctx.getString(R.string.url) + "NewsPictures/" + imgurl).into(viewHolder.img);
        else
            viewHolder.img.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.ic_launcher));
    }

    @Override
    public int getItemCount() {
        if(list==null)
            return 0;
        else
            return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.onvan);
            title.setTypeface(typeface);
            img= (ImageView) itemView.findViewById(R.id.img);
        }

        @Override
        public void onClick(View v) {
            Blog_items item = list.get(getAdapterPosition());
            Intent in = new Intent(ctx, Blog_Details.class);
            in.putExtra("id", item.getId());
            in.putExtra("onvan", item.getOnvan());
            in.putExtra("img",item.getImg());
            ctx.startActivity(in);
        }
    }
}

