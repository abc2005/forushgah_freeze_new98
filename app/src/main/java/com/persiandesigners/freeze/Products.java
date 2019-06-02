package com.persiandesigners.freeze;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.innodroid.expandablerecycler.ExpandableRecyclerAdapter;
import com.persiandesigners.freeze.Util.CustomProgressDialog;
import com.persiandesigners.freeze.Util.DatabaseHandler;
import com.persiandesigners.freeze.Util.HtmlPost;
import com.persiandesigners.freeze.Util.MyAdapterCallBack;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;
import com.persiandesigners.freeze.Util.RtlGridLayoutManager;
import com.persiandesigners.freeze.Util.SpacesItemDecoration;
import com.software.shell.fab.ActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class Products extends AppCompatActivity implements MyAdapterCallBack {
    Toolbar toolbar;
    Typeface typeface2;
    CustomProgressDialog mCustomProgressDialog;
    private List<CatItems> catItems;
    private CatsAdapter adapter;
    MyAdapter2 productAdaper;
    RecyclerView recycleCats, recycle;
    Bundle bl;
    Boolean loadmore = true, taskrunning;
    int page = 0, in, to;
    String urls;
    private int previousTotal = 0;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    SharedPreferences settings;
    Boolean grid;
    RtlGridLayoutManager mLayoutManager;
    LinearLayoutManager mLayoutManager2;
    TextView tvcats, tvsort;
    NestedScrollView nested;
    ImageView view_type;

    List<Params_Items> params;
    ListView[] formSpiiners;
    Integer[] SpinnerSubcatId;
    int countOptions;
    String[] optionsId = null;
    String[] options = null;
    ProgressBar pg;
    Cats_Adapter adapterCats;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.products);

        declare();


        actionbar();
        floatingButton();
        sortButton();

        String uid = "&uid=" + Func.getUid(this);
        if (bl.getString("for") != null) {
            urls = getString(R.string.url) + "/getProdcuts.php?for=" + bl.getString("for") + uid + "&page=0";
        } else if (bl.getString("sort") != null)
            urls = getString(R.string.url) + "/getProdcuts.php?sort=" + bl.getString("sort") + uid + "&page=0";
        else if (bl.getString("search") != null)
            urls = getString(R.string.url) + "/getProdcuts.php?n=1&for=search" + uid + "&page=0";
        else if (bl.getString("search_brand") != null)
            urls = getString(R.string.url) + "/getProdcuts.php?n=1&for=search_brand" + uid + "&page=0";
        else if (bl.getString("search_cat") != null)
            urls = getString(R.string.url) + "/getProdcuts.php?n=1&for=search_cat" + uid + "&page=0";
        else if (bl.getString("fav") != null) {
            DatabaseHandler db = new DatabaseHandler(this);
            if (!db.isOpen())
                db.open();
            Cursor cursor = db.getFavs();
            if (cursor.getCount() > 0) {
                String likes = "";
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    likes += cursor.getInt(0) + ",";
                }
                likes = likes.substring(0, likes.length() - 1);
                urls = getString(R.string.url) + "getProdcuts.php?for=like&likes=" + likes + uid;
                urls += "&page=0";
            }
        } else {
            urls = getString(R.string.url) + "/getProdcuts.php?id=" + bl.getString("catId") + uid + "&page=0";
            tvcats.setVisibility(View.VISIBLE);
        }

        if (Func.isInternet(getApplicationContext()) ||
                Func.hasOffline(getApplicationContext(), urls, 0)) {
            getData();
            if (Func.isInternet(this) == false)
                Func.NoNet(this);
        } else {
            Func.NoNet(this);
        }
    }

    private void getData() {
        if (Func.isInternet(getApplicationContext())) {
            if (page > 0) {
                pg.setVisibility(View.VISIBLE);
            }
            taskrunning = true;
            String search = "0";
            if (bl != null && bl.getString("s") != null) {
                if (bl.getString("search") != null) {
                    search = bl.getString("search");
                } else if (bl.getString("search_brand") != null) {
                    search = bl.getString("search_brand");
                } else if (bl.getString("search_cat") != null) {
                    search = bl.getString("search_cat");
                }
            }
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("search", search);
            String query = builder.build().getEncodedQuery();

            new HtmlPost(new OnTaskFinished() {
                @Override
                public void onFeedRetrieved(String body) {
                    pg.setVisibility(View.GONE);
                    Func.insertOffline(getApplicationContext(), body, urls, 0);
                    Log.v("this", body);
                    if (body.equals("errordade")) {
                        MyToast.makeText(getApplicationContext(), "اشکالی پیش آمده است");
                    } else {
                        LinearLayout lnsabadbottom = (LinearLayout) findViewById(R.id.lnsabadbottom);
                        lnsabadbottom.setVisibility(View.VISIBLE);
                        tahlilData(body);
                    }
                }
            }, false, Products.this, "", query).execute(urls + page);
        } else {
            String body = Func.getOffline(getApplicationContext(), urls, 0);
            tahlilData(body);
        }
    }

    private void MakeList(String body) {// body is json
        if (getResources().getBoolean(R.bool.show_category_in_product_activity)) {
//            adapter = new CatsAdapter(Products.this,
//                    Func.parseResultCats(body,"cats"));
//            if (adapter.getItemCount() == 0) {
//                recycleCats.setVisibility(View.GONE);
//                tvcats.setVisibility(View.GONE);
//            }
//            recycleCats.setAdapter(adapter);
            recycleCats.setVisibility(View.GONE);
            tvcats.setVisibility(View.GONE);
        } else {
            List<Cats_Adapter.MyList> items = new ArrayList<>();
            try {
                JSONObject response = new JSONObject(body);
                JSONArray posts = response.optJSONArray("cats");
                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.optJSONObject(i);
                    if (post.optString("mainCat").equals("1")) {
                        items.add(new Cats_Adapter.MyList(post.optString("name"), post.optString("id"), "0"));
                    } else {
                        Log.v("this", "paret " + post.optString("parrent"));
                        items.add(new Cats_Adapter.MyList(post.optString("name")
                                , post.optString("img"), post.optString("id"), post.optString("parrent")));
                    }
                }
                if (items.size() > 0) {
                    adapterCats = new Cats_Adapter(Products.this, items);
                    adapterCats.setMode(ExpandableRecyclerAdapter.MODE_ACCORDION);
                    recycleCats.setLayoutManager(new LinearLayoutManager(this));
                    recycleCats.setAdapter(adapterCats);
                    recycleCats.setVisibility(View.VISIBLE);
                } else {
                    recycleCats.setVisibility(View.GONE);
                    tvcats.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.v("this", e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private void tahlilData(String body) {
        taskrunning = false;
        if (page < 1) {
            //            adapter = new CatsAdapter(Products.this,
//                    Func.parseResultCatsProd(body));
//            if (adapter.getItemCount() == 0) {
//                recycleCats.setVisibility(View.GONE);
//                tvcats.setVisibility(View.GONE);
//            }
//            recycleCats.setAdapter(adapter);
            MakeList(body);
        }

        if (body.length() > 0) {
            if (productAdaper == null) {
                List<FeedItem> items = Func.parseResult(body);
                productAdaper = new MyAdapter2(Products.this, items);
                productAdaper.setFullWidth(true);
                if (items != null && items.size() < 20) {
                    loadmore = false;
                } else
                    loadmore = true;
                ScaleInAnimationAdapter scaldeAdapter = new ScaleInAnimationAdapter(productAdaper);
                scaldeAdapter.setDuration(300);
                recycle.setAdapter(scaldeAdapter);

                if (items == null || items.size() == 0) {
                    TextView tvnoitem = (TextView) findViewById(R.id.tvnoitem);
                    tvnoitem.setTypeface(typeface2);
                    tvnoitem.setVisibility(View.VISIBLE);
                }


                settings = getSharedPreferences("settings", MODE_PRIVATE);
                if (settings.getBoolean("grid", true)) {
                    GridLayout();
                } else {
                    ListLayout();
                }
            } else {
                List<FeedItem> items = Func.parseResult(body);
                if (items != null)
                    productAdaper.addAll(items);
            }

            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(body);
                JSONArray posts = jsonObj.optJSONArray("options");
                if (options != null && posts.length() > 0) {
                    options = new String[posts.length()];
                    countOptions = posts.length();
                    optionsId = new String[posts.length()];
                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject post = posts.optJSONObject(i);
                        options[i] = post.optString("option");
//                                options[i] =new String( post.optString("option").getBytes("ISO-8859-1"), "UTF-8");
                        optionsId[i] = post.optString("optionId");
                    }
                } else {
//                    tvfilter.setVisibility(View.INVISIBLE);
                }

                if (options != null) {
                    params = new ArrayList<>();
                    for (int m = 0; m < options.length; m++) {
                        JSONArray postss = jsonObj.optJSONArray("o" + optionsId[m]);
                        for (int i = 0; i < postss.length(); i++) {
                            JSONObject post = postss.optJSONObject(i);
                            Params_Items item = new Params_Items();
                            item.setValue(post.optString("option"));
                            item.setId(post.optString("optionId"));
                            item.setOnvan(options[m]);
                            item.setOption_id(optionsId[m]);
                            item.setChecked(false);
                            params.add(item);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mCustomProgressDialog.dismiss("");
    }

    private int getCount() {
        return mLayoutManager.findFirstVisibleItemPosition();
    }

    private void dialogFilter() {
        try {
            View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
            LinearLayout dynamicLl = (LinearLayout) view.findViewById(R.id.extra);

            Button dofilter = (Button) view.findViewById(R.id.dofilter);
            dofilter.setTypeface(typeface2);

            formSpiiners = new ListView[countOptions];
            SpinnerSubcatId = new Integer[countOptions];

            for (int i = 0; i < optionsId.length; i++) {
                LayoutInflater vi = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View l = vi.inflate(R.layout.new_row_checkbox, null);

                final TextView tvonvannew;
                tvonvannew = (TextView) l.findViewById(R.id.tvonvan);
                tvonvannew.setTypeface(typeface2);
                tvonvannew.setText(options[i]);

                final ListView spinner1 = (ListView) l.findViewById(R.id.listview);
                spinner1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                spinner1.setTag(optionsId[i]);
                final List<ParamsVal> labless = new ArrayList<>();
                for (int m = 0; m < params.size(); m++) {
                    if (params.get(m).getOption_id() == optionsId[i]) {
                        ParamsVal vals = new ParamsVal();
                        vals.setName(params.get(m).getValue());
                        vals.setId(params.get(m).getId());
                        vals.setChecked(params.get(m).getChecked());
                        labless.add(vals);
                    }
                }
                final FilterAdapter dataAdapters = new FilterAdapter(this, R.layout.spinner_item, labless);
                spinner1.setAdapter(dataAdapters);
                spinner1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (labless.get(position).getChecked())
                            labless.get(position).setChecked(false);
                        else
                            labless.get(position).setChecked(true);

                        for (int m = 0; m < params.size(); m++) {
                            if (params.get(m).getId().equals(labless.get(position).getId())) {
                                if (params.get(m).getChecked()) {
                                    params.get(m).setChecked(false);
                                } else {
                                    params.get(m).setChecked(true);
                                }
                                break;
                            }
                        }
                        dataAdapters.notifyDataSetChanged();
                    }
                });
                formSpiiners[i] = spinner1;

                dynamicLl.addView(l);
                dynamicLl.refreshDrawableState();
            }

            final Dialog mBottomSheetDialog = new Dialog(Products.this, R.style.MaterialDialogSheet);
            mBottomSheetDialog.setContentView(view);
            mBottomSheetDialog.setCancelable(true);
            mBottomSheetDialog.getWindow().setLayout(450, LinearLayout.LayoutParams.MATCH_PARENT);
            mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM | Gravity.RIGHT);
            mBottomSheetDialog.show();

            dofilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String properties = "";
                    for (int i = 0; i < formSpiiners.length; i++) {
                        String opId = formSpiiners[i].getTag().toString();
                        properties += opId + "id";
                        for (int m = 0; m < params.size(); m++) {
                            if (params.get(m).getOption_id().equals(opId)) {
                                if (params.get(m).getChecked())
                                    properties += params.get(m).getId() + ",";
                            }
                        }
                        properties += "0-";
                    }
                    Log.v("this", properties);
                    mBottomSheetDialog.dismiss();

                    if (urls.contains("filter")) {
                        urls = urls.substring(0, urls.indexOf("&filter"));
                    }
                    if (urls.contains("page")) {
                        urls = urls.substring(0, urls.indexOf("&page"));
                    }
                    page = 0;
                    urls = urls + "&filter=" + properties;
                    urls = urls + "&page=0";
                    productAdaper = null;
                    getData();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("this", e.getMessage());
        }
    }

    private void declare() {
        taskrunning = true;
        bl = getIntent().getExtras();
        mCustomProgressDialog = new CustomProgressDialog(this);
        mCustomProgressDialog.show("");
        pg = (ProgressBar) findViewById(R.id.pg);

        typeface2 = Typeface.createFromAsset(getAssets(), "IRAN Sans.ttf");

        tvcats = (TextView) findViewById(R.id.tvcats);
        tvcats.setTypeface(typeface2);

        tvsort = (TextView) findViewById(R.id.tvsort);
        tvsort.setTypeface(typeface2);
        tvsort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSort();
            }
        });

        ImageView sortimg = (ImageView) findViewById(R.id.sortimg);
        sortimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSort();
            }
        });

