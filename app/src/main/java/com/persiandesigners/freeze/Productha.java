package com.persiandesigners.freeze;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.MyAdapterCallBack;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;
import com.persiandesigners.freeze.Util.ShopOptionAdapter_cat;
import com.persiandesigners.freeze.Util.ShopOptionAdapter_sub_cat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by navid on 11/14/2017.
 */
public class Productha extends AppCompatActivity implements ShopOptionAdapter_cat,
        ShopOptionAdapter_sub_cat, MyAdapterCallBack {
    Typeface typeface;
    RecyclerView rc_cats, rc_subcat, rc_products;
    TextView loading;
    List<CatsItems> items, mainCats, subCats;
    ProgressBar progressBar;
    SubCatsAdapter subcatadapter;
    private int previousTotal = 0;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount, subCatPos = 0;
    GridLayoutManager mLayoutManager;
    Boolean loadmore = true, taskrunning;
    int page = 0, in, to;
    String urls, catId = "0", chooseId;
    MyAdapter2 productAdaper;
    List<FeedItem> items_products;
    AsyncTask<String, Void, String> AsyncProduct;
    Boolean FirstTime = true;
    Toolbar toolbar;
    Bundle bl;
    Boolean show_maincats_on_page2;
	CatsAdapter adapter ;
	
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productha);

        declare();
        if (bl != null && bl.getString("catId") != null) {
            catId = bl.getString("catId");
            if (bl.getString("chooseId") != null)
                chooseId = bl.getString("chooseId");
        }

        String url = getString(R.string.url) + "/getCatsTezol.php?catId=" + catId;
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
                            MyToast.makeText(Productha.this.getApplicationContext(), "اتصال اینترنت خود را بررسی کنید");
                        } else {
                            LinearLayout lnsabadbottom = (LinearLayout) findViewById(R.id.lnsabadbottom);
                            lnsabadbottom.setVisibility(View.VISIBLE);

                            loading.setVisibility(View.GONE);
                            TahlilData(body);

                            Func.insertOffline(getApplicationContext(), body, finalUrl, 0);
                        }
                    }
                }, false, Productha.this, "").execute(url);
            }

            rc_products.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    visibleItemCount = rc_products.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                    if (loadmore) {
                        if (totalItemCount > previousTotal) {
                            previousTotal = totalItemCount;
                        }
                    }
                    if ((totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold)) {
                        if (taskrunning != null && taskrunning == false && loadmore) {
                            loadmore = false;
                            page = page + 1;
                            getProducts(page);
                        }

                    }
                }
            });
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
            adapter = new CatsAdapter(Productha.this, mainCats, Productha.this, catId, chooseId);
            rc_cats.setAdapter(adapter);
            for (int i = 0; i < mainCats.size(); i++) {
                if (mainCats.get(i).getId().equals(chooseId))
                    rc_cats.scrollToPosition(i);
            }


            if (items.size() > 0 && mainCats.size() > 0) {
                if (FirstTime) {
                    FirstTime = false;
                    //                    if(!catId.equals("0"))
//                        changeCats(catId);
//                    else
                    if (bl != null && bl.getString("chooseId") != null)
                        changeCats(bl.getString("chooseId"));
                    else
                        changeCats(mainCats.get(0).getId());
                } else {
//                    if(!catId.equals("0"))
//                        changeCats(catId);
//                    else
                    changeCats(mainCats.get(0).getId());
                }
            } else {
                try {
                    urls = getString(R.string.url) + "/getProductsTezol.php?id=" + mainCats.get(0).getId() + "&chooseId="+chooseId+"&from=cats&page=0";
                } catch (Exception e) {
                    urls = getString(R.string.url) + "/getProductsTezol.php?id=" +catId+ "&chooseId="+chooseId+ "&from=cats&page=0";
                }
                getProducts(0);
            }
        }
        rc_products.setAdapter(null);
        progressBar.setVisibility(View.GONE);
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
        if (subCats != null && subCats.size() > 0) {
            subcatadapter = new SubCatsAdapter(Productha.this, subCats, Productha.this);
            rc_subcat.setAdapter(subcatadapter);
            if (subCats != null && subCats.size() > 0) {
                while (subCatPos >= subCats.size() && subCatPos >= 0) {
                    subCatPos--;
                }
//            changeSubCats(subCats.get(subCatPos).getId());
            } else {
//            changeSubCats(id);//subcat nadarad
            }
            changeSubCats(id);
            rc_products.setAdapter(null);
            progressBar.setVisibility(View.GONE);
        }else{
            rc_subcat.setAdapter(null);
            rc_products.setAdapter(null);
			changeSubCats(id);
        }
    }


    private void declare() {
        show_maincats_on_page2 = getResources().getBoolean(R.bool.show_maincats_on_page2);
        bl = getIntent().getExtras();
        FirstTime = true;
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        rc_cats = (RecyclerView) findViewById(R.id.cats);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Productha.this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setReverseLayout(true);
        rc_cats.setLayoutManager(layoutManager);

        rc_subcat = (RecyclerView) findViewById(R.id.subcats);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(Productha.this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager2.setReverseLayout(true);
        rc_subcat.setLayoutManager(layoutManager2);

        rc_products = (RecyclerView) findViewById(R.id.products);
        Display display = Productha.this.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;
        int columns = Math.round(dpWidth / 140);
        mLayoutManager = new GridLayoutManager(Productha.this, columns);
        rc_products.setLayoutManager(mLayoutManager);

        typeface = Func.getTypeface(this);
        loading = (TextView) findViewById(R.id.loading);
        loading.setTypeface(typeface);
    }

    @Override
    public void changeSubCats(String id) {
        page = 0;
        progressBar.setVisibility(View.VISIBLE);
        rc_products.setAdapter(null);
        productAdaper = null;

        changeTitleName(id);

        urls = getString(R.string.url)+"getProductsTezol.php?id=" + id + "&from=cats&page=0";
        getProducts(0);

        //agar zirdaste sevom dasht, ba click ruye subcat, subcat taghir kone be zir daste 3
        changeSubCatsThird(id);
    }

    private void changeTitleName(String id) {
        TextView title_toolbar = (TextView) findViewById(R.id.title_toolbar);
        title_toolbar.setTypeface(typeface);

        for (int i=0; i<items.size();i++){
            if(items.get(i).getId().equals(id)){
                title_toolbar.setText(items.get(i).getName());
                break;
            }
        }
    }

    private void changeSubCatsThird(String id) {
        page = 0;
        subCats = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getParrent().equals(id)) {
                subCats.add(items.get(i));
            }
        }
        if (subCats != null && subCats.size() > 0) {
            subcatadapter = new SubCatsAdapter(Productha.this, subCats, Productha.this);
            rc_subcat.setAdapter(subcatadapter);
        }
    }

    private void getProducts(int p) {
        if (AsyncProduct != null)
            AsyncProduct.cancel(true);

        taskrunning = true;
        AsyncProduct = new Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                taskrunning = false;
                if (body.equals("errordade")) {
                    MyToast.makeText(Productha.this, "اتصال اینترنت خود را بررسی کنید");
                } else {
                    items_products = Func.parseResult(body);
                    if (items_products != null && items_products.size() < 20) {
                        loadmore = false;
                    } else {
                        loadmore = true;
                    }

                    if (productAdaper == null) {
                        productAdaper = new MyAdapter2(Productha.this, items_products);
                        rc_products.setAdapter(productAdaper);
                        productAdaper.setFullWidth(true);
                    } else {
                        productAdaper.addAll(items_products);
                    }
                    progressBar.setVisibility(View.GONE);


                    //////gallery
//                    try {
//                        JSONObject galleryJson = new JSONObject(body);
//                        JSONArray galleryArray = galleryJson.getJSONArray("gall");
//
//                        RelativeLayout lngallery = (RelativeLayout) findViewById(R.id.lngallery);
//                        if (galleryArray != null && galleryArray.length() > 0) {
//                            HashMap<String, String> img = new HashMap<String, String>();
//                            int height = 0;
//                            for (int i = 0; i < galleryArray.length(); i++) {
//                                JSONObject post = galleryArray.optJSONObject(i);
//                                img.put(i + "", getString(R.string.url) + "Opitures/" + post.optString("img"));
//                                height = post.optInt("h");
//                            }
//                            try {
//                                if (height > 0)
//                                    lngallery.getLayoutParams().height = height;
//                            } catch (Exception e) {
//                                lngallery.getLayoutParams().height = 300;
//                            }
//
//
//                            if (img == null || img.isEmpty()) {
//                                lngallery.setVisibility(View.GONE);
//                            } else {
////                            lngallery.getLayoutParams().height = height;
//                                SliderLayout mDemoSlider = (SliderLayout) findViewById(R.id.slider);
//                                for (String name : img.keySet()) {
//                                    TextSliderView textSliderView = new TextSliderView(Productha.this);
//                                    // initialize a SliderLayout
//                                    textSliderView
//                                            .image(img.get(name))
//                                            .setScaleType(BaseSliderView.ScaleType.Fit)
////                            .setOnSliderClickListener(MainActivity.this)
//                                    ;
//
//                                    //add your extra information
//                                    textSliderView.bundle(new Bundle());
//                                    textSliderView.getBundle()
//                                            .putString("extra", name);
//
//                                    mDemoSlider.addSlider(textSliderView);
//                                }
//                                mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
//                                mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
//                                mDemoSlider.setCustomAnimation(new DescriptionAnimation());
//                                mDemoSlider.setDuration(4000);
//                            }
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }, false, Productha.this, "").execute(urls + p);
    }

    public void changeSubCatsAppend(String id) {
        progressBar.setVisibility(View.VISIBLE);
        urls = getString(R.string.url) + "/getProductsTezol.php?id=" + id + "&from=cats&page=0";
        getProducts(0);
        subcatadapter.nextPosMarket();
    }

    public void NotifyAdapters() {
        if (productAdaper != null) {
            Log.v("this", "not ");
            productAdaper.notifyDataSetChanged();
        }
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
        productAdaper.notifyDataSetChanged();
    }

    private void actionbar() {
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        if (bl.getString("onvan") != null)
            action.MakeActionBar(bl.getString("onvan"));
        Func.checkSabad(this);
        action.HideSearch();
        action.hideDrawer();
        action.showBack();

        ImageView img_sabad = (ImageView) findViewById(R.id.img_sabad);
        img_sabad.setVisibility(View.GONE);
        TextView tvnumsabad = (TextView) findViewById(R.id.text_numkharid);
        tvnumsabad.setVisibility(View.GONE);
        ImageView imglogo = (ImageView) findViewById(R.id.imglogo);
        imglogo.setVisibility(View.GONE);

        ImageView sort = (ImageView) findViewById(R.id.sort);
        sort.setVisibility(View.VISIBLE);
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogSort();
            }
        });
    }
	
 @Override
    public void onBackPressed() {
        try {
            if(subcatadapter!=null && subcatadapter.getPos()>=0){
                changeCats(adapter.getChooseCatId());
                subcatadapter.setPos(-1);
            }else
                super.onBackPressed();
        } catch (Exception e) {
            super.onBackPressed();
        }
    }
	
	   @Override
    protected void onStart() {
        super.onStart();
           LeftSabad lsabad=new LeftSabad(this);
		   
		if (productAdaper != null) {
            productAdaper.notifyDataSetChanged();
        }
        try {
            Func.checkSabad(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ////////////////
    private void DialogSort() {
        final Dialog dialog = new Dialog(Productha.this, R.style.DialogStyler);
        dialog.setContentView(R.layout.positon);

        ProgressBar progress = (ProgressBar) dialog.findViewById(R.id.progress);
        ListView lv = (ListView) dialog.findViewById(R.id.poslist);
        lv.setVisibility(View.VISIBLE);

        TextView tvpos = (TextView) dialog.findViewById(R.id.tvpos);
        tvpos.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);


        String sort[] = new String[]{"به ترتیب حروف الفبا", "قیمت از ارزان به گران", "قیمت از گران به ارزان"
                , "محصول جدیدتر به قدیم تر", "محصول قدیمی تر به جدیدتر",  "پربازدیدترین", "پرفروش ترین"
//                        ,"تعداد فروش"
        };

        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(Productha.this, android.R.layout.simple_list_item_1, sort);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (urls.contains("sort")) {
                    urls = urls.substring(0, urls.indexOf("&sort"));
                }
                if (urls.contains("page")) {
                    urls = urls.substring(0, urls.indexOf("&page"));
                }
                switch (position) {
                    case 0:
                        urls = urls + "&sort=alpha";
                        break;
                    case 1:
                        urls = urls + "&sort=price_asc";
                        break;
                    case 2:
                        urls = urls + "&sort=price_desc";
                        break;
                    case 3:
                        urls = urls + "&sort=id_desc";
                        break;
                    case 4:
                        urls = urls + "&sort=id_asc";
                        break;
                    case 5:
                        urls = urls + "&sort=numvisit";
                        break;
                    case 6:
                        urls = urls + "&sort=numforush";
                        break;
                }
                urls = urls + "&page=0";
                productAdaper = null;
                rc_products.setAdapter(null);
                page = 0;
                getProducts(0);

                dialog.dismiss();
            }
        });

    }

}
