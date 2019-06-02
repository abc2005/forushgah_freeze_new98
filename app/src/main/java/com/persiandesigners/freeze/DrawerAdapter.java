package com.persiandesigners.freeze;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by navid on 5/19/2016.
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {
    Typeface typeface;
    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;

    private String mNavTitles[]; // String Array to store the passed titles Value from MainActivity.java
    private int mIcons[];       // Int Array to store the passed icons resource value from MainActivity.java

    private String name;        //String Resource for header View Name
    private int profile;        //int Resource for header view profile picture
    Context ctx;

    public class ViewHolder extends RecyclerView.ViewHolder {
        int Holderid;
        TextView textView;
        ImageView imageView,imgklid;
        TextView Name;
        LinearLayout lnlogin;
        FrameLayout line;
        RecyclerView subcat_recycle;
        RelativeLayout row ;

        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            line=(FrameLayout)itemView.findViewById(R.id.line);
            if (ViewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.rowText); // Creating TextView object with the id of textView from item_row.xml
                textView.setTypeface(typeface);
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);// Creating ImageView object with the id of ImageView from item_row.xml
                Holderid = 1;
                row=(RelativeLayout)itemView.findViewById(R.id.row);
            } else {
                imgklid=(ImageView)itemView.findViewById(R.id.imgklid);
                Name = (TextView) itemView.findViewById(R.id.name);         // Creating Text View object from header.xml for name
                Name.setTypeface(typeface);
                lnlogin = (LinearLayout) itemView.findViewById(R.id.lnlogin);
                Holderid = 0;
            }
        }
    }

    DrawerAdapter(String Titles[], int Icons[], String Name, String Email, int Profile, Context context) {
        mNavTitles = Titles;
        mIcons = Icons;
        name = Name;
        profile = Profile;
        ctx = context;
        typeface = Typeface.createFromAsset(ctx.getAssets(), "IRAN Sans.ttf");
    }


    @Override
    public DrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false); //Inflating the layout
            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view
            return vhItem; // Returning the created object
        } else if (viewType == TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false); //Inflating the layout

            ViewHolder vhHeader = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhHeader; //returning the object created


        }
        return null;

    }

    @Override
    public void onBindViewHolder(DrawerAdapter.ViewHolder holder, int position) {
        if (holder.row != null) {
            if ((mNavTitles[position - 1].contains("کیف") &&
                    Func.getUid((Activity) ctx).equals("0")) ||
                    (ctx.getResources().getBoolean(R.bool.isActiveKif) == false &&
                            mNavTitles[position - 1].contains("کیف"))) {
                holder.row.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0));
            }else{
                holder.row.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            }

            if(position==2)
                holder.row.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0));

            ///chand forushande bud , porforush , forush vije .. gheyr fa al
            if(Func.isMultiSeller(ctx) && (position==6 || position==7 || position==8))
                holder.row.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0));
            else
                holder.row.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        }

        //چندفروشنده بود و انتخاب استان شهرستان داشت ، انتخاب شهر نمایش بده
        if(Func.getOstanShahrestanSellers(ctx)==false && position==2){
            holder.row.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0));
        }

        if(holder.line!=null){
            if(position==3 || position==6 || position==10 || position==16){
                holder.line.setVisibility(View.VISIBLE);
            }else
                holder.line.setVisibility(View.GONE);
        }


        if (holder.Holderid == 1) {
            holder.textView.setText(mNavTitles[position - 1]); // Setting the Text with the array of our Titles
            holder.imageView.setImageResource(mIcons[position - 1]);// Settimg the image with array of our icons
        } else {
            holder.Name.setText(name);
            SharedPreferences settings = ctx.getSharedPreferences("settings", ctx.MODE_PRIVATE);
            if (settings.getString("uid", "0").equals("0")) {
                holder.Name.setText("ورود و ثبت نام");
                holder.Name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ctx.startActivity(new Intent(ctx,Login.class));
                    }
                });
            } else {
                holder.imgklid.setVisibility(View.GONE);
                if(settings.getString("name", "").length()<2)
                    holder.Name.setText("خوش آمدید");
                else
					holder.Name.setText("\u200F "+settings.getString("name", "")+ " عزیز خوش آمدید");

                if(ctx.getResources().getBoolean(R.bool.isActiveKif)){
                    holder.Name.setText(settings.getString("name","")
                            +"\n موجودی کیف پول :"+ settings.getString("kif","0")+ " تومان"
                    );
                }

//                if(settings.getBoolean("emtiazModule",false)){
//                    holder.Name.setText(holder.Name.getText().toString()+"\n"
//                            +"امتیاز شما : "
//                    +settings.getString("emtiazUser","0")) ;
//                }
            }
        }
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return mNavTitles.length + 1; // the number of items in the list will be +1 the titles including the header view.
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}
