package com.persiandesigners.freeze;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.alirezaafkar.sundatepicker.DatePicker;
import com.alirezaafkar.sundatepicker.interfaces.DateSetListener;
import com.persiandesigners.freeze.Util.Alert;
import com.persiandesigners.freeze.Util.DatabaseHandler;
import com.persiandesigners.freeze.Util.HtmlPost;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by navid on 6/20/2016.
 */
public class Sabad_Takmil extends AppCompatActivity implements DateSetListener {
    Toolbar toolbar;
    Typeface typeface2;
    TextView takmil, jamKharid, post, jamFactor, tvinfo, takhfifha, taxTV;
    DatabaseHandler db;
    String jamKharids;
    Bundle bl;
    SharedPreferences settings;
    int  extra_peyk_price = 0, fori_marz_free = 0, zamandar_marz_free = 0, tax=0,
            taxvalue=0,postPrice,postPriceBack, JamFactor, bonTakhfif,
            priceFactor, offInRange, jamtakhfif;
    ProgressDialog pDialog;
    Spinner sendtime;
    EditText bon, tozihat;
    String HoursToday[], AllHours[];
    Integer extra_peyk_priceToday[], extra_peyk_priceAllHours[];
    ImageView ok;
    Boolean wentforpay, firsttime = true, outOfSaat = false;
    CardView lntime;
    Button tvdate;
    Boolean has_range_off, postPishtaz = false, is_kifpul = false, Equal = true;
    String today, miladiDate;

