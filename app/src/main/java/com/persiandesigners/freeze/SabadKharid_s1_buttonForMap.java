package com.persiandesigners.freeze;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.persiandesigners.freeze.Util.CustomProgressDialog;
import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.HtmlPost;
import com.persiandesigners.freeze.Util.Lv_adapter;
import com.persiandesigners.freeze.Util.MyLocationListener;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class SabadKharid_s1_buttonForMap extends AppCompatActivity {
    Toolbar toolbar;
    Typeface typeface2;
    TextView takmil, last_gps;
    EditText name, mobile, tel, adress, tozihat, codeposti;
    Spinner mahale;
    TextInputLayout mobile_l, name_l, tel_l, adres_l, codeposti_l, mahale_l;
    CustomProgressDialog mCustomProgressDialog;
    ArrayList<String> mahaleNames;
    ArrayList<Class_Mahale> mahales;

    Double lat = 0.0, lon = 0.0;
    Intent locatorService = null;
    Button gps;
    RadioGroup rd_group;
    Button makan;
    Boolean OstanShahrestanEnable = false, has_mahale;
    String ostanIDs = "0", shahrestanIDs = "0";
    RadioButton rd_Adres, rd_gps;
    Boolean locationOnMap = false;
    Bundle bl;
    String forWhat = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sabadkharids1);

        declare();
        actionbar();
        takmil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

    }

    private void submit() {
        name_l.setErrorEnabled(false);
        mobile_l.setErrorEnabled(false);
        tel_l.setErrorEnabled(false);
        adres_l.setErrorEnabled(false);
        codeposti_l.setErrorEnabled(false);
        mahale_l.setErrorEnabled(false);
        if (name.getText().length() < 3) {
            name_l.setErrorEnabled(true);
            name_l.setError(getString(R.string.wrong_name));
            name.requestFocus();
        } else if (mobile.getText().length() != 11) {
            mobile_l.setErrorEnabled(true);
            mobile_l.setError(getString(R.string.wrong_mobile));
            mobile.requestFocus();
//        } else if (tel.getText().length() < 6) {
//            tel_l.setErrorEnabled(true);
//            tel_l.setError(getString(R.string.wrong_tel));
//            tel.requestFocus();

        } else if (rd_group.getVisibility() == View.GONE && adress.getText().toString().length() < 10) {
            adres_l.setErrorEnabled(true);
            adres_l.setError("آدرس صحیح وارد کنید");
            adress.requestFocus();
        } else if (OstanShahrestanEnable == false && mahale.getSelectedItemPosition() == 0 && has_mahale) {
            MyToast.makeText(SabadKharid_s1_buttonForMap.this, getString(R.string.wrong_mahale));
        } else if (getResources().getBoolean(R.bool.sabad_ostan_shahrestan)
                && makan.getText().toString().contains("انتخاب")) {
            MyToast.makeText(SabadKharid_s1_buttonForMap.this, "استان و شهرستان را انتخاب کنید");
        } else if (rd_Adres.isChecked() && adress.getText().length() < 10) {
            adres_l.setErrorEnabled(true);
            adres_l.setError("آدرس صحیح وارد کنید");
            adress.requestFocus();
        } else if (rd_gps.isChecked() && gps.getText().toString().contains("دست")) {
            MyToast.makeText(SabadKharid_s1_buttonForMap.this, "موقعیت جغرافیایی را مشخص کنید");
        } else if (getResources().getBoolean(R.bool.getCustomerGpsCoord) == true &&
                rd_Adres.isChecked() == false && rd_gps.isChecked() == false) {
            MyToast.makeText(SabadKharid_s1_buttonForMap.this, "نحوه مشخص کردن موقعیت کاربر را انتخاب کنید");
        /*} else if (codeposti.getText().length() != 10) {
            codeposti_l.setErrorEnabled(true);
            codeposti_l.setError(getString(R.string.wrong_codeposti));
            codeposti.requestFocus();
        */
        } else {
            if (forWhat.equals("new") || forWhat.equals("edit")) {
                //getAddAdress.php
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("name", name.getText().toString())
                        .appendQueryParameter("uid", Func.getUid(SabadKharid_s1_buttonForMap.this))
                        .appendQueryParameter("tel", mobile.getText().toString())
                        .appendQueryParameter("adres", adress.getText().toString())
                        .appendQueryParameter("codeposti", codeposti.getText().toString())
                        ;
                if(mahale!=null)
                    builder.appendQueryParameter("mahale",mahales.get(mahale.getSelectedItemPosition()).getId());

                if(last_gps!=null && last_gps.getText()!=null && last_gps.getText().toString().contains("شده")){
                    builder.appendQueryParameter("lat",lat+"");
                    builder.appendQueryParameter("lon",lon+"");
                }else{
                    builder.appendQueryParameter("lat","0");
                    builder.appendQueryParameter("lon","0");
                }
                if(forWhat.equals("edit"))
                    builder.appendQueryParameter("id",bl.getString("id"));
                else
                    builder.appendQueryParameter("id","0");

                String query = builder.build().getEncodedQuery();
                new HtmlPost(new OnTaskFinished() {
                    @Override
                    public void onFeedRetrieved(String body) {
                        Log.v("this", body);
                        if (body.equals("errordade")) {

                        } else if (body.equals("ok")) {

                        } else if (body.equals("err"))
                            MyToast.makeText(SabadKharid_s1_buttonForMap.this, getString(R.string.problem));
                    }
                }, false, SabadKharid_s1_buttonForMap.this, "", query).execute(getString(R.string.url) + "/getAddAdress.php");
            }
            SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
            SharedPreferences.Editor pref = settings.edit();
            pref.putString("name_s", name.getText().toString());
            pref.putString("tel", tel.getText().toString());
            pref.putString("mobile_s", mobile.getText().toString());
            pref.putString("adres", adress.getText().toString());
            pref.putString("codeposti", codeposti.getText().toString());


            if (mahales != null && OstanShahrestanEnable == false) {
                Intent in = null;
                if (bl != null && bl.getString("for")!=null &&( bl.getString("for").equals("edit") || bl.getString("for").equals("new")))
                    in = new Intent(this, SabadAddress.class);
                else
                    in = new Intent(this, Sabad_Takmil.class);
                if (getResources().getBoolean(R.bool.getCustomerGpsCoord)) {
                    in.putExtra("lat", lat);
                    in.putExtra("lon", lon);
                }
                in.putExtra("shahrstanId", mahales.get(mahale.getSelectedItemPosition()).getId());
                in.putExtra("shahrstanPrice", mahales.get(mahale.getSelectedItemPosition()).getprice());
                pref.putString("mahale_name", mahales.get(mahale.getSelectedItemPosition()).getName());

                in.putExtra("msg", tozihat.getText().toString());
                in.putExtra("ostan", "");
                in.putExtra("ostanId", "");
                if(bl.getString("from")!=null)
                    in.putExtra("from",bl.getString("from"));
                startActivity(in);
                if (bl != null && bl.getString("for").equals("edit") || bl.getString("for").equals("new"))
                    finish();
            } else if (OstanShahrestanEnable || has_mahale == false) {//ostanShahrstan
                Intent in = new Intent(this, Sabad_Takmil.class);
                if (getResources().getBoolean(R.bool.getCustomerGpsCoord)) {
                    in.putExtra("lat", lat);
                    in.putExtra("lon", lon);
                }
                in.putExtra("shahrstanPrice", "0");
                in.putExtra("shahrstanId", shahrestanIDs);
                in.putExtra("ostanId", ostanIDs);
                if (makan != null)
                    in.putExtra("ostan", makan.getText().toString() + " -");
                else
                    in.putExtra("ostan", "");
                in.putExtra("msg", tozihat.getText().toString());
                startActivity(in);
            }
            pref.commit();
        }
    }

    private class getMahale extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
        Boolean goterr = false;

        protected ArrayList<HashMap<String, String>> doInBackground(Void... arg0) {
            String jsonStr = "";
            HttpURLConnection urlConnection;
            try {
                URL url = new URL(getString(R.string.url) + "/getMahaleha.php");
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

            if (jsonStr != null) {
                try {
                    JSONObject json = new JSONObject(jsonStr);
                    JSONArray jsonArray = new JSONArray(json.optString("contacts"));
                    mahaleNames = new ArrayList<String>();
                    mahales = new ArrayList<Class_Mahale>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Class_Mahale mahale = new Class_Mahale();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        mahale.setName(jsonObject.optString("name"));
                        mahale.setId(jsonObject.optString("id"));
                        mahale.setprice(jsonObject.optString("hazine"));
                        mahaleNames.add(jsonObject.optString("name"));
                        mahales.add(mahale);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                goterr = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
            super.onPostExecute(result);
            //mCustomProgressDialog.dismiss("");

            if (goterr == true || isCancelled()) {
                MyToast.makeText(SabadKharid_s1_buttonForMap.this, getString(R.string.problemload));
            } else {
                mahale.setAdapter(new ArrayAdapter<String>(SabadKharid_s1_buttonForMap.this, R.layout.spinner_item, mahaleNames));
                if (mahaleNames != null && mahaleNames.size() == 2) {
                    mahale.setSelection(1);
                    mahale_l.setVisibility(View.GONE);
                } else {
                    mahale.post(new Runnable() {
                        @Override
                        public void run() {
                            if (forWhat.equals("edit")) {
                                String def_mahale_name = bl.getString("mahaleId");
                                for (int i = 0; i < mahales.size(); i++) {
                                    if (def_mahale_name.equals(mahales.get(i).getId())) {
                                        mahale.setSelection(i);
                                        break;
                                    }
                                }
                            } else if (!forWhat.equals("new")) {
                                SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
                                String def_mahale_name = settings.getString("mahale_name", "");
                                for (int i = 0; i < mahales.size(); i++) {
                                    if (def_mahale_name.equals(mahales.get(i).getName())) {
                                        mahale.setSelection(i);
                                        break;
                                    }
                                }
                            }
                        }
                    });
                }
//                if(mahaleNames.size()>0)
//                    mahale.setSelection(1);
            }

        }

    }


    private void declare() {
        has_mahale = getResources().getBoolean(R.bool.has_mahale);
        typeface2 = Typeface.createFromAsset(getAssets(), "IRAN Sans.ttf");
        locationOnMap = getResources().getBoolean(R.bool.choose_gps_location_on_map);
        if (locationOnMap) {
            last_gps = (TextView) findViewById(R.id.last_gps);
            last_gps.setVisibility(View.VISIBLE);
            last_gps.setTypeface(typeface2);

            Button map = (Button) findViewById(R.id.map);
            map.setVisibility(View.VISIBLE);
            map.setTypeface(typeface2);
            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SabadKharid_s1_buttonForMap.this, Maps.class));
                }
            });
        }

        mCustomProgressDialog = new CustomProgressDialog(this);
        //mCustomProgressDialog.show("");
        final LinearLayout lngps = (LinearLayout) findViewById(R.id.lngps);
        final LinearLayout onAdres = (LinearLayout) findViewById(R.id.onAdres);
        rd_group = (RadioGroup) findViewById(R.id.rd_group);
        TextView tvinfo = (TextView) findViewById(R.id.tvinfo);
        tvinfo.setTypeface(typeface2);

        if (getResources().getBoolean(R.bool.getCustomerGpsCoord)) {
            lngps.setVisibility(View.GONE);
            onAdres.setVisibility(View.GONE);
        } else {
            tvinfo.setVisibility(View.GONE);

            rd_group.setVisibility(View.GONE);
        }

        rd_Adres = (RadioButton) findViewById(R.id.rd_adres);
        rd_Adres.setTypeface(typeface2);
        rd_gps = (RadioButton) findViewById(R.id.rd_gps);
        rd_gps.setTypeface(typeface2);
        rd_Adres.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lngps.setVisibility(View.GONE);
                    onAdres.setVisibility(View.VISIBLE);
                }
            }
        });
        rd_gps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lngps.setVisibility(View.VISIBLE);
                    onAdres.setVisibility(View.GONE);
                }
            }
        });


        if (getResources().getBoolean(R.bool.sabad_ostan_shahrestan)) {
            OstanShahrestanEnable = true;
            makan = (Button) findViewById(R.id.makan);
            makan.setVisibility(View.VISIBLE);
            makan.setTypeface(typeface2);
            makan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Html(new OnTaskFinished() {
                        @Override
                        public void onFeedRetrieved(String body) {
                            Log.v("this", body);
                            if (body.equals("errordade")) {
                                MyToast.makeText(getApplicationContext(), "اتصال اینترنت را بررسی کنید");
                            } else {
                                try {
                                    JSONObject response = new JSONObject(body);
                                    JSONArray posts = response.optJSONArray("contacts");
                                    final String[] ostans = new String[posts.length()];
                                    final String[] ostanId = new String[posts.length()];
                                    for (int i = 0; i < posts.length(); i++) {
                                        JSONObject post = posts.optJSONObject(i);
                                        ostans[i] = (post.optString("name"));
                                        ostanId[i] = (post.optString("id"));
                                    }
                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SabadKharid_s1_buttonForMap.this);
                                    LayoutInflater inflater = getLayoutInflater();
                                    View convertView = (View) inflater.inflate(R.layout.ostan_shahrestan_ln, null);
                                    TextView tv = (TextView) convertView.findViewById(R.id.ostan_shahrestan);
                                    tv.setTypeface(typeface2);
                                    tv.setText(getString(R.string.choose) + getString(R.string.ostan));


                                    alertDialog.setView(convertView);
                                    ListView lv = (ListView) convertView.findViewById(R.id.lv);
                                    Lv_adapter adapter = new Lv_adapter(SabadKharid_s1_buttonForMap.this, ostans);
                                    lv.setAdapter(adapter);

                                    final AlertDialog ad = alertDialog.show();

                                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                            ostanIDs = ostanId[position];
                                            getShahrestan(ostanId[position]);
                                            makan.setText(ostans[position]);
                                            ad.dismiss();
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, true, SabadKharid_s1_buttonForMap.this, "").execute(getString(R.string.url) + "/getOstanShahrestan.php");

                }
            });
        }

        mobile_l = (TextInputLayout) findViewById(R.id.input_layout_mobile);
        mobile_l.setTypeface(typeface2);
        name_l = (TextInputLayout) findViewById(R.id.input_layout_name);
        name_l.setTypeface(typeface2);
        tel_l = (TextInputLayout) findViewById(R.id.input_layout_tel);
        tel_l.setTypeface(typeface2);
        mobile_l = (TextInputLayout) findViewById(R.id.input_layout_mobile);
        mobile_l.setTypeface(typeface2);
        adres_l = (TextInputLayout) findViewById(R.id.input_layout_adress);
        adres_l.setTypeface(typeface2);
        codeposti_l = (TextInputLayout) findViewById(R.id.input_layout_codeposti);
        codeposti_l.setTypeface(typeface2);
        mahale_l = (TextInputLayout) findViewById(R.id.input_layout_mahale);
        mahale_l.setTypeface(typeface2);


        if (has_mahale == false)
            mahale_l.setVisibility(View.GONE);

        takmil = (TextView) findViewById(R.id.takmil);
        takmil.setTypeface(typeface2);

        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        name = (EditText) findViewById(R.id.name);
        name.setTypeface(typeface2);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                name_l.setErrorEnabled(false);
            }
        });
        if (settings.getString("name_s", "0").equals("0"))
            name.setText(settings.getString("name", ""));
        else if (Func.isDizbon(SabadKharid_s1_buttonForMap.this) == false)
            name.setText(settings.getString("name_s", ""));

        mobile = (EditText) findViewById(R.id.mobile);
        mobile.setTypeface(typeface2);
        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mobile.getText().toString().length() == 11) {
                    mobile_l.setErrorEnabled(false);
                }
            }
        });

        if (settings.getString("mobile_s", "0").equals("0"))
            mobile.setText(settings.getString("mobile", ""));
        else
            mobile.setText(settings.getString("mobile_s", ""));
        tel = (EditText) findViewById(R.id.tel);
        tel.setTypeface(typeface2);
        tel.setText(settings.getString("tel", ""));
        tel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tel_l.setErrorEnabled(false);
            }
        });

        adress = (EditText) findViewById(R.id.adress);
        adress.setTypeface(typeface2);
        adress.setText(settings.getString("adres", ""));
        adress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adres_l.setErrorEnabled(false);
            }
        });

        tozihat = (EditText) findViewById(R.id.tozihat);
        tozihat.setTypeface(typeface2);

        codeposti = (EditText) findViewById(R.id.codeposti);
        codeposti.setTypeface(typeface2);
        codeposti.setText(settings.getString("codeposti", ""));

        mahale = (Spinner) findViewById(R.id.mahale);

        if (OstanShahrestanEnable == false && has_mahale) {
            new getMahale().execute();
        } else
            mahale_l.setVisibility(View.GONE);

        if (getResources().getBoolean(R.bool.getCustomerGpsCoord)) {
            TextView tvgps = (TextView) findViewById(R.id.tvgps);
            tvgps.setTypeface(typeface2);
            tvgps.setText(("موقعیت جغرافیایی"));

            gps = (Button) findViewById(R.id.gps);
            gps.setTypeface(typeface2);
            gps.setText("به دست آوردن موقعیت جغرافیایی");
            gps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && checkSelfPermission(
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && checkSelfPermission(
                            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SabadKharid_s1_buttonForMap.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    } else {
                        doGPS();
                    }
                }
            });
        }

        bl = getIntent().getExtras();
        if (bl != null) {
            forWhat = bl.getString("for");
            if (forWhat.equals("edit")) {
                name.setText(bl.getString("name"));
                adress.setText(bl.getString("adres"));
                codeposti.setText(bl.getString("codeposti"));
                mobile.setText(bl.getString("tel"));
                takmil.setText("ویرایش");
            } else if (forWhat.equals("new")) {
                name.setText("");
                adress.setText("");
                codeposti.setText("");
                mobile.setText("");
                takmil.setText("ایجاد آدرس");
            }
        }
    }

    private void getShahrestan(String id) {
        long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        new Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                if (body.equals("errordade")) {
                    MyToast.makeText(getApplicationContext(), "اشکالی پیش آمده است");
                } else {
                    makeShahrestanDialog(body);
                }
            }
        }, true, SabadKharid_s1_buttonForMap.this, "").execute(getString(R.string.url) + "/getCities.php?id=" + id + "&n=" + number);
    }

    private void makeShahrestanDialog(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("contacts");
            final String[] shahrestan = new String[posts.length()];
            final String[] shahrestanID = new String[posts.length()];

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                shahrestan[i] = post.optString("name");
                shahrestanID[i] = post.optString("id");
            }

            if (posts.length() > 0) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SabadKharid_s1_buttonForMap.this);
                LayoutInflater inflater = getLayoutInflater();
                View convertView = (View) inflater.inflate(R.layout.ostan_shahrestan_ln, null);
                TextView tv = (TextView) convertView.findViewById(R.id.ostan_shahrestan);
                tv.setTypeface(typeface2);
                tv.setText(getString(R.string.choose) + getString(R.string.shahrestan));

                alertDialog.setView(convertView);
                ListView lv = (ListView) convertView.findViewById(R.id.lv);

                Lv_adapter adapter = new Lv_adapter(SabadKharid_s1_buttonForMap.this, shahrestan);
                lv.setAdapter(adapter);

                final AlertDialog ad = alertDialog.show();

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ad.dismiss();
                        shahrestanIDs = (shahrestanID[position]);

                        makan.setText(makan.getText().toString() + " - " + shahrestan[position]);
                    }
                });
            }
        } catch (Exception e) {
            Log.v("this", e.getMessage());
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

    public boolean stopService() {
        if (this.locatorService != null) {
            this.locatorService = null;
        }
        return true;
    }

    public boolean startService() {
        try {
            // this.locatorService= new
            // Intent(FastMainActivity.this,LocatorService.class);
            // startService(this.locatorService);

            FetchCordinates fetchCordinates = new FetchCordinates();
            fetchCordinates.execute();
            return true;
        } catch (Exception error) {
            return false;
        }

    }

    public AlertDialog CreateAlert(String title, String message) {
        AlertDialog alert = new AlertDialog.Builder(this).create();

        alert.setTitle(title);

        alert.setMessage(message);

        return alert;

    }

    public class FetchCordinates extends AsyncTask<String, Integer, String> {
        AlertDialog.Builder a;
        AlertDialog dialog;

        public double lati = 0.0;
        public double longi = 0.0;

        public LocationManager mLocationManager;
        public VeggsterLocationListener mVeggsterLocationListener;

        @Override
        protected void onPreExecute() {
            mVeggsterLocationListener = new VeggsterLocationListener();
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0,
                    mVeggsterLocationListener);

            a = new AlertDialog.Builder(SabadKharid_s1_buttonForMap.this);
            a.setMessage("در حال  به دست آوردن موقعیت جغرافیایی...");
            a.setPositiveButton(("لغو"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    FetchCordinates.this.cancel(true);
                }
            });

            dialog = a.show();
            TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
            messageText.setGravity(Gravity.RIGHT);
            messageText.setTypeface(typeface2);
        }

        @Override
        protected void onCancelled() {
            System.out.println("Cancelled by user!");
            dialog.dismiss();
            mLocationManager.removeUpdates(mVeggsterLocationListener);
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            lat = lati;
            lon = longi;
            gps.setText(lati + " - " + longi);
            MyToast.makeText(SabadKharid_s1_buttonForMap.this, "موقعیت شما با موفقیت ثبت شد");
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            while (this.lati == 0.0 && !isCancelled()) {

            }
            return null;
        }

        public class VeggsterLocationListener implements LocationListener {

            @Override
            public void onLocationChanged(Location location) {

                int lat = (int) location.getLatitude(); // * 1E6);
                int log = (int) location.getLongitude(); // * 1E6);
                int acc = (int) (location.getAccuracy());

                String info = location.getProvider();
                try {

                    // LocatorService.myLatitude=location.getLatitude();

                    // LocatorService.myLongitude=location.getLongitude();

                    lati = location.getLatitude();
                    longi = location.getLongitude();

                } catch (Exception e) {
                    // progDailog.dismiss();
                    // Toast.makeText(getApplicationContext(),"Unable to get Location"
                    // , Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i("OnProviderDisabled", "OnProviderDisabled");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i("onProviderEnabled", "onProviderEnabled");
            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                Log.i("onStatusChanged", "onStatusChanged");

            }

        }

    }


    private void doGPS() {
        try {
            LocationManager mlocManager = null;
            LocationListener mlocListener;
            mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mlocListener = new MyLocationListener();
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
            if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                startService();
//                Location location = null;
//                location = mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                if(MyLocationListener.latitude>0){
//                    lat=(MyLocationListener.latitude);
//                    lon=(MyLocationListener.longitude);
////                if (location != null) {
////
//                    gps.setText(lat + " - "+ lon);
//                    MyToast.makeText(SabadKharid_s1.this,"موقعیت شما با موفقیت ثبت شد");
//                }else
//                    MyToast.makeText(SabadKharid_s1.this, Z_Farsi.Convert(getString(R.string.gpsfinding)));
            } else {
                AlertDialog.Builder a = new AlertDialog.Builder(SabadKharid_s1_buttonForMap.this);
                a.setMessage(("جی پی اس خاموش است. آیا میخواهید روشن کنید؟"));
                a.setPositiveButton(("بله"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
                        boolean enabled = service
                                .isProviderEnabled(LocationManager.GPS_PROVIDER);
                        if (!enabled) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    }
                });
                a.setNegativeButton(("خیر"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = a.show();
                TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                messageText.setGravity(Gravity.RIGHT);
                messageText.setTypeface(typeface2);
            }
        } catch (Exception e) {
            MyToast.makeText(SabadKharid_s1_buttonForMap.this, e.getMessage());
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doGPS();
                } else {
                    MyToast.makeText(SabadKharid_s1_buttonForMap.this, "دسترسی به جی پی اس غیرفعال است");
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            stopService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (locationOnMap) {
            SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
            if (settings.getString("lat", "0").length() > 3) {
                last_gps.setText("وضعیت : موقعیت جغرافییایی مشخص شده است");
            } else {
                last_gps.setText("وضعیت : موقعیت جغرافییایی مشخص نشده است");
            }
        }
    }

}
