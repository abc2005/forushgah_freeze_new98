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
import com.persiandesigners.freeze.Util.ShopOptionAdapter_sub_cat;

import java.util.List;

public class SubCatsAdapterPics extends RecyclerView.Adapter<SubCatsAdapterPics.MyViewHolder> {
    private LayoutInflater inflater;
    private List<CatsItems> list;
    private Context ctx;
    private Typeface typeface;
    int pos = -1;
    ShopOptionAdapter_sub_cat interfaceListener;
    Boolean fromHomePage = false;

    public SubCatsAdapterPics(Context context, List<CatsItems> Lists, ShopOptionAdapter_sub_cat interfaceListener) {
        if (context != null) {
            inflater = LayoutInflater.from(context);
            this.list = Lists;
            this.ctx = context;
            this.typeface = Typeface.createFromAsset(ctx.getAssets(), "IRAN Sans Bold.ttf");
            this.interfaceListener = interfaceListener;
        }
    }

    //fromHomePage
    public SubCatsAdapterPics(Context context, List<CatsItems> Lists) {
        if (context != null) {
            fromHomePage = true;
            inflater = LayoutInflater.from(context);
            this.list = Lists;
            this.ctx = context;
            this.typeface = Typeface.createFromAsset(ctx.getAssets(), "IRAN Sans Bold.ttf");
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parrent, int i) {
        View view = inflater.inflate(R.layout.cat_row2, parrent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        CatsItems item = list.get(position);
        viewHolder.title.setText(item.getName());
		
		if(item.getIcon().length()<3)
            viewHolder.image.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.ic_launcher));
        else
            Glide.with(ctx).load(ctx.getString(R.string.url) + "Opitures/" + item.getIcon()).into(viewHolder.image);
    }

    @Override
    public int getItemCount() {
        if (list == null)
            return 0;
        else
            return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.onvan);
            title.setTypeface(typeface);
            image = (ImageView) itemView.findViewById(R.id.image);
        }

        @Override
        public void onClick(View v) {
            CatsItems item = list.get(getAdapterPosition());
            if (fromHomePage) {
                Intent in = null;
                if (Func.isMultiSeller(ctx))
                    in = new Intent(ctx, Shops.class);
                else if (item.getHasSubCat().equals("0"))
                    in = new Intent(ctx, Productha.class);
                else
                    in = new Intent(ctx, Subcats.class);
                in.putExtra("catId",item.getId());
                in.putExtra("onvan",item.getName());
                ctx.startActivity(in);
            } else {
                Intent in = new Intent(ctx, Productha.class);
                in.putExtra("catId", item.getParrent());
                in.putExtra("chooseId", item.getId());
                in.putExtra("onvan", item.getName());
                ctx.startActivity(in);
            }
        }


    }
}
