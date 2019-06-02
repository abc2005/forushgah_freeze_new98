package com.persiandesigners.freeze;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.persiandesigners.freeze.Util.CustomProgressDialog;
import com.persiandesigners.freeze.Util.DatabaseHandler;
import com.persiandesigners.freeze.Util.MyAdapterCallBack;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.NetworkAvailable;
import com.persiandesigners.freeze.Util.OnTaskFinished;
import com.software.shell.fab.ActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import ir.viewpagerindicator.CirclePageIndicator;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class Detailss extends AppCompatActivity implements OnClickListener,MyAdapterCallBack {
    Typeface typeface;
    ProgressDialog pDialog;
    ViewPager viewPager;// gallery
    PagerAdapter adapter;
    String[] imges, astate;
    int ad_id;
    int num_mojud;
    private static final String TAG_CONTACTS = "contacts";
    JSONArray contacts = null;
    String jsonStr = "";
    Bundle bl;
    DatabaseHandler db;
    String urls;
    ArrayList<HashMap<String, String>> dataC;
    String name, price,price2, img, numToAdd;
    TextView tvnumsabad, tvprice0;
    TextView tvonvan, tvcat, tvtext, tvprice, tvmore, tvprice2;
    LinearLayout lnbuy;
    TextView tvvazn, tvzamaneAmadeErsam;
    Toolbar toolbar;
    CustomProgressDialog mCustomProgressDialog;
    ImageView min, plus;
    TextView tedad;
    RatingBar rate;
    RecyclerView similar;

    List<Params_Items> params ;
    Spinner[]formSpiiners;
    Integer[] SpinnerSubcatId;
    int countOptions;
    String[] optionsId = null;
    String[] options = null;
    LinearLayout lntedad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        declare();

        bl = getIntent().getExtras();
        if (bl != null) {
            ad_id = Integer.parseInt(bl.getString("productid"));
            urls = getString(R.string.url) + "/getDetails.php?id=" + ad_id+"&w="+Func.getScreenWidth(this)+"&uid="+Func.getUid(this)+"&moreDetails="+getResources().getBoolean(R.bool.get_product_brand_tedad_and_more);
            Log.v("this",urls);
            if (Func.isInternet(getApplicationContext()) ||
                    Func.hasOffline(getApplicationContext(), "Details", ad_id))
                new GetContacts().execute();
            else {
                startActivity(new Intent(this, NoInternet.class));
                finish();
            }
        }

        if (db.isAddedSabad(ad_id + "")) {
            tedad.setText(db.getNumProd(ad_id + "") + "");
            SetPrices(db.getNumProd(ad_id + ""));
        } else {
            tedad.setText("1");
        }

        actionbar();
        floatingActionBar();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.plus:
                ChangeTedad(ad_id, "plus");
                break;
            case R.id.minus:
                ChangeTedad(ad_id, "min");
                break;
        }
    }

    private void ChangeTedad(int ids, String doWhat) {
        String id = ids + "";
        int numMojjud =num_mojud;
        if (!db.isOpen())
            db.open();

        if (db.isAddedSabad(id)) {//agar be sabad ezafe shode bud
            int currentNum = db.getNumProd(id);

            if (doWhat.equals("plus")) {
                currentNum++;
                if (currentNum <= numMojjud) {
                    db.updateNum(Integer.parseInt(id), currentNum);
                    tedad.setText(currentNum + "");
                } else
                    MyToast.makeText(Detailss.this, getString(R.string.notmojud));
            } else {//minus
                currentNum--;
                if (currentNum > 0) {
                    db.updateNum(Integer.parseInt(id), currentNum);
                    tedad.setText(currentNum + "");
                }else if(currentNum<=2){
                    lntedad.setVisibility(View.GONE);
                    lnbuy.setVisibility(View.VISIBLE);
                    db.removeFromSabad(id);
                    actionbar();
                }
            }
            SetPrices(currentNum);
        } else {//not added to sabad before
            int currentNum = Integer.parseInt(numToAdd);
            if (doWhat.equals("plus")) {
                currentNum++;
                if (currentNum <= numMojjud) {
                    numToAdd = currentNum + "";
                    tedad.setText(numToAdd + "");
                } else
                    MyToast.makeText(Detailss.this, getString(R.string.notmojud));
            } else {//minus
                currentNum--;
                if (currentNum > 0) {
                    numToAdd = currentNum + "";
                    tedad.setText(numToAdd + "");
                }else if(currentNum<=2){
                    lntedad.setVisibility(View.GONE);
                    lnbuy.setVisibility(View.VISIBLE);
                    db.removeFromSabad(id);
                    actionbar();
                }
            }
            SetPrices(currentNum);
        }
    }

    private void SetPrices(int currentNum) {
       try {
            int omde_num=Integer.parseInt(dataC.get(0).get("omde_num"));
            if(omde_num>0) {
                String omdePrice = (dataC.get(0).get("omde_price"));
                if (currentNum >= omde_num) {
                    int price=Integer.parseInt(omdePrice)*currentNum;
                    tvprice.setText(Func.getCurrency(price+"") + " تومان");
                    tvprice0.setText(Func.getCurrency(omdePrice+"") + " تومان");
                } else {
                    int prices=Integer.parseInt(price)*currentNum;
                    tvprice.setText(Func.getCurrency(prices+"") + " تومان");
                    tvprice0.setText(Func.getCurrency(price+"") + " تومان");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkSabad() {
        actionbar();
    }


    private class GetContacts extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
        boolean goterr = false;

		 @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(NetworkAvailable.isNetworkAvailable(Detailss.this)){
                mCustomProgressDialog = new CustomProgressDialog(Detailss.this);
                mCustomProgressDialog.show("");
            }
        }
		
        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Void... arg0) {
            if (Func.isInternet(getApplicationContext())) {
                HttpURLConnection urlConnection;
                try {
                    URL url = new URL(urls);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    int statusCode = urlConnection.getResponseCode();

                    if (statusCode == 200) {
                        BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = r.readLine()) != null) {
                            response.append(line);
                        }
                        jsonStr = response.toString();
                    } else {
                        return null;
                    }

                } catch (Exception e) {
                    goterr = true;
                }
                Func.insertOffline(getApplicationContext(), jsonStr, "Details", ad_id);
            } else {
                jsonStr = Func.getOffline(getApplicationContext(), "Details", ad_id);
            }
            dataC = new ArrayList<HashMap<String, String>>();

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    contacts = jsonObj.getJSONArray(TAG_CONTACTS);
                    JSONObject c = contacts.getJSONObject(0);
                    HashMap<String, String> contact = new HashMap<String, String>();
                    contact.put("name", c.getString("name"));
                    name = c.getString("name");
                    price = c.getString("price");
                    price2 = c.getString("price2");
                    img = c.getString("img");
                    contact.put("price", c.getString("price"));
                    contact.put("cat", c.getString("cat"));
                    contact.put("code", c.getString("code"));
                    contact.put("price2", c.getString("price2"));
                    contact.put("admins_id", c.getString("admins_id"));
                    contact.put("tozih", c.getString("tozih"));
                    contact.put("img", c.getString("img"));
                    contact.put("isOpen", c.getInt("isOpen") + "");
                    contact.put("majazi", c.getString("majazi"));
                    contact.put("vazn", c.getString("vazn"));
                    contact.put("omde_num", c.getString("omde_num"));
                    contact.put("omde_price", c.getString("omde_price"));
                    contact.put("zamaneAmadeErsam", c.getString("zamaneAmadeErsam"));
                    contact.put("rate", c.getString("rate"));
                    contact.put("other", c.getString("other"));
                    contact.put("pimg", c.getString("pimg"));
                    contact.put("num", c.getString("num"));
                    num_mojud=c.getInt("num");
                    contact.put("more",c.getString("more"));
                    contact.put("cat",c.getString("cat"));
                    contact.put("h", new String(c.getString("h")));
                    contact.put("video", c.getString("video"));
                    dataC.add(contact);
                } catch (JSONException e) {
                    Log.v("this", e.getMessage());
                    goterr = true;
                }

                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray posts = jsonObj.optJSONArray("options");
                    if (posts.length() > 0) {
                        options = new String[posts.length()];
                        countOptions = posts.length();
                        optionsId = new String[posts.length()];
                        for (int i = 0; i < posts.length(); i++) {
                            JSONObject post = posts.optJSONObject(i);
                            options[i] = post.optString("option");
                            optionsId[i] = post.optString("optionId");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (options != null) {
                    params = new ArrayList<>();
                    for (int m = 0; m < options.length; m++) {
                        try {
                            JSONObject jsonObj = new JSONObject(jsonStr);
                            JSONArray posts = jsonObj.optJSONArray("o" + optionsId[m]);
                            for (int i = 0; i < posts.length(); i++) {
                                JSONObject post = posts.optJSONObject(i);
                                Params_Items item = new Params_Items();
                                item.setValue(post.optString("option"));
                                item.setId(post.optString("id"));
                                item.setPrice(post.optString("price"));
                                item.setNum(post.optString("num"));
                                item.setOnvan(options[m]);
                                item.setOption_id(optionsId[m]);
                                params.add(item);
                            }
                        } catch (JSONException e) {
                            Log.v("this", e.getMessage());
                            e.printStackTrace();

                        }
                    }
                }
            } else {
                goterr = true;
            }
            return dataC;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
            super.onPostExecute(result);

            if (!isCancelled() && goterr == false && result.size()>0) {
                MyAdapter2 adapterSimilar = new MyAdapter2(Detailss.this, Func.parseResultSimilar(jsonStr));
                ScaleInAnimationAdapter scaldeAdapter = new ScaleInAnimationAdapter(adapterSimilar);
                scaldeAdapter.setDuration(300);
                similar.setAdapter(scaldeAdapter);

                if(dataC.get(0).get("video").length()>0 && dataC.get(0).get("video").contains("http")){
                    VideoPart(dataC.get(0).get("video"));
                }

                tvonvan.setText(dataC.get(0).get("name"));
                //tvcat.setText(Html.fromHtml(dataC.get(0).get("cat")));

                if (dataC.get(0).get("more").length() > 0)
                    tvmore.setText(Html.fromHtml(dataC.get(0).get("more")));
                else
                    tvmore.setVisibility(View.GONE);

                if (dataC.get(0).get("rate").equals("0"))
                    rate.setVisibility(View.GONE);
                else
                    rate.setRating(Float.parseFloat(dataC.get(0).get("rate")));


                TextView tvprice2 = (TextView) findViewById(R.id.tvprice2);
                tvprice2.setTypeface(typeface);
                tvprice2.setPaintFlags(tvprice2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                try {
                    tvprice0.setText(Func.getCurrency(price) + " تومان");
                } catch (Exception e) {
                    tvprice0.setText((price) + "  تومان");
                }
                if (!dataC.get(0).get("price2").equals("0") && dataC.get(0).get("price2").length() > 0) {
                    tvprice2.setVisibility(View.VISIBLE);
                    try {
                        tvprice2.setText("قیمت بازار :  " + Func.getCurrency(dataC.get(0).get("price2")) + " تومان");
                    } catch (Exception e) {
                        tvprice2.setText("قیمت بازار : " + (dataC.get(0).get("price2")) + "  تومان");
                    }
                }else{
                    tvprice0.setTextColor(Color.parseColor("#000000"));
                    tvprice0.setBackgroundResource(0);
                }

                if (num_mojud==0 || db.isAddedSabad(ad_id + "")) {
                    lnbuy.setVisibility(View.GONE);
					NestedScrollView scroll=(NestedScrollView)findViewById(R.id.scroll);
                    scroll.setPadding(0,0,0,0);
                }

                if (dataC.get(0).get("tozih").trim().length() > 5) {
                    CardView ln_tozihat=(CardView)findViewById(R.id.ln_tozihat);
                    if(getResources().getBoolean(R.bool.tozihat_in_details_page))
                        ln_tozihat.setVisibility(View.VISIBLE);

                    TextView tv_tozihat=(TextView)findViewById(R.id.tv_tozihat);
                    tv_tozihat.setTypeface(typeface);

                    final HtmlTextView text = (HtmlTextView) findViewById(R.id.html);
                    text.setVisibility(View.VISIBLE);
                    text.setTypeface(typeface);
                    text.setHtmlFromString(dataC.get(0).get("tozih"), new HtmlTextView.RemoteImageGetter());
                }

                if (dataC.get(0).get("vazn").length() == 0 && dataC.get(0).get("zamaneAmadeErsam").length() == 0
                        && dataC.get(0).get("more").length() == 0 ) {
                    CardView more = (CardView) findViewById(R.id.more);
                    if(getResources().getBoolean(R.bool.tozihat_in_details_page))
                        more.setVisibility(View.GONE);
                }

                if (dataC.get(0).get("vazn").length() == 0)
                    tvvazn.setVisibility(View.GONE);
                tvvazn.setText(("وزن : " + dataC.get(0).get("vazn")));

                if (dataC.get(0).get("zamaneAmadeErsam").length() == 0)
                    tvzamaneAmadeErsam.setVisibility(View.GONE);
                tvzamaneAmadeErsam.setText(("زمان حدودی آماده سازی : " + dataC.get(0).get("zamaneAmadeErsam")));

                if (num_mojud==0) {
                    tvprice.setText("ناموجود");
                } else {
                    try {
                        tvprice.setText(Func.getCurrency(price + " تومان"));
                    } catch (Exception e) {
                        tvprice.setText((price + " تومان"));
                    }
                }

                if (dataC.get(0).get("majazi").equals("1") || getResources().getBoolean(R.bool.show_minues_plus_in_details_page)==false) {
                    lntedad.setVisibility(View.INVISIBLE);
                }

                lnbuy.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (num_mojud==0)
                            MyToast.makeText(Detailss.this, ("محصول موجود نیست"));
                        else {
                            if(getResources().getBoolean(R.bool.has_options) && options!=null && options.length>0)
                                addToSabad();
                            else if (getResources().getBoolean(R.bool.multiseller) && dataC.get(0).get("isOpen").equals("0"))
                                MyToast.makeText(Detailss.this, "فروشگاه بسته است");
                            else
                                AddSabad(false,"");
                        }
                    }
                });

                if (result.get(0).get("img").length() > 1 && !result.get(0).get("img").equals("0#")
                        && !result.get(0).get("img").equals("0"))// عکس داشتیم
                    makeSlider(result.get(0).get("img"),result.get(0).get("h"));
                else {
                    RelativeLayout slidelayout = (RelativeLayout) findViewById(R.id.slidelayout);
                    slidelayout.setVisibility(View.GONE);
                    AppBarLayout applayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
                    applayout.setExpanded(false);
                }

            } else {
                MyToast.makeText(Detailss.this, (Detailss.this.getResources().getString(R.string.problemload)));
            }
           if(NetworkAvailable.isNetworkAvailable(Detailss.this))
                mCustomProgressDialog.dismiss("");
        }
    }

    private void AddSabad(boolean b,String property) {
        int prdId = Integer.parseInt(Integer.toString(ad_id));

        if (db.checkAddedBefore(prdId) <= 0) {
            Boolean stat=db.sabadkharid(dataC.get(0).get("name"),dataC.get(0).get("pimg")
                    ,prdId, price
                    , num_mojud+"", numToAdd,price2,property,0,Integer.parseInt(dataC.get(0).get("omde_num")),Integer.parseInt(dataC.get(0).get("omde_price")),dataC.get(0).get("admins_id"));
            if(Func.isMultiSeller(Detailss.this) && stat==false){
                Func.AlertMultipleStore(Detailss.this);
            }

            if (b == false && stat) {
                MyToast.makeText(Detailss.this, ("این محصول به سبد خرید شما اضافه شد"));
                lntedad.setVisibility(View.VISIBLE);
                lnbuy.setVisibility(View.GONE);
					NestedScrollView scroll=(NestedScrollView)findViewById(R.id.scroll);
                    scroll.setPadding(0,0,0,0);
            }

            Detailss activity = (Detailss) Detailss.this;
            activity.getMyData();
        } else
            MyToast.makeText(Detailss.this, ("این محصول قبلا به سبد خرید شما اضافه شده است"));

    }

    private void addToSabad() {
        if(getResources().getBoolean(R.bool.has_options)==false || params==null || params.size()==0)
            AddSabad(false,"");//true -->go to sabadKHarid
        else{
            openBottomSheet ();
        }
    }

    private void openBottomSheet() {
        try {
            View view = getLayoutInflater ().inflate (R.layout.bottom_sheet_sabad, null);
            ImageView add_sabad=(ImageView) view.findViewById(R.id.add_sabad);

            LinearLayout dynamicLl=(LinearLayout)view.findViewById(R.id.extra);

            formSpiiners=new Spinner[countOptions];
            SpinnerSubcatId=new Integer[countOptions];

            for (int i=0; i<optionsId.length;i++){
                LayoutInflater vi = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View l = vi.inflate(R.layout.new_row_spinner, null);

                final TextView tvonvannew;
                tvonvannew=(TextView)l.findViewById(R.id.tvonvan);
                tvonvannew.setTypeface(typeface);
                tvonvannew.setText(options[i]);

                final Spinner spinner1=(Spinner)l.findViewById(R.id.spinner);
                spinner1.setTag(optionsId[i]);
                List<String> labless=new ArrayList<String>();
                for (int m=0;m<params.size();m++){
                    if(params.get(m).getOption_id()==optionsId[i]){
                        labless.add(params.get(m).getValue()+ " ("+
                                params.get(m).getPrice()+" تومان)");
                    }
                }
                spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        String optionId=spinner1.getTag().toString();
                        int pos=0;
                        for (int m=0;m<params.size();m++){
                            if(params.get(m).getOption_id()==optionId){
                                if(position==pos){
                                    num_mojud=Integer.parseInt(params.get(m).getNum());
                                    price=params.get(m).getPrice();

                                    tvprice.setText(Func.getCurrency(price) + " تومان");
                                    break;
                                }
                                pos++;
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                ArrayAdapter<String> dataAdapters = new ArrayAdapter<String>(this, R.layout.spinner_item,
                        labless);
                spinner1.setAdapter(dataAdapters);
                formSpiiners[i]=spinner1;

                dynamicLl.addView(l);
                dynamicLl.refreshDrawableState();
            }

            final Dialog mBottomSheetDialog = new Dialog (Detailss.this,R.style.MaterialDialogSheet);
            mBottomSheetDialog.setContentView (view);
            mBottomSheetDialog.setCancelable (true);
            mBottomSheetDialog.getWindow ().setLayout (LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            mBottomSheetDialog.getWindow ().setGravity (Gravity.BOTTOM);
            mBottomSheetDialog.show ();

            add_sabad.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String properties ="";
                    for(int i=0;i<formSpiiners.length;i++){
                        Spinner spinner1=formSpiiners[i];

                        String optionId=spinner1.getTag().toString();
                        int pos=0;
                        for (int m=0;m<params.size();m++){
                            if(params.get(m).getOption_id()==optionId){
                                if(spinner1.getSelectedItemPosition()==pos){
                                    num_mojud=Integer.parseInt(params.get(m).getNum());
                                    price=params.get(m).getPrice();
                                    properties+=optionId+"-"+params.get(m).getId()+"-"+ ad_id+"#";
                                    break;
                                }
                                pos++;
                            }
                        }
                    }
                    AddSabad(false,properties);
                    mBottomSheetDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("this",e.getMessage());
        }
    }

    private void makeSlider(String bigimg, String height) {
        AutoScrollViewPager viewPager;// gallery
        PagerAdapter adapter;
        CirclePageIndicator mIndicator;

        if (bigimg != null && bigimg.length() > 0) {
            String[] img;
            int counter = 0;
            for (int i = 0; i < bigimg.length(); i++) {
                if (bigimg.charAt(i) == '#') {
                    counter++;
                }
            }

            img = new String[counter];
            StringTokenizer tokenizer = new StringTokenizer(bigimg, "#");
            counter = 0;
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                img[counter] = token;
                counter++;
            }
            viewPager = (AutoScrollViewPager) findViewById(R.id.pager);
            adapter = new ViewPagerAdaptergallery(Detailss.this, img, "details");
            int h=Integer.parseInt(height) ;
            if(h>0)
                viewPager.getLayoutParams().height=h ;
            viewPager.setAdapter(adapter);
            viewPager.startAutoScroll(5000);
            viewPager.setInterval(5000);

            // ViewPager Indicator
            mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
            mIndicator.setViewPager(viewPager);
            mIndicator.setCurrentItem(adapter.getCount() - 1);
        }

    }

    private void declare() {
        typeface = Typeface.createFromAsset(this.getAssets(), "IRAN Sans.ttf");
        db = new DatabaseHandler(this);
        db.open();
        numToAdd = "1";
        lntedad=(LinearLayout)findViewById(R.id.lntedad);
        rate = (RatingBar) findViewById(R.id.rtbProductRating);

        tvprice0 = (TextView) findViewById(R.id.tvprice0);
        tvprice0.setTypeface(typeface);
        tedad = (TextView) findViewById(R.id.tvtedad);
        tedad.setTypeface(typeface);
        min = (ImageView) findViewById(R.id.minus);
        plus = (ImageView) findViewById(R.id.plus);
        min.setOnClickListener(this);
        plus.setOnClickListener(this);

        tvonvan = (TextView) findViewById(R.id.tvonvan);
        tvonvan.setTypeface(typeface);

        tvcat = (TextView) findViewById(R.id.tvcat);
        tvcat.setTypeface(typeface);
        tvprice = (TextView) findViewById(R.id.tvprice);
        tvprice.setTypeface(typeface);
        /*tvprice2=(TextView) findViewById(R.id.tvprice2);
        tvprice2.setTypeface(typeface);
        tvprice2.setPaintFlags(tvprice2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);*/
        tvmore = (TextView) findViewById(R.id.tvmore);
        tvmore.setTypeface(typeface);
        lnbuy = (LinearLayout) findViewById(R.id.lnbuy);

        TextView tv1 = (TextView) findViewById(R.id.tv1);
        tv1.setTypeface(typeface);
        tv1.setText("افزودن به سبد");

        tvvazn = (TextView) findViewById(R.id.tvvazn);
        tvvazn.setTypeface(typeface);
        tvzamaneAmadeErsam = (TextView) findViewById(R.id.tvzaman);
        tvzamaneAmadeErsam.setTypeface(typeface);

        similar = (RecyclerView) findViewById(R.id.similar);
        similar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        TextView comments = (TextView) findViewById(R.id.comments);
        comments.setTypeface(typeface);
        comments.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Detailss.this, Comments.class);
                in.putExtra("id", ad_id);
                startActivity(in);
            }
        });
        TextView joziyat = (TextView) findViewById(R.id.joziyat);
        joziyat.setTypeface(typeface);
        joziyat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent (Detailss.this,Vijegi.class);
                in.putExtra("data",dataC.get(0).get("tozih"));
                in.putExtra("onvan",name);
                in.putExtra("id",ad_id);
                startActivity(in);
            }
        });

    }

    public String getMyData() {
        Func.checkSabadKharid(Detailss.this, tvnumsabad);
        return "ok";
    }

    // /تعداد داده های سبد خرید
    private void actionbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);


