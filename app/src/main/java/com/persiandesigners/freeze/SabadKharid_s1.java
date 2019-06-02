package com.persiandesigners.freeze;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.persiandesigners.freeze.Util.CustomProgressDialog;
import com.persiandesigners.freeze.Util.DialogListView;
import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.HtmlPost;
import com.persiandesigners.freeze.Util.Lv_adapter;
import com.persiandesigners.freeze.Util.MyLocationListener;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SabadKharid_s1 extends AppCompatActivity implements
        com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    Typeface typeface2;
    TextView takmil;
    TextView et_bazaryab;
    EditText onvan, name, mobile, tel, adress, tozihat, codeposti, vahed, tabaghe, pelak;
    Spinner mahale;
    TextInputLayout mobile_l, name_l, tel_l, adres_l, codeposti_l, mahale_l;
    CustomProgressDialog mCustomProgressDialog;
    ArrayList<String> mahaleNames;
    ArrayList<Class_Mahale> mahales;
    Boolean getLocation = false, forEdit = false;
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

    private static final String TAG = "this";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
    Boolean hasGps = true;

    SupportMapFragment googleMap;
    ScrollView mScrollView;
    Boolean isBazaryab = false;
    GoogleMap Map;
    Double lat = 0.0, lon;
    Boolean clicked;
    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sabadkharids1);

        declare();
        takmil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        //////////////map
        if (hasGps && isGooglePlayServicesAvailable()) {
            clicked = false;
            try {
                initilizeMap();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        actionbar();
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

        } else if (adress.getText().toString().length() < 10) {
            adres_l.setErrorEnabled(true);
            adres_l.setError("آدرس صحیح وارد کنید");
            adress.requestFocus();
        } else if (OstanShahrestanEnable == false && mahale.getSelectedItemPosition() == 0 && has_mahale) {
            MyToast.makeText(SabadKharid_s1.this, getString(R.string.wrong_mahale));
        } else if (getResources().getBoolean(R.bool.sabad_ostan_shahrestan)
                && makan.getText().toString().contains("انتخاب")) {
            MyToast.makeText(SabadKharid_s1.this, "استان و شهرستان را انتخاب کنید");
        } else if (rd_Adres.isChecked() && adress.getText().length() < 10) {
            adres_l.setErrorEnabled(true);
            adres_l.setError("آدرس صحیح وارد کنید");
            adress.requestFocus();
        } else if (rd_gps.isChecked() && gps.getText().toString().contains("دست")) {
            MyToast.makeText(SabadKharid_s1.this, "موقعیت جغرافیایی را مشخص کنید");
        } else if (getResources().getBoolean(R.bool.getCustomerGpsCoord) == true &&
                rd_Adres.isChecked() == false && rd_gps.isChecked() == false) {
            MyToast.makeText(SabadKharid_s1.this, "نحوه مشخص کردن موقعیت کاربر را انتخاب کنید");
        /*} else if (codeposti.getText().length() != 10) {
            codeposti_l.setErrorEnabled(true);
            codeposti_l.setError(getString(R.string.wrong_codeposti));
            codeposti.requestFocus();
        */
        } else {
            if (forWhat.equals("new") || forWhat.equals("edit")) {
                //getAddAdress.php
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("onvan", onvan.getText().toString())
                        .appendQueryParameter("name", name.getText().toString())
                        .appendQueryParameter("uid", Func.getUid(SabadKharid_s1.this))
                        .appendQueryParameter("tel", mobile.getText().toString())
                        .appendQueryParameter("adres", adress.getText().toString())
                        .appendQueryParameter("vahed", vahed.getText().toString())
                        .appendQueryParameter("tabaghe", tabaghe.getText().toString())
                        .appendQueryParameter("pelak", pelak.getText().toString())
                        .appendQueryParameter("ostanId", ostanIDs)
                        .appendQueryParameter("shahrestanIDs", shahrestanIDs)
                        .appendQueryParameter("codeposti", codeposti.getText().toString());
                if (mahales != null && mahale != null)
                    builder.appendQueryParameter("mahale", mahales.get(mahale.getSelectedItemPosition()).getId());

                builder.appendQueryParameter("lat", lat + "");
                builder.appendQueryParameter("lon", lon + "");

                if (forWhat.equals("edit"))
                    builder.appendQueryParameter("id", bl.getString("id"));
                else
                    builder.appendQueryParameter("id", "0");

                String query = builder.build().getEncodedQuery();
                new HtmlPost(new OnTaskFinished() {
                    @Override
                    public void onFeedRetrieved(String body) {
                        Log.v("this", body);
                        if (body.equals("errordade")) {

                        } else if (body.equals("ok")) {

                        } else if (body.equals("err"))
                            MyToast.makeText(SabadKharid_s1.this, getString(R.string.problem));
                    }
                }, false, SabadKharid_s1.this, "", query).execute(getString(R.string.url) + "/getAddAdress.php");
            }
            SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
            SharedPreferences.Editor pref = settings.edit();
            pref.putString("onvan", onvan.getText().toString());
            pref.putString("name_s", name.getText().toString());
            pref.putString("tel", tel.getText().toString());
            pref.putString("mobile_s", mobile.getText().toString());
            pref.putString("adres", adress.getText().toString());
            pref.putString("codeposti", codeposti.getText().toString());
            pref.putString("vahed", vahed.getText().toString());
            pref.putString("tabaghe", tabaghe.getText().toString());
            pref.putString("pelak", pelak.getText().toString());
            pref.putString("lat", lat + "");
            pref.putString("lon", lon + "");

            if (mahales != null && OstanShahrestanEnable == false) {
                Intent in = null;
                if (bl != null && (bl.getString("for").equals("edit") || bl.getString("for").equals("new")))
                    in = new Intent(this, SabadAddress.class);
                else
                    in = new Intent(this, Sabad_Takmil.class);
                if (getResources().getBoolean(R.bool.choose_gps_location_on_map)) {
                    in.putExtra("lat", String.valueOf(lat));
                    in.putExtra("lon", String.valueOf(lon));
                }
                in.putExtra("shahrstanId", mahales.get(mahale.getSelectedItemPosition()).getId());
                in.putExtra("shahrstanPrice", mahales.get(mahale.getSelectedItemPosition()).getprice());
                pref.putString("mahale_name", mahales.get(mahale.getSelectedItemPosition()).getName());

                in.putExtra("msg", tozihat.getText().toString());
                in.putExtra("ostan", "");
                in.putExtra("ostanId", "");
                if (bl != null && bl.getString("from") != null)
                    in.putExtra("from", bl.getString("from"));
                startActivity(in);
                if (bl != null && (bl.getString("for").equals("edit") || bl.getString("for").equals("new")))
                    finish();
            } else if (OstanShahrestanEnable || has_mahale == false) {//ostanShahrstan
                pref.putString("ostan", makan.getText().toString());
                Intent in = new Intent(this, Sabad_Takmil.class);
                if (getResources().getBoolean(R.bool.choose_gps_location_on_map)) {
                    in.putExtra("lat", String.valueOf(lat));
                    in.putExtra("lon", String.valueOf(lon));
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
            String more = "";
            if (getResources().getBoolean(R.bool.multiseller)) {
                more = "?shopId=" + Func.getShopId(SabadKharid_s1.this);
            }
            try {
                URL url = new URL(getString(R.string.url) + "/getMahaleha.php" + more);
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
                MyToast.makeText(SabadKharid_s1.this, getString(R.string.problemload));
            } else {
                mahale.setAdapter(new ArrayAdapter<String>(SabadKharid_s1.this, R.layout.spinner_item, mahaleNames));
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
        isBazaryab = Func.getIsBazaryab(this);

        has_mahale = getResources().getBoolean(R.bool.has_mahale);
        typeface2 = Typeface.createFromAsset(getAssets(), "IRAN Sans.ttf");
        locationOnMap = getResources().getBoolean(R.bool.choose_gps_location_on_map);
        if (!isGooglePlayServicesAvailable())
            locationOnMap = false;

        if (locationOnMap == false) {
            hasGps = false;
            RelativeLayout lnmap = (RelativeLayout) findViewById(R.id.lnmap);
            lnmap.setVisibility(View.GONE);
        }

        onvan = (EditText) findViewById(R.id.onvan);
        onvan.setTypeface(typeface2);
        if (Func.getUid(this).equals("0"))
            onvan.setVisibility(View.GONE);

        Func.setUidFromBazaryab(this,"0");
        et_bazaryab = findViewById(R.id.et_bazaryab);
        if(isBazaryab) {
            et_bazaryab.setVisibility(View.VISIBLE);
            onvan.setVisibility(View.GONE);
        }
        et_bazaryab.setTypeface(typeface2);
        et_bazaryab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBazaryabha();
            }
        });

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
                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SabadKharid_s1.this);
                                    LayoutInflater inflater = getLayoutInflater();
                                    View convertView = (View) inflater.inflate(R.layout.ostan_shahrestan_ln, null);
                                    TextView tv = (TextView) convertView.findViewById(R.id.ostan_shahrestan);
                                    tv.setTypeface(typeface2);
                                    tv.setText(getString(R.string.choose) + " " + getString(R.string.ostan));


                                    alertDialog.setView(convertView);
                                    ListView lv = (ListView) convertView.findViewById(R.id.lv);
                                    Lv_adapter adapter = new Lv_adapter(SabadKharid_s1.this, ostans);
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
                    }, true, SabadKharid_s1.this, "").execute(getString(R.string.url) + "/getOstanShahrestan.php");

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

        vahed = (EditText) findViewById(R.id.vahed);
        vahed.setTypeface(typeface2);
        tabaghe = (EditText) findViewById(R.id.tabaghe);
        tabaghe.setTypeface(typeface2);
        pelak = (EditText) findViewById(R.id.pelak);
        pelak.setTypeface(typeface2);


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
        else if (Func.isDizbon(SabadKharid_s1.this) == false)
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
        mahale.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adress.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (OstanShahrestanEnable == false && has_mahale) {
            new getMahale().execute();
        } else
            mahale_l.setVisibility(View.GONE);

        bl = getIntent().getExtras();
        if (bl != null) {
            forWhat = bl.getString("for");
            if (forWhat.equals("edit")) {
                forEdit = true;
                onvan.setText(bl.getString("onvan"));
                name.setText(bl.getString("name"));
                adress.setText(bl.getString("adres"));
                codeposti.setText(bl.getString("codeposti"));
                mobile.setText(bl.getString("tel"));
                vahed.setText(bl.getString("vahed"));
                tabaghe.setText(bl.getString("tabaghe"));
                pelak.setText(bl.getString("pelak"));

                takmil.setText("ویرایش");
            } else if (forWhat.equals("new")) {
                onvan.setText("");
                name.setText("");
                adress.setText("");
                codeposti.setText("");
                mobile.setText("");
                vahed.setText("");
                tabaghe.setText("");
                pelak.setText("");

                takmil.setText("ایجاد آدرس");
            }
        }

    }

    private void getBazaryabha() {
        long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        new Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                Log.v("this", body);
                if (body.equals("errordade")) {
                    MyToast.makeText(getApplicationContext(), "اتصال به اینترنت را بررسی کنید");
                } else {

                    try {
                        JSONObject jsonObject = new JSONObject(body);
                        JSONArray jsonArray = jsonObject.optJSONArray("bazaryabha");
                        final String names[] = new String[jsonArray.length()];
                        final String adreses[] = new String[jsonArray.length()];
                        final String mobileArray[] = new String[jsonArray.length()];
                        final String ides[] = new String[jsonArray.length()];
                        final String vaheds[] = new String[jsonArray.length()];
                        final String pelaks[] = new String[jsonArray.length()];
                        final String tabaghes[] = new String[jsonArray.length()];

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject row = jsonArray.optJSONObject(i);
                            names[i]=row.optString("name");
                            adreses[i]=row.optString("address");
                            ides[i]=row.optString("id");
                            mobileArray[i]=row.optString("mobile");
                            vaheds[i]=row.optString("mobile");
                            pelaks[i]=row.optString("mobile");
                            tabaghes[i]=row.optString("mobile");
                        }

                        Drawable icon = getResources().getDrawable(R.drawable.default_avatar);
                        new DialogListView(new OnTaskFinished() {
                            @Override
                            public void onFeedRetrieved(String position) {
                                Func.setUidFromBazaryab(SabadKharid_s1.this,ides[Integer.parseInt(position)]);
                                et_bazaryab.setText("کاربر : " + names[Integer.parseInt(position)]
                                +" - "+ mobileArray[Integer.parseInt(position)]);
                                name.setText(names[Integer.parseInt(position)]);
                                mobile.setText(mobileArray[Integer.parseInt(position)]);
                                adress.setText(adreses[Integer.parseInt(position)]);
                                pelak.setText(pelaks[Integer.parseInt(position)]);
                                vahed.setText(vaheds[Integer.parseInt(position)]);
                                tabaghe.setText(tabaghes[Integer.parseInt(position)]);

                            }
                        }, SabadKharid_s1.this, names, icon, "انتخاب کاربر");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, true, SabadKharid_s1.this, "").execute(getString(R.string.url) + "/getBazaryabHa.php?n=" + number);


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
        }, true, SabadKharid_s1.this, "").execute(getString(R.string.url) + "/getCities.php?id=" + id + "&n=" + number);
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
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SabadKharid_s1.this);
                LayoutInflater inflater = getLayoutInflater();
                View convertView = (View) inflater.inflate(R.layout.ostan_shahrestan_ln, null);
                TextView tv = (TextView) convertView.findViewById(R.id.ostan_shahrestan);
                tv.setTypeface(typeface2);
                tv.setText(getString(R.string.choose) + " " + getString(R.string.shahrestan));

                alertDialog.setView(convertView);
                ListView lv = (ListView) convertView.findViewById(R.id.lv);

                Lv_adapter adapter = new Lv_adapter(SabadKharid_s1.this, shahrestan);
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

    ////////////maps 
    private void doGPS() {
        if (mGoogleApiClient == null) {
            createLocationRequest();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            if (!isGooglePlayServicesAvailable()) {
                MyToast.makeText(SabadKharid_s1.this, "گوگل پلی سرویس برای استفاده از نقشه نیاز است");
                finish();
            }

            mGoogleApiClient.connect();
        }

        LocationManager mlocManager = null;
        LocationListener mlocListener;
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(SabadKharid_s1.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SabadKharid_s1.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//gps is connected
            mGoogleApiClient.connect();
        } else {
            AlertDialog.Builder a = new AlertDialog.Builder(SabadKharid_s1.this);
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

            Button btn1 = (Button) dialog.findViewById(android.R.id.button1);
            btn1.setTypeface(typeface2);

            Button btn2 = (Button) dialog.findViewById(android.R.id.button2);
            btn2.setTypeface(typeface2);
        }
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onStart() {
        super.onStart();
        getLocation = false;
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null && hasGps) {
            mGoogleApiClient.connect();
            Log.v(TAG, "onStart fired ..............");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && hasGps) {
            Log.v(TAG, "onStop fired ..............");
            mGoogleApiClient.disconnect();
            Log.v(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
//            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (mGoogleApiClient != null) {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            Log.v(TAG, "Location update started ..............: ");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI() {
        Log.v(TAG, "UI update initiated .............");
        if (null != mCurrentLocation && getLocation == false && forEdit == false) {

            Map.clear();
            getLocation = true;
            String lats = String.valueOf(mCurrentLocation.getLatitude());
            String lngs = String.valueOf(mCurrentLocation.getLongitude());
            Log.v("this", "At Time: " + mLastUpdateTime + "\n" +
                    "Latitude: " + lats + "\n" +
                    "Longitude: " + lngs + "\n" +
                    "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
                    "Provider: " + mCurrentLocation.getProvider());
            lat = mCurrentLocation.getLatitude();
            lon = mCurrentLocation.getLongitude();

            LatLng sydney = new LatLng(lat, lon);
            Map.addMarker(new MarkerOptions().position(sydney).title(""));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(15).build();
            Map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } else {
            Log.v(TAG, "location is null ...............");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (hasGps)
            stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Location update stopped .......................");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasGps && isGooglePlayServicesAvailable()) {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                startLocationUpdates();
                Log.v(TAG, "Location update resumed .....................");
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && checkSelfPermission(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                doGPS();
            }
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
                    MyToast.makeText(SabadKharid_s1.this, "دسترسی به جی پی اس غیرفعال است");
                }
                return;
            }
        }
    }

    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = (WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            googleMap.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    Map = googleMap;
                    Map.getUiSettings().setZoomControlsEnabled(true);

                    mScrollView = (ScrollView) findViewById(R.id.scroll); //parent scrollview in xml, give your scrollview id value
                    ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                            .setListener(new WorkaroundMapFragment.OnTouchListener() {
                                @Override
                                public void onTouch() {
                                    mScrollView.requestDisallowInterceptTouchEvent(true);
                                }
                            });
                    ///it requires permission ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION
                    try {
                        Map.setMyLocationEnabled(true);
                        Map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                            @Override
                            public boolean onMyLocationButtonClick() {
                                mGoogleApiClient.connect();
                                clicked = false;
                                Map.clear();
                                getLocation = false;
                                startLocationUpdates();
                                return true;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (googleMap == null) {
                        Toast.makeText(getApplicationContext(),
                                "نقشه روی گوشی شما قابل نمایش نیست", Toast.LENGTH_SHORT)
                                .show();
                    }

                    Map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            Map.clear();
                            Map.addMarker(new MarkerOptions().position(latLng));
                            lat = latLng.latitude;
                            lon = latLng.longitude;
                            clicked = true;
                            stopLocationUpdates();
                        }
                    });

                    if (bl != null && bl.getString("for").equals("edit")
                            && bl.getString("lat").length() > 2) {
                        try {
                            LatLng sydney = new LatLng(Double.parseDouble(bl.getString("lat")), Double.parseDouble(bl.getString("lon")));
                            Map.addMarker(new MarkerOptions().position(sydney).title(""));
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(15).build();
                            Map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            stopLocationUpdates();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.v("this", "err " + e.getMessage());
                        }
                    } else
                        getDefultLatLon(Map);
                }
            });

        }

    }

    private void getDefultLatLon(GoogleMap map) {
        long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        new Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                Log.v("this", body);
                if (body.equals("errordade")) {

                } else {
                    try {
                        JSONObject json = new JSONObject(body);

                        LatLng sydney = new LatLng(json.optDouble("lat"), json.optDouble("long"));
                        //Map.addMarker(new MarkerOptions().position(sydney).title(""));
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                        Map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, false, SabadKharid_s1.this, "").execute(getString(R.string.url) + "/getDefLatLon.php?n=" + number);

    }

    private void actionbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        action.MakeActionBar("آدرس");
        Func.checkSabad(SabadKharid_s1.this);

        ImageView img_sabad = (ImageView) findViewById(R.id.img_sabad);
        img_sabad.setVisibility(View.GONE);
        TextView tvnumsabad = (TextView) findViewById(R.id.text_numkharid);
        tvnumsabad.setVisibility(View.GONE);
    }
}
