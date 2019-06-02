package com.persiandesigners.freeze;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.persiandesigners.freeze.Util.ShopOptionAdapter_cat;

import java.util.List;

public class CatsAdapter extends RecyclerView.Adapter<CatsAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private List<CatsItems> list;
    private Context ctx;
    private Typeface typeface;
    int pos = 0;
    ShopOptionAdapter_cat interfaceListener;
    String catId, chooseId;

    public CatsAdapter(Context context, List<CatsItems> Lists,
                       ShopOptionAdapter_cat nterfaceListener, String catId) {
        if (context != null) {
            inflater = LayoutInflater.from(context);
            this.list = Lists;
            this.ctx = context;
            this.typeface = Typeface.createFromAsset(ctx.getAssets(), "IRAN Sans.ttf");
            this.interfaceListener = nterfaceListener;
            if (catId.equals("0"))
                this.catId = null;
            else
                this.catId = catId;

        }
    }

    public CatsAdapter(Context context, List<CatsItems> Lists,
                       ShopOptionAdapter_cat nterfaceListener, String catId, String chooseId) {
        if (context != null) {
            inflater = LayoutInflater.from(context);
            this.list = Lists;
            this.ctx = context;
            this.typeface = Typeface.createFromAsset(ctx.getAssets(), "IRAN Sans.ttf");
            this.interfaceListener = nterfaceListener;
            if (catId.equals("0"))
                this.catId = "-1";
            else
                this.catId = catId;

            if (chooseId==null || chooseId.equals("0"))
                this.chooseId = "-1";
            else
                this.chooseId = chooseId;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parrent, int i) {
        View view = inflater.inflate(R.layout.cats_row, parrent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        CatsItems item = list.get(position);
        viewHolder.title.setText(item.getName());
        String imgurl = item.getIcon();
        if (imgurl.length() > 5)
            Glide.with(ctx).load(ctx.getString(R.string.url) + "Opitures/" + imgurl).into(viewHolder.img);
        else
            viewHolder.img.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.ic_launcher));

        if(chooseId!=null && chooseId.equals(list.get(position).getId()) && !chooseId.equals("-1")){
            catId = "-1";
            chooseId="-1";
            pos=-1;
            viewHolder.line.setVisibility(View.VISIBLE);
        }else if (catId.equals(list.get(position).getId()) && !catId.equals("-1") && chooseId==null) {
            catId = "-1";
            pos=-1;
            viewHolder.line.setVisibility(View.VISIBLE);
        } else if (pos == position && catId.equals("-1")) {
            viewHolder.line.setVisibility(View.VISIBLE);
        } else {
            viewHolder.line.setVisibility(View.INVISIBLE);
        }
    }



    @Override
    public int getItemCount() {
        if (list == null)
            return 0;
        else
            return list.size();
    }
	
	   public String getChooseCatId() {
        CatsItems item = list.get(pos);
        return item.getId();
    }


class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView title;
    ImageView img;
    View line;

    public MyViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        title = (TextView) itemView.findViewById(R.id.onvan);
        title.setTypeface(typeface, Typeface.BOLD);
        img = (ImageView) itemView.findViewById(R.id.img);
        line = (View) itemView.findViewById(R.id.line);
    }

    @Override
    public void onClick(View v) {
        pos = getAdapterPosition();
        CatsItems item = list.get(pos);

        interfaceListener.changeCats(item.getId());
        notifyDataSetChanged();
//            Intent in = new Intent(ctx, NewsDetails.class);
//            in.putExtra("id", item.getId());
//            in.putExtra("onvan", item.getOnvan());
//            in.putExtra("img",item.getPicname());
//            ctx.startActivity(in);
    }
}
}
