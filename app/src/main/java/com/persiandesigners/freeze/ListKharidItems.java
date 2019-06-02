package com.persiandesigners.freeze;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.persiandesigners.freeze.Util.CustomProgressDialog;
import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.MyAdapterCallBack;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class ListKharidItems extends AppCompatActivity implements MyAdapterCallBack {
    Typeface typeface;
    CustomProgressDialog customProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listkharid_items);

        declare();
        getDatas();
        actionbar();
    }

    private void getDatas() {
        long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        new Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                Log.v("this", body);
                if (body.equals("errordade")) {
                    MyToast.makeText(getApplicationContext(), "اتصال اینترنت را بررسی کنید");
                } else {
                    try {
                        ViewGroup main=(ViewGroup)findViewById(R.id.lv_listkharid_extras);

                        JSONObject jsonObject = new JSONObject(body);
                        JSONArray jsonArray = jsonObject.optJSONArray("items");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            for (int i=0; i<jsonArray.length();i++){
                                LayoutInflater inflater = getLayoutInflater(); //this refers to Activity Foo.
                                View view=inflater.inflate(R.layout.listkharid_items_row, null);

                                JSONObject row=jsonArray.optJSONObject(i);

                                TextView onvan = (TextView) view.findViewById(R.id.tv_title);
                                onvan.setTypeface(typeface);
                                onvan.setText(row.optString("name"));

                                RecyclerView rc=(RecyclerView) view.findViewById(R.id.rc_products);
                                LinearLayoutManager layoutManager2
                                        = new LinearLayoutManager(ListKharidItems.this, LinearLayoutManager.HORIZONTAL, false);
                                layoutManager2.setReverseLayout(true);
                                rc.setLayoutManager(layoutManager2);

                                String jsonItem="{\"contacts\":"+row.optString("contact")+"}";
                                MyAdapter adapterRec = new MyAdapter(ListKharidItems.this, Func.parseResult(jsonItem));
                                ScaleInAnimationAdapter scaldeAdapter = new ScaleInAnimationAdapter(adapterRec);
                                scaldeAdapter.setDuration(300);
                                rc.setAdapter(scaldeAdapter);

                                main.addView(view);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    customProgressDialog.dismiss("");
                }
            }
        }, true, this, "").execute(getString(R.string.url) + "/getListKharid_items.php?uid=" + Func.getUid(this));
    }

    private void declare() {
        customProgressDialog = new CustomProgressDialog(this);
        customProgressDialog.show("");

        typeface= Func.getTypeface(this);
    }

    private void actionbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        action.MakeActionBar("لیست خرید");
        Func.checkSabad(this);
        action.HideSearch();
        action.hideDrawer();
        action.showBack();
    }

    @Override
    public void checkSabad() {
        actionbar();
    }
}