    int allowAfter = 0;
    RadioButton online, darmahal, kifpul, cartkhan;
    RadioButton fori, zamandar;
    CheckBox sendsimilar;
    int working_hour_from, working_hour_to;
    ViewGroup lnGroup;
    String adrs="";
    Boolean isBazaryab=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sabad_takmil);

        bl = getIntent().getExtras();
        declare();
        actionbar();

        calFactor();
        long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        String query = "";
        if (postPishtaz) {
            Cursor cursor = db.getProdctsSabad();
            String prds = "";
            Uri.Builder builder = new Uri.Builder();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                prds += cursor.getInt(0) + "#";
                builder.appendQueryParameter("params" + cursor.getInt(0), cursor.getString(2));
                builder.appendQueryParameter("countProd" + cursor.getInt(0), cursor.getInt(1) + "");
            }
            prds = prds.substring(0, prds.length() - 1);
            builder.appendQueryParameter("products", prds);
            query = builder.build().getEncodedQuery();
        }
		String more="";
        if(getResources().getBoolean(R.bool.multiseller)){
            more="&shopId="+Func.getShopId(Sabad_Takmil.this);
        }
        new HtmlPost(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
				 int is_online=1, is_darmahal=1;
                Log.v("this", body);
                if (body.equals("errordade")) {
                    MyToast.makeText(getApplicationContext(), "اشکالی پیش آمده است");
                } else {
                    try {
                        JSONObject response = new JSONObject(body);
                        JSONArray posts = response.optJSONArray("contacts");
                        if (posts != null) {
                            JSONObject post = posts.optJSONObject(0);
							is_online=post.optInt("enable_online_payment");
                            is_darmahal=post.optInt("enable_dar_mahal_payment");
                            postPriceBack=postPrice = post.optInt("postprice");
                            offInRange = post.optInt("off");
							
//                            tvdate.setText(post.optString("today"));
                            today = post.optString("today");
                            allowAfter = post.optInt("allowafter");
                            working_hour_from = post.optInt("working_hour_from");
                            working_hour_to = post.optInt("working_hour_to");

                            zamandar_marz_free= post.optInt("zamandar_marz_free");
                            fori_marz_free= post.optInt("fori_marz_free");

                            ///zaman ersal bar asas sa at kari
//                            ArrayList<String> workingHours=new ArrayList<String>();
//                            workingHours.add(   "زمان ارسال را مشخص کنید");
//                            int frm=working_hour_from;
//                            while(frm<working_hour_to){
//                                int to=frm+1;
//                                workingHours.add(frm+"-"+to);
//                                frm++;
//                            }
//                            sendtime.setAdapter(new ArrayAdapter<String>(Sabad_Takmil.this, R.layout.spinner_item, workingHours));
                            ///////////end zaman ersal bar asas sa at kari

                            tax=post.optInt("tax");
                            if(tax>0){
                                CardView lntax=(CardView)findViewById(R.id.lntax);
                                lntax.setVisibility(View.VISIBLE);
                            }
                            if (getResources().getBoolean(R.bool.has_saat_kari)) {
                                if (post.optInt("outOfSaat") == 1) {
                                    outOfSaat = true;
                                    alertBede("فروشگاه خارج از ساعت کاری می باشد، شما می توانید ارسال زماندار را انتخاب کنید.");
                                } else
                                    outOfSaat = false;
                            } else {
                                outOfSaat = false;
                            }

                            if (outOfSaat) {
                                fori.setEnabled(false);
                                zamandar.setChecked(true);
                                lntime.setVisibility(View.VISIBLE);
                            }
                            if(post.optInt("enable_dar_mahal_pos_payment")==1){
                                cartkhan.setVisibility(View.VISIBLE);
                            }
                            if(post.optInt("enable_ersale_fori")==0)
                                fori.setVisibility(View.GONE);
                            if(post.optInt("enable_ersale_zamandar")==0)
                                zamandar.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.v("this", e.getMessage());
                    }
					
					if(is_darmahal==1){
                        darmahal.setVisibility(View.VISIBLE);
                    }else {
                        darmahal.setVisibility(View.GONE);
                        darmahal.setChecked(false);
                    }

                    if(is_online==1){
                        online.setVisibility(View.VISIBLE);
                    }else {
                        online.setVisibility(View.GONE);
                        online.setChecked(false);
                    }

					
//                    sendtime.setSelection(allowAfter);

//                    sendtime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            if (allowAfter > position && Equal && firsttime == false) {
//                                sendtime.setSelection(allowAfter);
//                                MyToast.makeText(Sabad_Takmil.this, "زمان انتخابی نامعتبر است");
//                            }
//                            firsttime = false;
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//
//                        }
//                    });
                    ///sat haye kari
                    try {
                        JSONObject jsonHour = new JSONObject(body);
                        JSONArray jsonHours = jsonHour.getJSONArray("saatha");
                        if (jsonHours != null && jsonHours.length() > 0) {
                            HoursToday = new String[jsonHours.length()];
                            extra_peyk_priceToday = new Integer[jsonHours.length()];
                            for (int i = 0; i < jsonHours.length(); i++) {
                                JSONObject post = jsonHours.optJSONObject(i);
                                HoursToday[i] = post.optString("hrs");
                                extra_peyk_priceToday[i] = post.optInt("extra_peyk_price");
                            }
                        }
                        JSONArray allHours = jsonHour.optJSONArray("allSaatha");
                        if (allHours != null && allHours.length() > 0) {
                            AllHours = new String[allHours.length()];
                            extra_peyk_priceAllHours = new Integer[allHours.length()];
                            for (int i = 0; i < allHours.length(); i++) {
                                JSONObject post = allHours.optJSONObject(i);
                                AllHours[i] = post.optString("hrs");
                                extra_peyk_priceAllHours[i] = post.optInt("extra_peyk_price");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(AllHours==null && HoursToday==null){
                        lntime.setVisibility(View.GONE);
                        fori.setChecked(true);
                    }else
                        makeTodayHours();
   
                    if (offInRange > 0 && has_range_off) {
                        AlertDialog.Builder a = new AlertDialog.Builder(Sabad_Takmil.this);
                        a.setMessage("مبلغ سفارش شما در رنج تخفیف قرار گرفت . از مبلغ فاکتور شما " +
                                offInRange + " تومان کسر گردید ");
                        a.setPositiveButton(("باشه"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog dialog = a.show();
                        TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                        messageText.setGravity(Gravity.RIGHT);
                        messageText.setTypeface(typeface2);

                        Button btn1 = (Button) dialog.findViewById(android.R.id.button1);
                        btn1.setTypeface(typeface2);

                        Button btn2 = (Button) dialog.findViewById(android.R.id.button2);
                        btn2.setTypeface(typeface2);
                    }else
                        offInRange=0;

                    calFactor();
                }
            }
        }, true, Sabad_Takmil.this, "", query).execute(getString(R.string.url) + "/getPostPrice.php?jam="
                + priceFactor + "&n=" + number + "&mahale=" + bl.getString("shahrstanId") + "&postPishtaz=" + postPishtaz+"&ostan="+bl.getString("ostanId")+more);

        takmil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_kifpul && Func.getUid(Sabad_Takmil.this).equals("0") && kifpul.isChecked()) {
                    MyToast.makeText(Sabad_Takmil.this, "تنها کاربران عضو میتوانند از کیف پول خرید کنند");
                }else if(online.isChecked()==false && darmahal.isChecked()==false
                        && kifpul.isChecked()==false && cartkhan.isChecked()==false)
                    MyToast.makeText(Sabad_Takmil.this,"روش پرداخت را انتخاب کنید");
                else if (tvdate.getText().toString().contains("انتخاب") && zamandar.isChecked()) {
                    MyToast.makeText(Sabad_Takmil.this, "تاریخ ارسال را مشخص کنید");
                } else if (sendtime.getSelectedItemPosition() == 0 && zamandar.isChecked() && sendtime.getSelectedItem().toString().contains("زمان")) {
                    MyToast.makeText(Sabad_Takmil.this, "زمان ارسال را مشخص کنید");
                } else if (outOfSaat && getResources().getBoolean(R.bool.has_saat_kari)) {
                    alertBede("فروشگاه خارج از ساعت کاری می باشد.");
                } else if (getResources().getBoolean(R.bool.only_users_can_buy)
                        && Func.getUid(Sabad_Takmil.this).equals("0")) {

                    LinearLayout ln = (LinearLayout) findViewById(R.id.ln);
                    Snackbar.make(ln, "جهت تکمیل سفارش، ابتدا وارد شوید", Snackbar.LENGTH_LONG)
                            .setAction("ورود|عضویت", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(Sabad_Takmil.this, Login.class));
                                }
                            }).show();
                } else
                    new submitForm().execute();
            }
        });

        if(settings.getString("vahed", "").length()>0)
            adrs+=" واحد " + settings.getString("vahed", "");
        if(settings.getString("tabaghe", "").length()>0)
            adrs+=" طبقه " + settings.getString("tabaghe", "");
        if(settings.getString("pelak", "").length()>0)
            adrs+=" پلاک " + settings.getString("pelak", "");

        tvinfo.setText(android.text.Html.fromHtml("آدرس : "
                + "<b>" + bl.getString("ostan") + settings.getString("adres", "")+adrs + "</b>  خریدار : " + "<b>" + settings.getString("name_s", "")
        ));
    }

    private void alertBede(String text) {
        final Alert mAlert = new Alert(Sabad_Takmil.this, R.style.mydialog);
        mAlert.setCancelable(false);
        mAlert.setIcon(android.R.drawable.ic_dialog_alert);
        mAlert.setMessage(text);
        mAlert.setPositveButton("باشه", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlert.dismiss();
            }
        });
        mAlert.show();
    }

    private void calFactor() {
        int sumPostPrice = postPrice + extra_peyk_price;
        post.setText(Func.getCurrency(sumPostPrice + "") + getString(R.string.toman));
        int takhfifKalaha = db.getJamTakhfifha();
        priceFactor = JamFactor - takhfifKalaha;
		 if(tax>0) {
            taxvalue = (priceFactor + -bonTakhfif - offInRange) * tax / 100;
            taxTV.setText(Func.getCurrency(taxvalue+"")+ " تومان");
        }
        jamKharids = (priceFactor + sumPostPrice - bonTakhfif - offInRange+taxvalue) + "";
        jamFactor.setText(Func.getCurrency(jamKharids) + getString(R.string.toman));
        jamtakhfif = bonTakhfif + takhfifKalaha + offInRange;
        takhfifha.setText(Func.getCurrency((bonTakhfif + takhfifKalaha + offInRange) + "") + " تومان");
    }

    private void declare() {
        isBazaryab = Func.getIsBazaryab(this);
        lnGroup=(ViewGroup)findViewById(R.id.ln);
        has_range_off = getResources().getBoolean(R.bool.has_range_off);
        postPishtaz = getResources().getBoolean(R.bool.post_pishtaz);
        is_kifpul = getResources().getBoolean(R.bool.isActiveKif);

        if (getResources().getBoolean(R.bool.has_choose_zaman_daryaft_sefaresh) == false) {
            LinearLayout lnmore = (LinearLayout) findViewById(R.id.lnmore);
            lnmore.setVisibility(View.GONE);
        }

        wentforpay = false;
        settings = getSharedPreferences("settings", MODE_PRIVATE);
        typeface2 = Typeface.createFromAsset(getAssets(), "IRAN Sans.ttf");

        tozihat=(EditText)findViewById(R.id.tozihat);
        tozihat.setTypeface(typeface2);

        db = new DatabaseHandler(this);
        if (!db.isOpen())
            db.open();

        bonTakhfif = 0;

        bon = (EditText) findViewById(R.id.bon);
        bon.setTypeface(typeface2);

        ok = (ImageView) findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBon();
            }
        });

        sendtime = (Spinner) findViewById(R.id.sendtime);

        postPrice = 0;
        JamFactor = 0;

        jamKharid = (TextView) findViewById(R.id.jamkharid);
        jamKharid.setTypeface(typeface2);

        jamKharids = db.getSabadMablaghKol();
        jamKharid.setText(Func.getCurrency(jamKharids) + getString(R.string.toman));
        JamFactor = Integer.parseInt(jamKharids);


        takmil = (TextView) findViewById(R.id.takmil);
        takmil.setTypeface(typeface2);
        post = (TextView) findViewById(R.id.post);
        post.setTypeface(typeface2);
        //post.setText(bl.getString("shahrstanPrice") + getString(R.string.toman));
        post.setText("0" + getString(R.string.toman));
        jamFactor = (TextView) findViewById(R.id.jamkol);
        jamFactor.setTypeface(typeface2);
        takhfifha = (TextView) findViewById(R.id.takhfifha);
        takhfifha.setTypeface(typeface2);
        sendsimilar = (CheckBox) findViewById(R.id.sendsimilar);
        sendsimilar.setTypeface(typeface2);

        tvinfo = (TextView) findViewById(R.id.tvinfo);
        tvinfo.setTypeface(typeface2);
        taxTV = (TextView) findViewById(R.id.tax);
        taxTV.setTypeface(typeface2);
        jamKharids = (Integer.parseInt(jamKharids) + Integer.parseInt(bl.getString("shahrstanPrice"))) + "";
        jamFactor.setText(Func.getCurrency(jamKharids) + getString(R.string.toman));

        TextView tvjamkharid = (TextView) findViewById(R.id.tvjamkharid);
        tvjamkharid.setTypeface(typeface2);
        TextView tvtakhfifha = (TextView) findViewById(R.id.tvtakhfifha);
        tvtakhfifha.setTypeface(typeface2);
        TextView tvs2 = (TextView) findViewById(R.id.tvs2);
        tvs2.setTypeface(typeface2);
        TextView tvs4 = (TextView) findViewById(R.id.tvs4);
        tvs4.setTypeface(typeface2);
        TextView tvs1 = (TextView) findViewById(R.id.tvs1);
        tvs1.setTypeface(typeface2);
        TextView tvs5 = (TextView) findViewById(R.id.tvs5);
        tvs5.setTypeface(typeface2);

        TextView tvpost = (TextView) findViewById(R.id.tvpost);
        tvpost.setTypeface(typeface2);
		TextView tvtax = (TextView) findViewById(R.id.tvtax);
        tvtax.setTypeface(typeface2);
        TextView tvjamkol = (TextView) findViewById(R.id.tvjamkol);
        tvjamkol.setTypeface(typeface2);
        TextView tvbon = (TextView) findViewById(R.id.tvbon);
        tvbon.setTypeface(typeface2);
        tvdate = (Button) findViewById(R.id.tvdate);
        tvdate.setTypeface(typeface2);
        tvdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker.Builder builder = new DatePicker
                        .Builder()
                        .future(true);
                //.showYearFirst(true)
                //.closeYearAutomatically(true)
                //.minYear(1393)
                builder.build(Sabad_Takmil.this)
                        .show(getSupportFragmentManager(), "");
            }
        });

        //        online,darmahal
        online = (RadioButton) findViewById(R.id.online);
        online.setTypeface(typeface2);
        darmahal = (RadioButton) findViewById(R.id.darmahal);
        darmahal.setTypeface(typeface2);
        kifpul = (RadioButton) findViewById(R.id.kifpul);
        kifpul.setTypeface(typeface2);
        if(is_kifpul){
            kifpul.setVisibility(View.VISIBLE);
        }
        cartkhan = (RadioButton) findViewById(R.id.cartkhan);
        cartkhan.setTypeface(typeface2);

        fori = (RadioButton) findViewById(R.id.fori);
        fori.setTypeface(typeface2);
        zamandar = (RadioButton) findViewById(R.id.zamandar);
        zamandar.setTypeface(typeface2);

        lntime = (CardView) lnGroup.findViewById(R.id.lntime);
        fori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fori.isChecked()) {
                    TransitionManager.beginDelayedTransition(lnGroup);
                    lntime.setVisibility(View.GONE);

                    if (priceFactor >= fori_marz_free) {
                        postPrice = 0;
                    } else
                        postPrice = postPriceBack;

                    calFactor();
                }
            }
        });
        zamandar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zamandar.isChecked()) {
                    TransitionManager.beginDelayedTransition(lnGroup);
                    lntime.setVisibility(View.VISIBLE);

                    if (priceFactor >= zamandar_marz_free) {
                        postPrice = 0;
                    } else {
                        postPrice = postPriceBack;
                    }
                    calFactor();
                }
            }
        });
    }

    private void checkBon() {
        long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("submit", "true")
                .appendQueryParameter("app", "true")
                .appendQueryParameter("bonCode", bon.getText().toString())
                .appendQueryParameter("tatolPriceFactor",  db.getSabadMablaghKolWithTakhfif() + "");
        String query = builder.build().getEncodedQuery();

        new HtmlPost(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                if (body.equals("errordade")) {
                    MyToast.makeText(getApplicationContext(), "اشکالی پیش آمده است");
                } else if (body.contains("اشتباه")) {
                    ok.setImageDrawable(ContextCompat.getDrawable(Sabad_Takmil.this, R.drawable.removeiconred));
                    ok.setVisibility(View.VISIBLE);
                } else {
                    AlertDialog.Builder a = new AlertDialog.Builder(Sabad_Takmil.this);
                    if (body.contains("##")) {
                        bonTakhfif = Integer.parseInt(body.substring(body.indexOf("##") + 2));
                        a.setMessage(android.text.Html.fromHtml(body.substring(0, body.indexOf("##"))));
                        ok.setImageDrawable(ContextCompat.getDrawable(Sabad_Takmil.this, R.drawable.icon_ok));
                        ok.setVisibility(View.VISIBLE);
                        bon.setEnabled(false);
                    } else {
                        bonTakhfif = 0;
                        a.setMessage(android.text.Html.fromHtml(body));
                        ok.setImageDrawable(ContextCompat.getDrawable(Sabad_Takmil.this, R.drawable.removeiconred));
                        ok.setVisibility(View.VISIBLE);
                    }
                    a.setPositiveButton(("باشه"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            calFactor();

                            ok.setImageDrawable(ContextCompat.getDrawable(Sabad_Takmil.this, R.drawable.icon_ok));
                            ok.setVisibility(View.VISIBLE);
                            bon.setEnabled(false);
                        }
                    });

                    AlertDialog dialog = a.show();
                    TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                    messageText.setGravity(Gravity.RIGHT);
                    messageText.setTypeface(typeface2);
                }
            }
        }, false, Sabad_Takmil.this, "", query).execute(getString(R.string.url) + "/bonCode.php?n=" + number);
    }

    private class submitForm extends AsyncTask<String, String, String> {
        Boolean goterr = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Sabad_Takmil.this);
            pDialog.setMessage(getString(R.string.sendingData));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... params) {
            String responseBody = "";
            HttpURLConnection conn = null;
            try {
                URL url = new URL(getString(R.string.url) + "/postRequest.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(20000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
            } catch (IOException e) {
                e.printStackTrace();

            }
            Cursor cursor = db.getProdctsSabad();
            String prds = "";
            String paymetnId = "1";
            if (online.isChecked())
                paymetnId = "2";
            if (kifpul.isChecked())
                paymetnId = "3";
            if (cartkhan.isChecked())
                paymetnId = "5";

            String nahveersal = "زمان ارسال : ارسال فوری";
            if (zamandar.isChecked()) {
                nahveersal =
                        "زمان ارسال : ارسال زمان دار \n" +
                                " تاریخ ارسال : " + tvdate.getText().toString() + " \n" +
                                "زمان ارسال : " + sendtime.getSelectedItem().toString();

                nahveersal = "ارسال زمان دار";
            }

            String msg = "";
            if (sendsimilar.isChecked()) {
                msg = "\n\n <br />" + sendsimilar.getText().toString();
            }
           // msg = nahveersal + msg;
		    msg =  msg;
            String postinTime = "";
//            try {
//                postinTime = sendtime.getSelectedItem().toString();
//                postinTime = postinTime.substring(0, postinTime.indexOf("-"));
//                postinTime = postinTime.replace("-", "");
//            } catch (Exception e) {
//                postinTime = "";
//            }
            if(sendtime!=null && zamandar.isChecked())
                postinTime = sendtime.getSelectedItem().toString();

            String deliveryID;
            if (fori.isChecked())
                deliveryID = "1";
            else
                deliveryID = "2";

            String userId=settings.getString("uid", "0");
            if(isBazaryab && !Func.getUidFromBazaryab(Sabad_Takmil.this).equals("0")){
                userId=Func.getUidFromBazaryab(Sabad_Takmil.this);
            }

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("name", settings.getString("name_s", ""))
                    .appendQueryParameter("tel", settings.getString("tel", ""))
                    .appendQueryParameter("tarikhe_zamandar_dates", miladiDate)
                    .appendQueryParameter("mobile", settings.getString("mobile_s", ""))
                    .appendQueryParameter("address", bl.getString("ostan") + settings.getString("adres", "")+ adrs)
                    .appendQueryParameter("postalCode", settings.getString("codeposti", ""))
                    .appendQueryParameter("message", tozihat.getText().toString() + "\n " + msg)
                    .appendQueryParameter("mahaleId", bl.getString("shahrstanId"))
                    .appendQueryParameter("shahrestan", bl.getString("shahrstanId"))
                    .appendQueryParameter("ostan", bl.getString("ostanId"))
                    .appendQueryParameter("postingTime", miladiDate)
                    .appendQueryParameter("postingHours", postinTime)
//                        .appendQueryParameter("shahrstanPrice", bl.getString("shahrstanPrice"))
                    .appendQueryParameter("shahrstanPrice", (postPrice + extra_peyk_price) + "")
                    .appendQueryParameter("jam", jamKharids)
                    .appendQueryParameter("app", "yes")
                    .appendQueryParameter("bonCode", bon.getText().toString())
                    .appendQueryParameter("paymentID", paymetnId)//1->dar mahal
                    .appendQueryParameter("uid", userId)
                    .appendQueryParameter("totalOff", jamtakhfif + "")
                    .appendQueryParameter("offInRange", offInRange + "")
                    .appendQueryParameter("deliveryID", deliveryID)
                    .appendQueryParameter("has_range_off", has_range_off + "");
            ;
            if (getResources().getBoolean(R.bool.choose_gps_location_on_map)) {
                builder.appendQueryParameter("lat", bl.getString("lat") + "");
				builder.appendQueryParameter("lon", bl.getString("lon") + "");
            }

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                prds += cursor.getInt(0) + "#";
                //builder.appendQueryParameter("params" + cursor.getInt(0), cursor.getString(2));
                builder.appendQueryParameter("productProperties" , cursor.getString(2));
                builder.appendQueryParameter("countProd" + cursor.getInt(0), cursor.getInt(1) + "");
                builder.appendQueryParameter("price"+ cursor.getInt(0) , cursor.getString(3));
            }
            prds = prds.substring(0, prds.length() - 1);
            builder.appendQueryParameter("products", prds);

            try {
                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        responseBody += line;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return responseBody;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            Log.v("this", result);
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();

            if (goterr == false && !isCancelled()) {
                if (result.contains("ok")) {//agahiiejad shod
                    wentforpay = true;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.url) + "payOnlineApp.php?id=" + result.replace("ok#", "")+"&app=true"));
                    startActivity(browserIntent);
				}else if(result.contains("majazi")) {
					MyToast.makeText(Sabad_Takmil.this, "کالهای مجازی تنها از طریق درگاه پرداخت آنلاین قابل سفارش میباشد");
                }else if(result.contains("notWorkingHour")) {
                    MyToast.makeText(Sabad_Takmil.this,"فروشگاه خارج از ساعت کاری میباشد");
                }else if(result.contains("notWorkingHour")) {
                    MyToast.makeText(Sabad_Takmil.this, "فروشگاه خارج از ساعت کاری میباشد");
                } else if (result.contains("ma")) {
                    final Alert mAlert = new Alert(Sabad_Takmil.this, R.style.mydialog);
                    mAlert.setCancelable(false);
                    mAlert.setIcon(android.R.drawable.ic_dialog_alert);
                    mAlert.setMessage((" خرید شما با موفقیت انجام شد . کد رهگیری سفارش شما "
                            + result.replace("ma", "") + " میباشد " +
                            "\n" +
                            "با تشکر از خرید شما "));
                    mAlert.setPositveButton("بازگشت به صفحه اصلی", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAlert.dismiss();
                            db.clearSabadKharid();
                            startActivity(new Intent(Sabad_Takmil.this, Home.class));
                            finish();
                        }
                    });
                    mAlert.setNegativeButton("نمایش فاکتور", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAlert.dismiss();
                            final Dialog dialog_fac = new Dialog(Sabad_Takmil.this, R.style.DialogStyler);
                            dialog_fac.setContentView(R.layout.page2);
                            dialog_fac.setTitle("وضعیت سفارش شما");
                            dialog_fac.setCancelable(false);

                            final ProgressBar progress = (ProgressBar) dialog_fac.findViewById(R.id.progress);

                            Button text = (Button) dialog_fac.findViewById(R.id.ok);
                            text.setVisibility(View.VISIBLE);
                            text.setText("بازگشت");
                            text.setTypeface(typeface2);
                            text.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog_fac.dismiss();
                                    db.clearSabadKharid();
                                    startActivity(new Intent(Sabad_Takmil.this, Home.class));
                                }
                            });
                            WebView wb = (WebView) dialog_fac.findViewById(R.id.webView1);
                            WebSettings settingswb = wb.getSettings();
                            text.bringToFront();
                            settingswb.setDefaultTextEncodingName("utf-8");
                            settingswb.setCacheMode(WebSettings.LOAD_NO_CACHE);
                            settingswb.setSaveFormData(false);
                            settingswb.setSupportZoom(true);
                            settingswb.setDisplayZoomControls(false);
                            settingswb.setBuiltInZoomControls(true);

                            String code = result.replace("ma", "");
                            wb.loadUrl(getString(R.string.url) + "admin/printData.php?id=" + code + "&codRah=" + code + "&fromApp=true");
                            Log.v("this", getString(R.string.url) + "admin/printData.php?id=" + code + "&codRah=" + code + "&fromApp=true");
                            wb.setWebViewClient(new WebViewClient() {
                                @Override
                                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                                    handler.proceed();
                                }

                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    view.loadUrl(url);
                                    return true;
                                }

                                public void onPageFinished(WebView view, String url) {
                                    try {
                                        progress.setVisibility(View.GONE);
                                    } catch (final IllegalArgumentException e) {

                                    } catch (final Exception e) {
                                    } finally {

                                    }
                                }
                            });

                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(dialog_fac.getWindow().getAttributes());
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                            dialog_fac.show();
                            dialog_fac.getWindow().setAttributes(lp);
                        }
                    });
                    mAlert.show();

                } else if (result.contains("nE")) {
                    final Alert mAlert = new Alert(Sabad_Takmil.this, R.style.mydialog);
                    mAlert.setCancelable(false);
                    mAlert.setIcon(android.R.drawable.ic_dialog_alert);
                    mAlert.setMessage("موجودی کیف پول کافی نیست\n"
                            +"\n"
                            +" موجودی کیف پول شما " + Func.getCurrency(result.replace("nٍ","").replace("nE",""))+" تومان"
                    );
                    mAlert.setPositveButton("افزایش موجودی کیف پول", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAlert.dismiss();
                            Func.KifPul(Sabad_Takmil.this);
                        }
                    });
                    mAlert.setNegativeButton("بعدا", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAlert.dismiss();
                        }
                    });
                    mAlert.show();
                } else if (result.contains("@@")) {
                    AlertDialog.Builder a = new AlertDialog.Builder(Sabad_Takmil.this);
                    a.setMessage(result.replace("@@", ""));
                    a.setPositiveButton(("باشه"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
//                    a.setCancelable(false);
                    a.show();
                }
            } else if (goterr == true)
                MyToast.makeText(Sabad_Takmil.this, getString(R.string.problemload));

        }
    }


    private void actionbar() {
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        action.MakeActionBar(getString(R.string.sabadkhrid));
        action.hideSabadKharidIcon();
        ;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (wentforpay) {
//                db.clearSabadKharid();
                startActivity(new Intent(Sabad_Takmil.this, Home.class));
                finish();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onDateSet(int id, @Nullable Calendar calendar, int day, int month, int year) {
        Log.v("this", year + "-" + month + "-" + day);//shamsi

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String chdate = format1.format(calendar.getTime());
        try {
            Date todays = new Date();
            todays.setHours(0);
            todays.setMinutes(0);
            todays.setSeconds(0);
            todays.setMinutes(0);
            Date choosedate = format1.parse(chdate);
            miladiDate = format1.format(calendar.getTime());

            if (choosedate.after(todays) || choosedate.equals(todays)
                    || choosedate.toString().equals(todays.toString())) {//date before today ot equal today
                Log.v("this", "after today or today");

                tvdate.setText(year + "-" + month + "-" + day);
            } else {//date after today
                MyToast.makeText(Sabad_Takmil.this, "تاریخ سفارش نامعتبر است");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.v("this", e.getMessage());
        }

        String Now = year + "-" + month + "-" + day;
        if (Now.equals(today)) {
            Equal = true;
            makeTodayHours();
        } else {
            Equal = false;
            makeAllHours();
        }

        Log.v("this", "MiladiDateChoosen " + format1.format(calendar.getTime()));
        miladiDate = format1.format(calendar.getTime());
    }

    private void makeTodayHours() {
        if (HoursToday != null && HoursToday.length > 0) {
            sendtime.setAdapter(new ArrayAdapter<String>(Sabad_Takmil.this, R.layout.spinner_item, HoursToday));
            if (extra_peyk_priceToday != null) {
                sendtime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        extra_peyk_price = extra_peyk_priceToday[i];
                        calFactor();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        }
    }

    private void makeAllHours() {
        if (AllHours != null && AllHours.length > 0) {
            sendtime.setAdapter(new ArrayAdapter<String>(Sabad_Takmil.this, R.layout.spinner_item, AllHours));
            if (extra_peyk_priceAllHours != null) {
                sendtime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        extra_peyk_price = extra_peyk_priceAllHours[i];
                        calFactor();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        }
    }

}
