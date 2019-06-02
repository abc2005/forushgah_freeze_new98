package com.persiandesigners.freeze;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;

import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by navid on 6/1/2017.
 */
public class Cats_digi extends AppCompatActivity {
    Typeface typeface;
    List<CatItems> CatItems = null;
    List<CatItems> SubCatItems = null;
    private TabLayout tabLayout;
    RecyclerView rc_cats;
    String catId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cats2);

        Bundle bl=getIntent().getExtras();
        if(bl!=null)
            catId=bl.getString("catId");

        declare();
        actionbar();

        long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        new Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                Log.v("this", body);
                if (body.equals("errordade")) {
                    MyToast.makeText(getApplicationContext(), "اشکالی پیش آمده است");
                } else {
                    int pos=0;
                    CatItems = new ArrayList<>();
                    try {
                        JSONObject response = new JSONObject(body);
                        JSONArray posts = response.optJSONArray("contacts");
                        int posMainCat=0;
                        for (int i = 0; i < posts.length(); i++) {
                            JSONObject post = posts.optJSONObject(i);
                            CatItems item = new CatItems();
                            item.setName(post.optString("name"));
                            item.setId(post.optString("id"));
                            if(catId==null && i==0)
                                catId=post.optString("id");
                            item.setThumb(post.optString("thumb"));
                            String p=post.optString("parrent");
                            if(p.equals("0"))
                                posMainCat++;
                            item.setParrent(p);
                            if(post.optString("parrent").equals("0")){
                                TabLayout.Tab tab = tabLayout.newTab().setText(post.optString("name"));
                                tab.setTag(item.getId());
                                tabLayout.addTab(tab);
                            }

                            if(post.optString("id").equals(catId)){
                                pos=posMainCat;
                            }
                            CatItems.add(item);
                        }
                        pos--;
                        if(pos>=0){
                            final int finalPos = pos;
                            tabLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    int right = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(finalPos).getRight();
                                    tabLayout.scrollTo(right,0);
                                    tabLayout.getTabAt(finalPos).select();
                                    MakeSubCats(catId);
                                }
                            });
                        }else{
                            final int finalPos = tabLayout.getTabCount();
                            tabLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    int right = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(finalPos-1).getRight();
                                    tabLayout.scrollTo(right,0);
                                    tabLayout.getTabAt(finalPos-1).select();
                                    MakeSubCats(catId);
                                }
                            });
                        }
                    } catch (Exception e) {

                    }

                }
            }
        }, true, Cats_digi.this, "").execute(getString(R.string.url) + "/getAllCatsSubcats.php?n=" + number);
    }

    private void declare() {
        typeface = Typeface.createFromAsset(this.getAssets(), "IRAN Sans Bold.ttf");

        rc_cats=(RecyclerView)findViewById(R.id.rc_cats);
        rc_cats.setLayoutManager(new LinearLayoutManager(this));
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                MakeSubCats(tab.getTag().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void MakeSubCats(String cat) {
        rc_cats.setAdapter(null);
        SubCatItems=new ArrayList<CatItems>();
        for (int i=0;i<CatItems.size();i++){
            if(CatItems.get(i).getParrent().equals(cat)){
                SubCatItems.add(CatItems.get(i));
            }
        }
        CatsAdapterDigi adapter = new CatsAdapterDigi(Cats_digi.this, SubCatItems);
        rc_cats.setAdapter(adapter);
    }

    private void actionbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        action.MakeActionBar("دسته بندی ها");
        Func.checkSabad(Cats_digi.this);
    }
}
