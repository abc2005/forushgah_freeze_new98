package com.persiandesigners.freeze;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.Lv_adapter;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by navid on 6/15/2016.
 */
public class Register extends AppCompatActivity {
    Toolbar toolbar;
    Typeface typeface2;
    EditText adress,mobile, pass,repass,user,email,moaref,verify,codeposti,tel;
    TextView register;
    TextInputLayout mobile_l, pass_l,repass_lpass_l,user_l,email_l,moaref_l,verify_l;
    ProgressDialog pDialog;
    LinearLayout mainln, verifyln;
    Boolean run=false,log_reg_sms=false;
    Runnable r =null;
    Handler handler ;
    Button makan;
    String ostanIDs = "0", shahrestanIDs = "0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        declare();
        actionbar();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void submit() {
        user_l.setErrorEnabled(false);
        mobile_l.setErrorEnabled(false);
        pass_l.setErrorEnabled(false);
        repass_lpass_l.setErrorEnabled(false);
        moaref_l.setErrorEnabled(false);
        if (mobile.getText().toString().trim().length() != 11) {
            mobile_l.setErrorEnabled(true);
            mobile_l.setError(getString(R.string.wrong_mobile));
            mobile.requestFocus();
        } else if (user.getText().toString().trim().length() <3) {
            user_l.setErrorEnabled(true);
            user_l.setError(getString(R.string.wrong_name));
            user.requestFocus();
        }else if (pass.getText().length() < 4 && log_reg_sms==false) {
            repass_lpass_l.setErrorEnabled(false);
            pass_l.setError(getString(R.string.wrong_pass));
            pass.requestFocus();
        } else if (repass.getText().length() < 4 && log_reg_sms==false) {
            repass_lpass_l.setErrorEnabled(true);
            repass_lpass_l.setError(getString(R.string.wrong_pass));
            repass.requestFocus();
        }else if(!pass.getText().toString().equals(repass.getText().toString())){
            repass_lpass_l.setErrorEnabled(true);
            repass_lpass_l.setError(getString(R.string.noeqalpass));
            repass.requestFocus();
        }else if (moaref.getText().length() != 11 && moaref.getText().length()>2) {
            moaref_l.setErrorEnabled(true);
            moaref_l.setError(getString(R.string.wrong_moaref));
            moaref.requestFocus();
        } else {
            new submitForm().execute();
        }
    }
    private class submitForm extends AsyncTask<String, String, String> {

