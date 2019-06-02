package com.persiandesigners.freeze;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;

import java.util.List;

/**
 * Created by Navid on 5/12/2018.
 */

public class SabadAddress extends AppCompatActivity {
    Typeface typeface;
    TextView loading, loadmoretv;
    RecyclerView rc_sabadadress;
    Boolean loadmore = false, taskrunning;
    LinearLayoutManager lnmanager;
    SabadAddressAdapter adapter;
    TextView takmil;
    Bundle bl ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sabadadress);

        declare();
        actionbar();
        getData();
    }

    private void getData() {
        taskrunning = true;
        long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        new Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                loading.setVisibility(View.GONE);
                if (body.equals("errordade")) {
                    MyToast.makeText(SabadAddress.this, getString(R.string.problem));
                } else {
                    TahlilData(body);
                    taskrunning = false;
                }
            }
        }, false, SabadAddress.this, "").execute(getString(R.string.url) + "/getAddresses.php?uid=" + Func.getUid(this) + "&n=" + number);

    }

    private void TahlilData(String body) {
        List items = Func.ParseSabadAddress(body);
        if (items != null) {
            if (items.size() < 20)
                loadmore = false;
            else
                loadmore = true;

            if (adapter == null) {
                adapter = new SabadAddressAdapter(SabadAddress.this, items);
                rc_sabadadress.setAdapter(adapter);

                if (items.size() == 0) {
                    loading.setVisibility(View.VISIBLE);
                    loading.setText("جهت ادامه خرید،یک آدرس اضافه کنید");

                    FrameLayout fndata = (FrameLayout) findViewById(R.id.fndata);
                    fndata.setVisibility(View.GONE);
                }
            }else{
				loading.setVisibility(View.GONE);
				loadmoretv.setVisibility(View.GONE);
			}
        } else {
            loading.setVisibility(View.VISIBLE);
            loading.setText("موردی یافت نشد....");
        }


    }

    private void declare() {
        typeface = Func.getTypeface(this);
        bl = getIntent().getExtras();

        takmil = (TextView) findViewById(R.id.takmil);
        takmil.setTypeface(typeface);
        takmil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int listPos =-1;
                if(adapter!=null)
                    listPos=adapter.getSelectedAddress();
                if (adapter.getItemCount() == 0)
                    MyToast.makeText(SabadAddress.this, "جهت تکمیل سفارش،آدرس جدید ایجاد کنید");
                if (listPos == -1)
                    MyToast.makeText(SabadAddress.this, "آدرس انتخاب نشده است");
                else {
                    SabadAddress_Items item = adapter.getSelectedList(listPos);

                    SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
                    SharedPreferences.Editor pref = settings.edit();
                    pref.putString("name_s", item.getname());
                    pref.putString("tel", "");
                    pref.putString("mobile_s", item.gettel());
                    pref.putString("adres", item.getaddress());
                    pref.putString("tabaghe", item.getTabaghe());
                    pref.putString("vahed", item.getVahed());
                    pref.putString("pelak", item.getPelak());
                    pref.putString("codeposti", item.getcodeposti());
                    pref.commit();

                    Intent in = in = new Intent(SabadAddress.this, Sabad_Takmil.class);
                    if (getResources().getBoolean(R.bool.choose_gps_location_on_map)) {
                        in.putExtra("lat", item.getlat());
                        in.putExtra("lon", item.getlon());
                    }
                    in.putExtra("shahrstanId", item.getmahale_id());
                    in.putExtra("shahrstanPrice", "0");
                    in.putExtra("msg", "");
                    in.putExtra("ostan", "");
                    in.putExtra("ostanId", "");
                    startActivity(in);
                }
            }
        });

        loading = (TextView) findViewById(R.id.loading);
        loading.setTypeface(typeface);
        loadmoretv = (TextView) findViewById(R.id.loadmoretv);
        loadmoretv.setTypeface(typeface);

        rc_sabadadress = (RecyclerView) findViewById(R.id.rc_sabadadress);
        lnmanager = new LinearLayoutManager(this);
        rc_sabadadress.setLayoutManager(lnmanager);

        TextView newaddress=findViewById(R.id.newaddress);
        newaddress.setTypeface(typeface);
        newaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(SabadAddress.this, SabadKharid_s1.class);
                in.putExtra("for", "new");
                if(bl!=null && bl.getString("from") != null)
                    in.putExtra("from", "profile");
                startActivity(in);
                finish();
            }
        });

        FloatingActionButton newAdres = (FloatingActionButton) findViewById(R.id.fab);
        newAdres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(SabadAddress.this, SabadKharid_s1.class);
                in.putExtra("for", "new");
                if(bl!=null && bl.getString("from") != null)
                    in.putExtra("from", "profile");
                startActivity(in);
                finish();
            }
        });
    }

    private void actionbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        action.MakeActionBar("انتخاب آدرس");
        action.hideSabadKharidIcon();

        if (bl != null && bl.getString("from") != null && bl.getString("from").equals("profile")) {
            action.MakeActionBar("آدرس های من");
            takmil.setVisibility(View.GONE);
        }

        ImageView imgSearch = (ImageView) findViewById(R.id.imgsearch);
        if (imgSearch != null) {
            imgSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in=new Intent (SabadAddress.this,Home.class);
                    in.putExtra("search","true");
                    startActivity(in);
                    finish();
                }
            });
        }
    }
}