//        tvfilter = (TextView) findViewById(R.id.tvfilter);
//        tvfilter.setTypeface(typeface2);
//        tvfilter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogFilter();
//            }
//        });

        view_type = (ImageView) findViewById(R.id.view_type);
        view_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (grid) {
                    ListLayout();
                } else {
                    GridLayout();
                }
            }
        });

        TextView tvshow = (TextView) findViewById(R.id.tvshow);
        tvshow.setTypeface(typeface2);
        tvshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (grid) {
                    ListLayout();
                } else {
                    GridLayout();
                }
            }
        });

        recycleCats = (RecyclerView) findViewById(R.id.recycleCats);
        LinearLayoutManager linearCats = new LinearLayoutManager(this);
        recycleCats.setLayoutManager(linearCats);
        recycleCats.setNestedScrollingEnabled(false);

        nested = (NestedScrollView) findViewById(R.id.nested);

        recycle = (RecyclerView) findViewById(R.id.RecyclerView);
        recycle.addItemDecoration(new SpacesItemDecoration(5));
        recycle.setNestedScrollingEnabled(false);
    }

    private void GridLayout() {
        grid = true;
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;
        int columns = Math.round(dpWidth / 160);
        mLayoutManager = new RtlGridLayoutManager(this, columns);
        recycle.setLayoutManager(mLayoutManager);
        if (productAdaper != null)
            productAdaper.setActionMode(null);

        SharedPreferences.Editor pref = settings.edit();
        pref.putBoolean("grid", true);
        pref.commit();

        view_type.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_vertical));


        nested.setOnScrollChangeListener(new EndlessParentScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int pages, int totalItemsCount) {
                visibleItemCount = recycle.getChildCount();
                if (grid) {
                    totalItemCount = mLayoutManager.getItemCount();
                    firstVisibleItem = getCount();
                } else {
                    totalItemCount = mLayoutManager2.getItemCount();
                    firstVisibleItem = mLayoutManager2.findFirstVisibleItemPosition();
                }
                if ((totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    if (taskrunning != null && taskrunning == false && loadmore) {
                        page = page + 1;
                        getData();
                        ;
                    }
                }
            }
        });
    }

    private void ListLayout() {
        grid = false;
        mLayoutManager2 = new LinearLayoutManager(this);
        recycle.setLayoutManager(mLayoutManager2);
        ActionMode actionmod = new ActionMode() {
            @Override
            public void setTitle(CharSequence title) {

            }

            @Override
            public void setTitle(int resId) {

            }

            @Override
            public void setSubtitle(CharSequence subtitle) {

            }

            @Override
            public void setSubtitle(int resId) {

            }

            @Override
            public void setCustomView(View view) {

            }

            @Override
            public void invalidate() {

            }

            @Override
            public void finish() {

            }

            @Override
            public Menu getMenu() {
                return null;
            }

            @Override
            public CharSequence getTitle() {
                return null;
            }

            @Override
            public CharSequence getSubtitle() {
                return null;
            }

            @Override
            public View getCustomView() {
                return null;
            }

            @Override
            public MenuInflater getMenuInflater() {
                return null;
            }
        };
        if (productAdaper != null)
            productAdaper.setActionMode(actionmod);
        SharedPreferences.Editor pref = settings.edit();
        pref.putBoolean("grid", false);
        pref.commit();

        view_type.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_grid));


        nested.setOnScrollChangeListener(new EndlessParentScrollListener(mLayoutManager2) {
            @Override
            public void onLoadMore(int pages, int totalItemsCount) {
                visibleItemCount = recycle.getChildCount();
                if (grid) {
                    totalItemCount = mLayoutManager.getItemCount();
                    firstVisibleItem = getCount();
                } else {
                    totalItemCount = mLayoutManager2.getItemCount();
                    firstVisibleItem = mLayoutManager2.findFirstVisibleItemPosition();
                }
                if ((totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    if (taskrunning != null && taskrunning == false && loadmore) {
                        page = page + 1;
                        getData();
                        ;
                    }
                }
            }
        });
    }


    private void sortButton() {
        final ActionButton actionButton2 = (ActionButton) findViewById(R.id.sort);
        actionButton2.setButtonColor(Color.parseColor("#DA4336"));
        actionButton2.setButtonColorPressed(Color.parseColor("#C34B40"));
        actionButton2.playShowAnimation();
        actionButton2.setShadowRadius(8);
        actionButton2.setShadowXOffset(0);
        actionButton2.setShadowYOffset(0);
//        actionButton.setImageDrawable(ContextCompat.getDrawable(Products.this, R.drawable.ic_view_module_2_white_24dp));
        actionButton2.setImageDrawable(ContextCompat.getDrawable(Products.this, R.drawable.ic_clear_all_white_24dp));

        actionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogSort();
            }
        });

    }

    private void DialogSort() {
        final Dialog dialog = new Dialog(Products.this, R.style.DialogStyler);
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
                , "محصول جدیدتر به قدیم تر", "محصول قدیمی تر به جدیدتر", "محبوبیت", "پربازدیدترین", "پرفروش ترین"
//                        ,"تعداد فروش"
        };

        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(Products.this, android.R.layout.simple_list_item_1, sort);
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
                        urls = urls + "&sort=favs";
                        break;
                    case 6:
                        urls = urls + "&sort=numvisit";
                        break;
                    case 7:
                        urls = urls + "&sort=numforush";
                        break;
                }
                urls = urls + "&page=0";
                productAdaper = null;
                recycle.setAdapter(null);
                page = 0;
                getData();

                dialog.dismiss();
            }
        });