        Boolean goterr=false ;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Register.this);
            pDialog.setMessage(("در حال ارسال اطلاعات"));
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... params) {
            String responseBody="";
            try {
                String more="";
                if(getResources().getBoolean(R.bool.verify_user_by_sms)==true)
                    more="?activeBySms=true";
                if(log_reg_sms)
                    more="?log_reg_by_sms=true";

                URL url = new URL(getString(R.string.url)+"/getRegister.php"+more);
                Log.v("this",getString(R.string.url)+"/getRegister.php"+more);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(20000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("e",mobile.getText().toString().trim())
                        .appendQueryParameter("adres",adress.getText().toString().trim())
                        .appendQueryParameter("p", pass.getText().toString().trim())
                        .appendQueryParameter("name",user.getText().toString())
                        .appendQueryParameter("em", email.getText().toString().trim())
                        .appendQueryParameter("moaref", moaref.getText().toString().trim())
                        .appendQueryParameter("shahrestan_id", shahrestanIDs+"")
                        .appendQueryParameter("codeposti", codeposti.getText().toString().trim()+"")
                        .appendQueryParameter("tel", tel.getText().toString().trim())
                        ;
                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        responseBody+=line;
                    }
                }
            } catch (IOException e) {

            }
            return responseBody;
        }

        @Override
        protected void onPostExecute(String body) {
            super.onPostExecute(body);

            if(pDialog!=null && pDialog.isShowing())
                pDialog.dismiss();

            if(goterr==false && !isCancelled()){
                if (body.equals("errordade")) {
                    MyToast.makeText(Register.this, "اشکالی پیش آمده است");
                }else if(body.contains("userExsist")) {
                    mobile_l.setError(getString(R.string.mobile_exist));
                    mobile.requestFocus();
                }else if(body.contains("wrong_moaref")) {
                    moaref_l.setError(getString(R.string.wrong_maoref_entered));
                    moaref.requestFocus();
                }else if(body.length()>0 && body.contains("id")){
                    if(getResources().getBoolean(R.bool.verify_user_by_sms)==true || log_reg_sms){//verify by sms
                        dialogCode(body);
                    }else{
                        doRegister(body);
                    }
                }
            }else if (goterr==true)
                MyToast.makeText(Register.this, ("اشکالی به وجود آمده است . مجددا سعی کنید"));

        }
    }

    private void dialogCode(final String body) {
		final TextView  countdown = (TextView) findViewById(R.id.countdown);
        if(Func.isDizbon(this)==false) {
            Func func = new Func(Register.this);
            func.MakeCountDown();
        }else{
            countdown.setVisibility(View.GONE);
        }
		
		TextView sendagain=(TextView)findViewById(R.id.sendagain);
        sendagain.setTypeface(typeface2);
        sendagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout ln = (LinearLayout) findViewById(R.id.lnmain);
                if(countdown.getText().toString().equals("0")){
                    submit();
                }else{
                    Snackbar.make(ln, "لطفا تا 0 شدن زمان شکیبا باشید", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        handler = new Handler();
        r = new Runnable() {
            public void run() {
                Log.v("this","run");
                SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
                if(settings.getString("code","0").length()>1){
                    verify.setText(settings.getString("code","0"));
                    if (verify.getText().toString().length() == 5 && run==false) {
                        run=true;
                        CheckCode(body, verify.getText().toString());
                    }
                    try {
                        if(handler!=null)
                            handler.removeCallbacks(r);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                handler.postDelayed(this,500);
            }
        };
        handler.postDelayed(r, 500);


        verifyln.setVisibility(View.VISIBLE);
        mainln.setVisibility(View.GONE);

        verify.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (verify.getText().toString().length() == 5 && run==false) {
                    run=true;
                    CheckCode(body, verify.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        View view = Register.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void CheckCode(final String bodys, String mabda) {
        long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        new Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                Log.v("this", body);
                if (body.equals("errordade")) {
                    MyToast.makeText(getApplicationContext(), "اشکالی پیش آمده است");
					run=false;
                } else if (body.contains("name")) {
                    doRegister(body);
                } else {
                    MyToast.makeText(Register.this, "کد رهگیری وارد شده اشتباه است");
                    dialogCode(bodys);
                    run=false;
                }
            }
        }, true, Register.this, "").execute(getString(R.string.url) + "/getActiveUser.php?code=" + mabda + "&n=" + number);
    }

    private void doRegister(String body) {
        try {
            JSONObject response = new JSONObject(body);
            JSONArray posts = response.optJSONArray("contacts");
            JSONObject post = posts.optJSONObject(0);
            SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
            SharedPreferences.Editor pref = settings.edit();
            pref.putString("mobile", mobile.getText().toString());
            pref.putString("p", post.optString("p"));
            pref.putString("uid", post.optString("id"));
            pref.putString("name", user.getText().toString());
            pref.putString("adres", adress.getText().toString());
//                        pref.putString("mahaleId", mahales.get(mahale.getSelectedItemPosition()).getId());
            pref.commit();

            MyToast.makeText(Register.this, "عضویت با موفقیت انجام شد");
            startActivity(new Intent(Register.this, Home.class));

            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("this", e.getMessage());
        }
    }


    private void declare() {
        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor pref = settings.edit();
        pref.putString("code","0");
        pref.commit();

        log_reg_sms=getResources().getBoolean(R.bool.login_register_by_sms_no_pass_needed);
        typeface2 = Typeface.createFromAsset(getAssets(), "IRAN Sans.ttf");
        tel = (EditText) findViewById(R.id.tel);
        tel.setTypeface(typeface2);
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
                if(mobile.getText().toString().length()==11){
                    mobile_l.setErrorEnabled(false);
                }
            }
        });
        pass = (EditText) findViewById(R.id.password);
        pass.setTypeface(typeface2);
        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pass_l.setErrorEnabled(false);
            }
        });

        repass= (EditText) findViewById(R.id.repass);
        repass.setTypeface(typeface2);
        repass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                repass_lpass_l.setErrorEnabled(false);
                if(!pass.getText().toString().equals(repass.getText().toString())){
                    repass_lpass_l.setErrorEnabled(true);
                    repass_lpass_l.setError(getString(R.string.noeqalpass));
                    repass.requestFocus();
                }else{
                    repass_lpass_l.setErrorEnabled(false);
                }
            }
        });

        user= (EditText) findViewById(R.id.user);
        user.setTypeface(typeface2);


        email= (EditText) findViewById(R.id.email);
        email.setTypeface(typeface2);
        moaref= (EditText) findViewById(R.id.moaref);
        moaref.setTypeface(typeface2);
        verify = (EditText) findViewById(R.id.verifycode);
        verify.setTypeface(typeface2);
        adress = (EditText) findViewById(R.id.adress);
        adress.setTypeface(typeface2);
        codeposti = (EditText) findViewById(R.id.codeposti);
        codeposti.setTypeface(typeface2);
        register = (TextView) findViewById(R.id.register);
        register.setTypeface(typeface2);

        mobile_l = (TextInputLayout) findViewById(R.id.input_layout_mobile);
        mobile_l.setTypeface(typeface2);
        moaref_l = (TextInputLayout) findViewById(R.id.input_layout_moaref);
        moaref_l.setTypeface(typeface2);
        if(getResources().getBoolean(R.bool.has_moaref)){
            moaref_l.setVisibility(View.VISIBLE);
        }
        pass_l = (TextInputLayout) findViewById(R.id.input_layout_pass);
        pass_l.setTypeface(typeface2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            pass_l.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        repass_lpass_l = (TextInputLayout) findViewById(R.id.input_layout_repass);
        repass_lpass_l.setTypeface(typeface2);
        user_l = (TextInputLayout) findViewById(R.id.input_layout_user);
        user_l.setTypeface(typeface2);
        email_l = (TextInputLayout) findViewById(R.id.input_layout_email);
        email_l.setTypeface(typeface2);

        verify_l = (TextInputLayout) findViewById(R.id.input_layout_verify);
        verify_l.setTypeface(typeface2);
        mainln = (LinearLayout) findViewById(R.id.mainln);
        verifyln = (LinearLayout) findViewById(R.id.veriftyln);



        user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                user_l.setErrorEnabled(false);
            }
        });


        TextView tvverify = (TextView) findViewById(R.id.tvverify);
        tvverify.setTypeface(typeface2);

        TextInputLayout input_layout_codeposti = (TextInputLayout) findViewById(R.id.input_layout_codeposti);
        input_layout_codeposti.setTypeface(typeface2);
        TextInputLayout input_layout_adress = (TextInputLayout) findViewById(R.id.input_layout_adress);
        input_layout_adress.setTypeface(typeface2);
        TextInputLayout input_layout_tel = (TextInputLayout) findViewById(R.id.input_layout_tel);
        input_layout_tel.setTypeface(typeface2);

        makan = (Button) findViewById(R.id.makan);
        makan.setVisibility(View.VISIBLE);
        makan.setTypeface(typeface2);
        makan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Register.this);
                LayoutInflater inflater = getLayoutInflater();
                View convertView = (View) inflater.inflate(R.layout.ostan_shahrestan_ln, null);
                TextView tv = (TextView) convertView.findViewById(R.id.ostan_shahrestan);
                tv.setTypeface(typeface2);
                tv.setText(getString(R.string.choose) + getString(R.string.ostan));
                final String[] ostans = getResources().getStringArray(R.array.ostan_name);

                alertDialog.setView(convertView);
                ListView lv = (ListView) convertView.findViewById(R.id.lv);
                Lv_adapter adapter = new Lv_adapter(Register.this, ostans);
                lv.setAdapter(adapter);

                final AlertDialog ad = alertDialog.show();

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String[] ostanId = getResources().getStringArray(R.array.ostan_id);
                        ostanIDs = ostanId[position];
                        getShahrestan(ostanId[position]);
                        makan.setText(ostans[position]);
                        ad.dismiss();
                    }
                });
            }
        });
        if(getResources().getBoolean(R.bool.reg_ostan_shahrestan)==false)
            makan.setVisibility(View.GONE);
        if(getResources().getBoolean(R.bool.reg_adres)==false)
            input_layout_adress.setVisibility(View.GONE);
        if(getResources().getBoolean(R.bool.reg_codeposti)==false)
            input_layout_codeposti.setVisibility(View.GONE);
        if(getResources().getBoolean(R.bool.reg_tel)==false)
            input_layout_tel.setVisibility(View.GONE);
    if(getResources().getBoolean(R.bool.reg_email)==false) {
            email_l.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
        }

        if(log_reg_sms){
            pass_l.setVisibility(View.GONE);
            repass_lpass_l.setVisibility(View.GONE);
        }

        if (Build.VERSION.SDK_INT >= 23) {

                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(android.Manifest.permission.RECEIVE_SMS)
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.v("this","granted");
                    } else {
                        ActivityCompat.requestPermissions(Register.this, new String[]{android.Manifest.permission.RECEIVE_SMS}, 2);
                        Log.v("this","need");
                    }
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
        }, true, Register.this, "").execute(getString(R.string.url) + "/getCities.php?id=" + id + "&n=" + number);
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
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Register.this);
                LayoutInflater inflater = getLayoutInflater();
                View convertView = (View) inflater.inflate(R.layout.ostan_shahrestan_ln, null);
                TextView tv = (TextView) convertView.findViewById(R.id.ostan_shahrestan);
                tv.setTypeface(typeface2);
                tv.setText(getString(R.string.choose) + getString(R.string.shahrestan));

                alertDialog.setView(convertView);
                ListView lv = (ListView) convertView.findViewById(R.id.lv);

                Lv_adapter adapter = new Lv_adapter(Register.this, shahrestan);
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


    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(android.Manifest.permission.RECEIVE_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.v("this","granted");
                } else {
                    ActivityCompat.requestPermissions(Register.this, new String[]{android.Manifest.permission.RECEIVE_SMS}, 2);
                    Log.v("this","need");
                }
            }
        }
    }
    private void actionbar() {
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        action.MakeActionBar(getString(R.string.register));

        ImageView img_sabad = (ImageView)findViewById(R.id.img_sabad);
        img_sabad.setVisibility(View.GONE);
        TextView tvnumsabad = (TextView) findViewById(R.id.text_numkharid);
        tvnumsabad.setVisibility(View.GONE);
        if(getResources().getBoolean(R.bool.registerOnFpage)){
            ImageView imgsearch = (ImageView)findViewById(R.id.imgsearch);
            imgsearch.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(pDialog!=null && pDialog.isShowing())
            pDialog.dismiss();

        try {
            if(handler!=null)
                handler.removeCallbacks(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
//        Snackbar snack = Snackbar.make(verifyln,, Snackbar.LENGTH_LONG);
//        View view = snack.getView();
//        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
//        tv.setGravity(Gravity.RIGHT);
//        tv.setTypeface(Func.getTypeface(Register.this));
//        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,35);
//        snack.setAction("بله", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                verifyln.setVisibility(View.GONE);
//                mainln.setVisibility(View.VISIBLE);
//            }
//        }) ;
//        TextView snackbar_action = (TextView) view.findViewById(android.support.design.R.id.snackbar_action);
//        snackbar_action.setGravity(Gravity.CENTER);
//        snackbar_action.setTypeface(Func.getTypeface(Register.this));
//        snackbar_action.setTextColor(Color.WHITE);
//        snack.show();

        if (verifyln.getVisibility() == View.VISIBLE && getResources().getBoolean(R.bool.verify_user_by_sms) ) {
            Snackbar snackbar = Snackbar.make(verifyln, "", Snackbar.LENGTH_LONG);
            Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

            TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
            textView.setVisibility(View.INVISIBLE);

            LayoutInflater inflater=this.getLayoutInflater();
            View snackView = inflater.inflate(R.layout.mysnack, null);
// Configure the view
            TextView custom = (TextView) snackView.findViewById(R.id.custom);
            custom.setText("فعال سازی حساب شما هنوز انجام نشده است، " +
                    "\n" +
                    "آیا مطمئن هستید؟");
            custom.setTypeface(typeface2);

            TextView action = (TextView) snackView.findViewById(R.id.action);
            action.setTypeface(typeface2);
            action.setText("بله");
            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyln.setVisibility(View.GONE);
                    mainln.setVisibility(View.VISIBLE);
                }
            });
            layout.addView(snackView, 0);
            snackbar.show();

        }else
            super.onBackPressed();
    }

}
