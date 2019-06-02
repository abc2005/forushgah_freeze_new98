package com.persiandesigners.freeze;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CatsHomeAdapter extends RecyclerView.Adapter<CatsHomeAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private List<CatItems> list;
    private Context ctx;
    private Typeface typeface;
    Boolean likeDigikala=false;

    public CatsHomeAdapter(Context context, List<CatItems> Lists) {
        if(context!=null) {
            inflater= LayoutInflater.from(context);
            this.list= Lists;
            this.ctx= context;
            this.typeface= Typeface.createFromAsset(ctx.getAssets(), "IRAN Sans Bold.ttf");
            likeDigikala=ctx.getResources().getBoolean(R.bool.category_like_digikala);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parrent, int i) {
        View view = inflater.inflate(R.layout.cats_home_row, parrent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        CatItems item = list.get(position);
        viewHolder.title.setText(item.getName());
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

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.tv_cat_row);
            title.setTypeface(typeface);
        }

        @Override
        public void onClick(View v) {
            CatItems item = list.get(getAdapterPosition());
            Intent in =null;
            if(likeDigikala)
                in=new Intent(ctx, Cats_digi.class);
            else
                in=new Intent(ctx, Cats.class);
            in.putExtra("catId", item.getId());
            in.putExtra("onvan", item.getName());
            ctx.startActivity(in);
        }
    }
}