//        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
//        collapsingToolbarLayout.setBackground(getResources().getDrawable(R.drawable.bgclp));

        tvnumsabad = (TextView) findViewById(R.id.text_numkharid);
		    tvnumsabad.setTypeface(typeface);

        ImageView img_sabad=(ImageView)findViewById(R.id.img_sabad);
        img_sabad.setOnClickListener(new OnClickListener() {
            // sabad kharid click  shod
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Detailss.this,SabadKharid_s2.class));
                overridePendingTransition(R.anim.go_up,R.anim.go_down);
            }
        });
        //Func.checkSabadKharid(Detailss.this, rlsabad, tvnumsabad);
        ImageView imgback = (ImageView) findViewById(R.id.back);
        imgback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        ImageView img_share = (ImageView) findViewById(R.id.img_share);
        if(getResources().getBoolean(R.bool.has_website)==false)
            img_share.setVisibility(View.GONE);

        img_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = (dataC.get(0).get("name") + "\n"
                        + " مشاهده در سایت \n "+getString(R.string.url)+"pr" + ad_id + "/app");
                // sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                // (onvantext));
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, ("اشتراک گذاری")));
            }
        });

        final DatabaseHandler db = new DatabaseHandler(Detailss.this);
        if (!db.isOpen())
            db.open();

        final ImageView imglike = (ImageView) findViewById(R.id.imglike);
        if (db.isLiked(ad_id)) {
            imglike.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart));
        } else {
            imglike.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_outline));
        }

        imglike.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    if (db.isLiked(ad_id)) {
                        doFav(false);
                        imglike.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_outline));
                        db.dolike(false, Integer.toString(ad_id), name, price, img);
                    } else {
                        imglike.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart));
                        db.dolike(true, Integer.toString(ad_id), name, price, img);
                        doFav(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        getMyData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (pDialog.isShowing()) {
                pDialog.cancel();
            }
        } catch (Exception e) {
        }
    }

    private void doFav(boolean b) {
        String uid=Func.getUid(this);
        if(!uid.equals("0")) {
            long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
            new com.persiandesigners.freeze.Util.Html(new OnTaskFinished() {
                @Override
                public void onFeedRetrieved(String body) {
                    Log.v("this", body);

                }
            }, false, Detailss.this, "").execute(getString(R.string.url) + "/doFav.php?uid="+uid+"&id="+ad_id+"&w="+b+"&n="+number);
        }
    }

    private void VideoPart(final String video) {
        ActionButton actionButton = (ActionButton) findViewById(R.id.video_button);
        actionButton.setVisibility(View.VISIBLE);
        actionButton.setButtonColor(Color.parseColor("#DA4336"));
        actionButton.setButtonColorPressed(Color.parseColor("#C34B40"));
        actionButton.playShowAnimation();
        actionButton.setShadowRadius(8);
        actionButton.setShadowXOffset(0);
        actionButton.setShadowYOffset(0);
        actionButton.setImageDrawable(ContextCompat.getDrawable(Detailss.this, R.mipmap.ic_video));

        actionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent in=new Intent (Detailss.this,VideoPlay.class);
//                in.putExtra("video",video);
//                startActivity(in);

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(video));
                startActivity(browserIntent);

