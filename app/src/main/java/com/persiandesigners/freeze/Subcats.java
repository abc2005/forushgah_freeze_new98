package com.persiandesigners.freeze;

import android.content.Intent;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.MyAdapterCallBack;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;
import com.persiandesigners.freeze.Util.RtlGridLayoutManager;
import com.persiandesigners.freeze.Util.ShopOptionAdapter_cat;
import com.persiandesigners.freeze.Util.ShopOptionAdapter_sub_cat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by navid on 12/2/2017.
 */
public class Subcats extends AppCompatActivity implements ShopOptionAdapter_cat,
        ShopOptionAdapter_sub_cat, MyAdapterCallBack {
    Typeface typeface;
    RecyclerView rc_cats, rc_subcat;
    TextView loading;
    List<CatsItems> items, mainCats, subCats;
    ProgressBar progressBar;
    SubCatsAdapterPics subcatadapter;
    int subCatPos = 0;
    GridLayoutManager mLayoutManager;
    int page = 0, in, to;
    String urls, catId = "0";
    MyAdapter2 productAdaper;
    Boolean FirstTime = true;
    Toolbar toolbar;
    Bundle bl;
    Boolean show_maincats_on_page2;
    TextView title_toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subcats);

        declare();
        if (bl != null && bl.getString("catId") != null) {
            catId = bl.getString("catId");
        }

         String url = getString(R.string.url) + "/getCatsTezol.php?catId=" + catId ;
        if (Func.isInternet(this) == false &&
                Func.hasOffline(getApplicationContext(), url, 0)==false) {
            Func.NoNet(this);
            loading.setVisibility(View.GONE);
        } else {
            if (show_maincats_on_page2)
                url = getString(R.string.url) + "/getCatsTezol.php?catId=0";

            if (Func.isInternet(this) == false) {
                LinearLayout lnsabadbottom = (LinearLayout) findViewById(R.id.lnsabadbottom);
                lnsabadbottom.setVisibility(View.VISIBLE);

                loading.setVisibility(View.GONE);
                TahlilData(Func.getOffline(getApplicationContext(), url, 0));
            } else {
                final String finalUrl = url;
                new Html(new OnTaskFinished() {
                    @Override
                    public void onFeedRetrieved(String body) {
                        Log.v("this", body);
                        if (body.equals("errordade")) {
                            MyToast.makeText(Subcats.this.getApplicationContext(), "اتصال اینترنت خود را بررسی کنید");
                        } else {
                            LinearLayout lnsabadbottom = (LinearLayout) findViewById(R.id.lnsabadbottom);
                            lnsabadbottom.setVisibility(View.VISIBLE);

                            loading.setVisibility(View.GONE);
                            TahlilData(body);

                            Func.insertOffline(getApplicationContext(), body, finalUrl, 0);
                        }
                    }
                }, false, Subcats.this, "").execute(url);
            }
        }
        actionbar();
    }


    private void TahlilData(String body) {
        items = Func.ParseCats(body);
        mainCats = new ArrayList<>();
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                if (bl != null && bl.getString("catId") != null && show_maincats_on_page2 == false) {//az cat umade bud
                    if (items.get(i).getParrent().equals(catId))
                        mainCats.add(items.get(i));
                } else if (items.get(i).getParrent().equals("0")) {
                    mainCats.add(items.get(i));
                }
            }
            CatsAdapter adapter = new CatsAdapter(Subcats.this, mainCats, Subcats.this, catId);
            rc_cats.setAdapter(adapter);
            for (int i = 0; i < mainCats.size(); i++) {
                if (mainCats.get(i).getId().equals(catId))
                    rc_cats.scrollToPosition(i);
            }


            if (items.size() > 0 && mainCats.size() > 0) {
                if (FirstTime) {
                    FirstTime = false;
                    if (!catId.equals("0"))
                        changeCats(catId);
                    else
                        changeCats(mainCats.get(0).getId());
                } else {
                    if (!catId.equals("0"))
                        changeCats(catId);
                    else
                        changeCats(mainCats.get(0).getId());
                }
            }
        }
        progressBar.setVisibility(View.GONE);
    }

    private void changeTitleName(String id) {
        TextView title_toolbar = (TextView) findViewById(R.id.title_toolbar);
        title_toolbar.setTypeface(typeface);

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(id)) {
                title_toolbar.setText(items.get(i).getName());
                break;
            }
        }
    }

    @Override
    public void changeCats(String id) {
        page = 0;
        subCats = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getParrent().equals(id)) {
                subCats.add(items.get(i));
            }
        }
        if (subCats.size() == 0) {
            Intent in = null;
            in = new Intent(Subcats.this, Productha.class);
            in.putExtra("catId", id);
            in.putExtra("onvan", "");
            startActivity(in);
        } else {
            subcatadapter = new SubCatsAdapterPics(Subcats.this, subCats, Subcats.this);
            rc_subcat.setAdapter(subcatadapter);
            if (subCats != null && subCats.size() > 0) {
                while (subCatPos >= subCats.size() && subCatPos >= 0) {
                    subCatPos--;
                }
//            changeSubCats(subCats.get(subCatPos).getId());
            } else {
//            changeSubCats(id);//subcat nadarad
            }
            changeTitleName(id);
            changeSubCats(id);
            progressBar.setVisibility(View.GONE);
        }
    }


    private void declare() {
        show_maincats_on_page2 = true;
        bl = getIntent().getExtras();
        FirstTime = true;
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        rc_cats = (RecyclerView) findViewById(R.id.cats);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Subcats.this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setReverseLayout(true);
        rc_cats.setLayoutManager(layoutManager);

        rc_subcat = (RecyclerView) findViewById(R.id.subcats);
        RtlGridLayoutManager layoutManager2 = new RtlGridLayoutManager(Subcats.this, 3);
        rc_subcat.setLayoutManager(layoutManager2);

        Display display = Subcats.this.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;
        int columns = Math.round(dpWidth / 140);
        mLayoutManager = new GridLayoutManager(Subcats.this, columns);

        typeface = Func.getTypeface(this);
        loading = (TextView) findViewById(R.id.loading);
        loading.setTypeface(typeface);

        if(Func.isMultiSeller(this)){
            rc_cats.setVisibility(View.GONE);
        }
    }

    @Override
    public void changeSubCats(String id) {
        page = 0;
        progressBar.setVisibility(View.VISIBLE);
        productAdaper = null;


    }

    @Override
    public void onResume() {
        super.onResume();
        if (productAdaper != null) {
            productAdaper.notifyDataSetChanged();
        }
    }

    @Override
    public void checkSabad() {
        Func.checkSabad(this);
        if (productAdaper != null)
            productAdaper.notifyDataSetChanged();
    }

    private void actionbar() {
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        if (bl.getString("onvan") != null)
            action.MakeActionBar(bl.getString("onvan"));
        Func.checkSabad(this);

        title_toolbar = (TextView) findViewById(R.id.title_toolbar);
        title_toolbar.setTypeface(typeface);

        action.HideSearch();
        action.hideDrawer();
        action.showBack();

        ImageView img_sabad = (ImageView) findViewById(R.id.img_sabad);
        img_sabad.setVisibility(View.GONE);
        ImageView imglogo = (ImageView) findViewById(R.id.imglogo);
        imglogo.setVisibility(View.GONE);
        TextView tvnumsabad = (TextView) findViewById(R.id.text_numkharid);
        tvnumsabad.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LeftSabad lsabad=new LeftSabad(this);
        checkSabad();
    }
}
