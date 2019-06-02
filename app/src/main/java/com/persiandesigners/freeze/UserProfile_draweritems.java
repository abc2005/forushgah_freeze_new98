package com.persiandesigners.freeze;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.persiandesigners.freeze.Util.DatabaseHandler;
import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;

import java.util.ArrayList;

/**
 * Created by Navid on 5/19/2018.
 */

public class UserProfile_draweritems extends AppCompatActivity {
    Typeface typeface;
    ListView lv;
    ArrayList<String>titles;
    ArrayList<Integer>images;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userprofile);

        makeArrays();
        declare();
        actionbar();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent in =null;
                if(getResources().getBoolean(R.bool.multiseller))
                    i++;

                switch (i){
                    case 0:
                        Func.dialogRahgiri(UserProfile_draweritems.this);
                        break;
                    case 1:
                        startActivity(new Intent(UserProfile_draweritems.this, Order.class));
                        break;
                    case 2:
                        in=new Intent(UserProfile_draweritems.this,SabadAddress.class);
                        in.putExtra("from","profile");
                        break;
                    case 3:
                        DatabaseHandler db = new DatabaseHandler(UserProfile_draweritems.this);
                        if (!db.isOpen())
                            db.open();
                        Cursor cursor = db.getFavs();
                        if (cursor.getCount() > 0) {
                            in = new Intent(UserProfile_draweritems.this, Products.class);
                            in.putExtra("title", "علاقه مندی ها");
                            in.putExtra("fav", "fav");
                            startActivity(in);
                        } else
                            MyToast.makeText(UserProfile_draweritems.this, "محصولی به علاقه مندی های خود اضافه نکرده اید");
                        break;
                    case 4:
                        Intent inRules = new Intent(UserProfile_draweritems.this, Page.class);
                        inRules.putExtra("w", "rules");
                        inRules.putExtra("onvan", "قوانین و مقررات");
                        startActivity(inRules);
                        break;
                    case 5:
                        Intent inAbout = new Intent(UserProfile_draweritems.this, Page.class);
                        inAbout.putExtra("w", "aboutus");
                        inAbout.putExtra("onvan", "درباره ما");
                        startActivity(inAbout);
                        break;
                    case 6:
                        Intent inContact = new Intent(UserProfile_draweritems.this, Contactus.class);
                        inContact.putExtra("onvan", "تماس ما");
                        startActivity(inContact);
                        break;
                    case 7:
                        Intent inHelp = new Intent(UserProfile_draweritems.this, Page.class);
                        inHelp.putExtra("w", "shoppingGuide");
                        inHelp.putExtra("onvan", "راهنمای خرید");
                        startActivity(inHelp);
                        break;
                    case 8:
                        Intent inFaq = new Intent(UserProfile_draweritems.this, Page.class);
                        inFaq.putExtra("w", "questions");
                        inFaq.putExtra("onvan", "پرسش های متداول");
                        startActivity(inFaq);
                        break;
                    case 9:
                        try {
                            long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
                            new Html(new OnTaskFinished() {
                                @Override
                                public void onFeedRetrieved(String body) {
                                    Log.v("this", "Sd " + body);
                                    if(body.contains("http")) {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(body.trim()));
                                        startActivity(browserIntent);
                                    }else{
                                        MyToast.makeText(UserProfile_draweritems.this,"آدرس کانال تعریف نشده است");
                                    }
                                }
                            }, true, UserProfile_draweritems.this, "").execute(getString(R.string.url) + "/getTelegram.php?n=" + number);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 10:
                        in=new Intent(UserProfile_draweritems.this, Profile.class);
                        in.putExtra("for","pass");
                        break;
                    case 11:
                        SharedPreferences settings = getSharedPreferences("settings", Context.MODE_PRIVATE);
                        settings.edit().clear().commit();
                        startActivity(new Intent(UserProfile_draweritems.this, Home.class));
                        break;
                }
            }
        });
    }

    private void makeArrays() {
        titles=new ArrayList<>();
        images=new ArrayList<>();
        titles.add("پیگیری سفارش");
        images.add(R.drawable.recently);
        titles.add("سابقه سفارشها");
        images.add(R.drawable.ic_timer_grey600_24dp);


        if(getResources().getBoolean(R.bool.multiseller)==false) {
            titles.add("آدرس های من");
            images.add(R.drawable.ic_action_location);
        }
        titles.add("علاقه مندی ها");
        images.add(R.drawable.favorits_dark);
        titles.add("قوانین و مقررات");
        images.add(R.drawable.rules);
        titles.add("درباره ما");
        images.add(R.drawable.aboutus_dark);
        titles.add("تماس با ما");
        images.add(R.drawable.contactus);
        titles.add("راهنمای خرید");
        images.add(R.drawable.edu);
        titles.add("پرسش های متداول");
        images.add(R.drawable.ic_action_help);
        titles.add("کانال تلگرامی");
        images.add(R.drawable.telegrams);

        titles.add("تغییر گذرواژه");
        images.add(R.drawable.ic_action_lock);
        titles.add("خروج از حساب کاربری");
        images.add(R.drawable.ic_action_remove_dark);
    }

    class myAdapter extends ArrayAdapter<String> {
        Context context;

        myAdapter(Context c) {
            super(c, R.layout.user_row, R.id.title, titles);
            this.context = c;
        }

        class myViewHolder {
            ImageView myimage;
            TextView myTitle;

            myViewHolder(View v) {
                myimage = (ImageView) v.findViewById(R.id.img);
                myTitle = (TextView) v.findViewById(R.id.onvan);
                myTitle.setTypeface(typeface);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            myViewHolder holder = null;
            if (row == null) { // agar avalin bar hast ke sakhte mishe
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.user_row, parent, false);
                holder = new myViewHolder(row);
                row.setTag(holder);
            } else {
                holder = (myViewHolder) row.getTag();
            }
            holder.myimage.setImageResource(images.get(position));
            holder.myTitle.setText(titles.get(position));
            return row;
        }
    }


    private void declare() {
        typeface = Func.getTypeface(this);
        TextView tvname = (TextView) findViewById(R.id.tvname);
        tvname.setTypeface(typeface);

        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        tvname.setText(settings.getString("name","")+ " "+ settings.getString("famil",""));

        lv = (ListView) findViewById(R.id.lv);
        myAdapter adapter = new myAdapter(this);
        lv.setAdapter(adapter);

        LinearLayout editprofile=(LinearLayout)findViewById(R.id.editprofile);
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserProfile_draweritems.this,Profile.class));
            }
        });

        Button support=(Button)findViewById(R.id.support);
        support.setTypeface(typeface);

    }

    public void support(View v){
        try {
            long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
            new Html(new OnTaskFinished() {
                @Override
                public void onFeedRetrieved(String body) {
                    Log.v("this", "Sd " + body);
                    if(body.contains("http")) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(body));
                        startActivity(browserIntent);
                    }else{
                        MyToast.makeText(UserProfile_draweritems.this,"آدرس کانال تعریف نشده است");
                    }
                }
            }, true, UserProfile_draweritems.this, "").execute(getString(R.string.url) + "/getTelegram.php?n=" + number);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actionbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        action.MakeActionBar("حساب کاربری");
        Func.checkSabad(this);

        ImageView img_sabad = (ImageView) findViewById(R.id.img_sabad);
        img_sabad.setVisibility(View.GONE);
        TextView tvnumsabad = (TextView) findViewById(R.id.text_numkharid);
        tvnumsabad.setVisibility(View.GONE);
    }
}