//                final Dialog dialog = new Dialog(Detailss.this, R.style.DialogStyler);
//                dialog.setContentView(R.layout.video_dialog);
//
//                final VideoView videoView = (VideoView) dialog.findViewById(R.id.vide);
//                videoView.setZOrderOnTop(true);
//                Log.v("this", video);
//                final ProgressDialog progressDialog = new ProgressDialog(Detailss.this);
//                progressDialog.setMessage("Loading...");
//                progressDialog.setCancelable(false);
//                progressDialog.show();
//                MediaController mediaController = new MediaController(Detailss.this);
//                mediaController.setAnchorView(videoView);
//                videoView.setMediaController(mediaController);
//
//                try {
//                    videoView.setMediaController(mediaController);
//                    videoView.setVideoURI(Uri.parse(video));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.v("this", e.getMessage());
//                }
//
//                videoView.requestFocus();
//                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    public void onPrepared(MediaPlayer arg0) {
//                        progressDialog.dismiss();
//                        videoView.start();
//                        videoView.setZOrderOnTop(true);
//                    }
//                });
            }
        });
    }

    private void floatingActionBar() {
        //float ActionBar
//        ActionButton actionButton = (ActionButton) findViewById(R.id.action_button);
//        actionButton.setButtonColor(Color.parseColor("#DA4336"));
//        actionButton.setButtonColorPressed(Color.parseColor("#C34B40"));
//        actionButton.playShowAnimation();
//        actionButton.setShadowRadius(8);
//        actionButton.setShadowXOffset(0);
//        actionButton.setShadowYOffset(0);
//        actionButton.setImageDrawable(ContextCompat.getDrawable(Detailss.this, R.drawable.ic_comment));
//
//        actionButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent in = new Intent(Detailss.this, Comments.class);
//                in.putExtra("id", ad_id);
//                startActivity(in);
//            }
//        });
    }

}
