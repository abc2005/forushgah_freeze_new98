package com.persiandesigners.freeze;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;

import java.util.List;

/**
 * Created by Navid on 4/18/2018.
 */

public class Shops extends AppCompatActivity {
    Bundle bl;
    Typeface typeface;
    TextView loading;
    RecyclerView rc_shops;
    Boolean loadmore = false, taskrunning;
    int page = 0, in, to;
    private int previousTotal = 0;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    LinearLayoutManager lnmanager;
    ShopsAdapter adapter;
    String catId="0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shops);

        bl = getIntent().getExtras();
        if(bl!=null && bl.getString("catId")!=null){
            catId=(bl.getString("catId"));
        }
        declare();
        getData();
        actionbar();
    }

    private void getData() {
        taskrunning = true;

        long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        new Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                loading.setVisibility(View.GONE);
                if (body.equals("errordade")) {
                    MyToast.makeText(Shops.this, getString(R.string.problem));
                } else {
                    TahlilData(body);
                    taskrunning = false;
                }
            }
        }, false, Shops.this, "").execute(getString(R.string.url) + "/sellers/getShops.php?n=" + number +
                "&catId="+catId+"&page=0" + page);

    }

    private void TahlilData(String body) {
        List items = Func.ParseShops(body);
        if (items != null) {
            if (items.size() < 20)
                loadmore = false;
            else
                loadmore = true;

            if (adapter == null) {
                adapter = new ShopsAdapter(Shops.this, items);
                rc_shops.setAdapter(adapter);

                if (items.size() == 0) {
                    loading.setVisibility(View.VISIBLE);
                    loading.setText("موردی یافت نشد....");
                }
            }
        } else {
            loading.setVisibility(View.VISIBLE);
            loading.setText("موردی یافت نشد....");
        }

        loading.setVisibility(View.GONE);
    }


    private void declare() {
        typeface = Typeface.createFromAsset(this.getAssets(), "IRAN Sans Bold.ttf");
        loading = (TextView) findViewById(R.id.loading);
        loading.setTypeface(typeface);
        rc_shops = (RecyclerView) findViewById(R.id.rc_shops);
        lnmanager = new LinearLayoutManager(this);
        rc_shops.setLayoutManager(lnmanager);
        SearchShops();
    }


    private void actionbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        if (bl != null && bl.getString("onvan") != null)
            action.MakeActionBar(bl.getString("onvan"));
        else
            action.MakeActionBar("فروشندگان");
        Func.checkSabad(this);

        action.HideSearch();
        action.hideDrawer();
        action.showBack();

        TextView search_et=(TextView)findViewById(R.id.search_et);
        search_et.setVisibility(View.GONE);

        ImageView img_sabad = (ImageView) findViewById(R.id.img_sabad);
        img_sabad.setVisibility(View.GONE);
        ImageView imglogo = (ImageView) findViewById(R.id.imglogo);
        imglogo.setVisibility(View.GONE);
        TextView tvnumsabad = (TextView) findViewById(R.id.text_numkharid);
        tvnumsabad.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Func.checkSabad(this);
    }

    private void SearchShops() {
        final AutoCompleteTextView etSearch=(AutoCompleteTextView)findViewById(R.id.search_shop);
        etSearch.setVisibility(View.VISIBLE);
        etSearch.setTypeface(typeface);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etSearch.getText().length() > 1)
                    new ShopSearch(etSearch, Shops.this,catId).execute();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        LeftSabad lsabad = new LeftSabad(this);
    }
}
