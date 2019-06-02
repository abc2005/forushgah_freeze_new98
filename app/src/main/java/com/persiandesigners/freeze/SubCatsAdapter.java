package com.persiandesigners.freeze;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.persiandesigners.freeze.Util.ShopOptionAdapter_sub_cat;

import java.util.List;

public class SubCatsAdapter extends RecyclerView.Adapter<SubCatsAdapter.MyViewHolder>
{
    private LayoutInflater inflater;
    private List<CatsItems> list;
    private Context ctx;
    private Typeface typeface;
    int pos=-1;
    ShopOptionAdapter_sub_cat interfaceListener;


    public SubCatsAdapter(Context context, List<CatsItems> Lists, ShopOptionAdapter_sub_cat interfaceListener) {
        if(context!=null) {
            inflater= LayoutInflater.from(context);
            this.list= Lists;
            this.ctx= context;
            this.typeface= Typeface.createFromAsset(ctx.getAssets(), "IRAN Sans.ttf");
            this.interfaceListener=interfaceListener;

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parrent, int i) {
        View view = inflater.inflate(R.layout.sub_cats_row, parrent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        CatsItems item = list.get(position);
        viewHolder.title.setText(item.getName());

        if(pos==position) {
            viewHolder.title.setBackgroundResource(R.drawable.round_subcats_fill);
            viewHolder.title.setTextColor(Color.WHITE);
        }else {
            viewHolder.title.setBackgroundResource(R.drawable.round_subcats);
            viewHolder.title.setTextColor(ctx.getResources().getColor(R.color.color_dark_blue));
        }
    }

    @Override
    public int getItemCount() {
        if(list==null)
            return 0;
        else
            return list.size();
    }


    public void nextPosMarket() {
        pos++;
        notifyDataSetChanged();
    }
	
	
    public int getPos() {
        return pos ;
    }

    public void setPos(int i) {
        pos=i;
        notifyDataSetChanged();
    }



    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        View line ;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.onvan);
            title.setTypeface(typeface);
        }

        @Override
        public void onClick(View v) {
            try {
                pos=getAdapterPosition();
                CatsItems item = list.get(pos);
                interfaceListener.changeSubCats(item.getId());
                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            Intent in = new Intent(ctx, NewsDetails.class);
//            in.putExtra("id", item.getId());
//            in.putExtra("onvan", item.getOnvan());
//            in.putExtra("img",item.getPicname());
//            ctx.startActivity(in);
        }


    }
}
