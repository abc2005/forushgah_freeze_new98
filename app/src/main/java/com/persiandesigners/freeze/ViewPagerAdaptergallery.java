package com.persiandesigners.freeze;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.persiandesigners.freeze.Util.TouchImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewPagerAdaptergallery extends PagerAdapter {
    // Declare Variables
    Context context;
    String[] img, link;
    LayoutInflater inflater;
    String folderName;
    ArrayList<HashMap<String, String>> dataC;

    public ViewPagerAdaptergallery(Context context, String[] img, String from) {
        this.context = context;
        this.img = img;
        if (from.equals("details"))
            folderName = "Opitures";
        else
            folderName = "galleryPics";
    }

    public ViewPagerAdaptergallery(Context context, String[] img, String from, String[] link,
                                   ArrayList<HashMap<String, String>> dataC) {
        this.dataC = dataC;
        this.context = context;
        this.img = img;
        if (from.equals("details"))
            folderName = "Opitures";
        else
            folderName = "galleryPics";
        this.link = link;
    }

    @Override
    public int getCount() {
        return img.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imgflag;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = null;
        if (folderName.equals("galleryPics")) {
            itemView = inflater.inflate(R.layout.viewpager_item2, container, false);
            imgflag = (ImageView) itemView.findViewById(R.id.flag);
            imgflag.setScaleType(ScaleType.FIT_XY);
        } else {//fistAcitivty
            itemView = inflater.inflate(R.layout.viewpager_item, container, false);
            imgflag = (ImageView) itemView.findViewById(R.id.flag);
        }

        //     imgflag = (ImageView) itemView.findViewById(R.id.flag);
        //    imgflag.setScaleType(ScaleType.FIT_XY);

        Glide.with(context).load(context.getString(R.string.url) + folderName + "/" + img[position]).into(imgflag);
        imgflag.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String link_type = "", links = "";
                if(context.getResources().getBoolean(R.bool.zoom_2finger_details_page)==false) {
                    if (dataC != null) {
                        try {
                            link_type = dataC.get(position).get("link_type");
                            links = dataC.get(position).get("link");
                            doCLickListener(v, link_type, links);
                        } catch (Exception e) {
                            openImageDialog(context.getString(R.string.url) + folderName + "/" + img[position]);
                        }
                    }else
                        openImageDialog(context.getString(R.string.url) + folderName + "/" + img[position]);
                }else{
                    openImageDialog(context.getString(R.string.url) + folderName + "/" + img[position]);
                }
            }
        });
        ((ViewPager) container).addView(itemView);
        return itemView;
    }

    private void openImageDialog(String url) {
        final Dialog dialog = new Dialog(context, R.style.DialogStyler);
        dialog.setContentView(R.layout.bigimg2);
        TouchImageView imgbnig=(TouchImageView)dialog.findViewById(R.id.imageView1);
        Glide.with(context).load(url).into(imgbnig);
        dialog.show();
        if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_LARGE || (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==Configuration.SCREENLAYOUT_SIZE_XLARGE ) {
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        }else{
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(lp);
        }
    }

    private void doCLickListener(View v, String link_type, String link) {
        if (link_type.equals("web")) {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                context.startActivity(browserIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (link_type.equals("cat")) {
            Intent in = new Intent(context, Products.class);
            in.putExtra("catId", link);
            in.putExtra("onvan", "");
            context.startActivity(in);
        } else if (link_type.equals("prod") || link_type.equals("pro")) {
            Intent in = new Intent(context, Detailss.class);
            in.putExtra("productid", link);
            in.putExtra("name", "");
            context.startActivity(in);
        }
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

}
