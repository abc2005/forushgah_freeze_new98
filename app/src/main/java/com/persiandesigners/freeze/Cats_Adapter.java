package com.persiandesigners.freeze;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.innodroid.expandablerecycler.ExpandableRecyclerAdapter;

import java.util.List;

public class Cats_Adapter extends ExpandableRecyclerAdapter<Cats_Adapter.MyList> {
    public static final int TYPE_PERSON = 1001;
    Typeface typeface;
    Context context;
    List<MyList> itemsCats;

    public Cats_Adapter(Context context, List<MyList> items) {
        super(context);
        this.context=context;
        typeface =Typeface.createFromAsset(context.getAssets(), "IRAN Sans Bold.ttf");
        itemsCats=items;
        setItems(items);
    }

    public static class MyList extends ExpandableRecyclerAdapter.ListItem {
        public String Text,Name, Pic, Id,Parrent ;

        public MyList(String group,String id ,String parrent) {
            super(TYPE_HEADER);
            Text = group;
            Id=id;
            Parrent=parrent;
        }

        public MyList(String name,String pic,String id,String parrent ) {
            super(TYPE_PERSON);
            Name =name;
            Pic=pic;
            Id=id;
            Parrent=parrent;
        }
    }

    public class HeaderViewHolder extends ExpandableRecyclerAdapter.HeaderViewHolder {
        TextView name;
        LinearLayout lnheader;

        public HeaderViewHolder(View view) {
            super(view, (ImageView) view.findViewById(R.id.item_arrow));
            name = (TextView) view.findViewById(R.id.title);
            name.setTypeface(typeface);
            lnheader=(LinearLayout)view.findViewById(R.id.header_ln);
        }

        public void bind(final int position) {
            super.bind(position);
            name.setText(visibleItems.get(position).Text);
            name.setTag(visibleItems.get(position).Id);
            name.setTag(-1,"false");
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id=name.getTag().toString();
                    Boolean goIn=true;
                    for (int i=0; i<itemsCats.size();i++){
                        if(itemsCats.get(i).Parrent.equals(id)){
                            goIn=false;
                            break;
                        }
                    }
                    if(goIn){//agar cat asli subcat nadasht bere dakhel
                        Intent in=new Intent (context,Products.class);
                        in.putExtra("catId", id);
                        in.putExtra("onvan","");
                        context.startActivity(in);
                    }else{
                        if(isExpanded(position))
                            collapseItems(position,true);
                        else
                            expandItems(position, true);
                    }
                }
            });
        }
    }

    public class PersonViewHolder extends ExpandableRecyclerAdapter.ViewHolder {
        TextView name;
        ImageView img ;

        public PersonViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.title);
            name.setTypeface(typeface);
            img=(ImageView)view.findViewById(R.id.img);
        }

        public void bind(int position) {
            name.setText(visibleItems.get(position).Name);
//            Glide.with(context).load(context.getString(R.string.url) + "Opitures/" + img).into(img);

            name.setTag(visibleItems.get(position).Id);
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in=new Intent (context,Products.class);
                    in.putExtra("catId", name.getTag().toString());
                    in.putExtra("onvan","");
                    context.startActivity(in);
                }
            });
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(inflate(R.layout.cat_header, parent));

            case TYPE_PERSON:
            default:
                return new PersonViewHolder(inflate(R.layout.cat_child, parent));
        }
    }

    @Override
    public void onBindViewHolder(ExpandableRecyclerAdapter.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                ((HeaderViewHolder) holder).bind(position);
                break;
            case TYPE_PERSON:
            default:
                ((PersonViewHolder) holder).bind(position);
                break;
        }
    }
}