//                if (grid) {
//                    actionButton.setImageDrawable(ContextCompat.getDrawable(Products.this, R.drawable.ic_view_stream_2_white_24dp));
//                    ListLayout();
//                } else {
//                    actionButton.setImageDrawable(ContextCompat.getDrawable(Products.this, R.drawable.ic_view_module_2_white_24dp));
//                    GridLayout();
//                }
    }


    private void floatingButton() {
        final ActionButton actionButton = (ActionButton) findViewById(R.id.action_button);
        actionButton.setButtonColor(Color.parseColor("#DA4336"));
        actionButton.setButtonColorPressed(Color.parseColor("#C34B40"));
        actionButton.playShowAnimation();
        actionButton.setShadowRadius(8);
        actionButton.setShadowXOffset(0);
        actionButton.setShadowYOffset(0);
        actionButton.setImageDrawable(ContextCompat.getDrawable(Products.this, R.drawable.ic_view_module_2_white_24dp));

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (grid) {
                    actionButton.setImageDrawable(ContextCompat.getDrawable(Products.this, R.drawable.ic_view_stream_2_white_24dp));
                    ListLayout();
                } else {
                    actionButton.setImageDrawable(ContextCompat.getDrawable(Products.this, R.drawable.ic_view_module_2_white_24dp));
                    GridLayout();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        LeftSabad lsabad = new LeftSabad(this);
        if (productAdaper != null) {
            productAdaper.notifyDataSetChanged();
            recycle.setAdapter(productAdaper);
        }
    }

    private void actionbar() {
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        if (bl.getString("title") != null)
            action.MakeActionBar(bl.getString("title"));
        else
            action.MakeActionBar(bl.getString("onvan"));

        ImageView img_sabad = (ImageView) findViewById(R.id.img_sabad);
        img_sabad.setVisibility(View.GONE);
        TextView tvnumsabad = (TextView) findViewById(R.id.text_numkharid);
        tvnumsabad.setVisibility(View.GONE);
    }

    @Override
    public void checkSabad() {
        actionbar();
    }

}
