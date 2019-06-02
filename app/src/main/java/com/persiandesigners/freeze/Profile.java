package com.persiandesigners.freeze;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.persiandesigners.freeze.Util.CustomProgressDialog;
import com.persiandesigners.freeze.Util.Html;
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
public class Profile extends AppCompatActivity {
    Toolbar toolbar;
    Typeface typeface2;
    EditText mobile, pass, repass, user, email, moaref, oldpass;
    TextView register;
    TextInputLayout mobile_l, pass_l, repass_lpass_l, user_l, email_l, moaref_l, oldpass_l;
    ProgressDialog pDialog;
    CustomProgressDialog customProgressDialog;
    TextInputLayout tel_l, adres_l, codeposti_l;
    EditText tel, adress, codeposti;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        Bundle bl = getIntent().getExtras();
        if (bl != null && bl.getString("for") != null && bl.getString("for").equals("pass")) {
            LinearLayout lnpass = (LinearLayout) findViewById(R.id.lnpass);
            lnpass.setVisibility(View.VISIBLE);
            LinearLayout profile = (LinearLayout) findViewById(R.id.lnprofile);
            profile.setVisibility(View.GONE);
        }

        declare();
        new Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                Log.v("this", body);
                if (body.equals("errordade")) {
                    MyToast.makeText(Profile.this, "اشکالی پیش آمده است");
                } else if (body.contains("notFoundss"))
                    MyToast.makeText(Profile.this, "کاربری با اطلاعات وارد شده یافت نشد");
                else if (body.length() > 0 && body.contains("id")) {
                    try {
                        JSONObject response = new JSONObject(body);
                        JSONArray posts = response.optJSONArray("contacts");
                        JSONObject post = posts.optJSONObject(0);

                        mobile.setText(post.optString("mobile"));
                        user.setText(post.optString("name"));
                        email.setText(post.optString("email"));

                        tel.setText(post.optString("phone"));
                        adress.setText(post.optString("address"));
                        codeposti.setText(post.optString("postalcode"));
                        moaref.setText(post.optString("moaref"));

                        customProgressDialog.dismiss("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, true, Profile.this, "").execute(getString(R.string.url) + "/getLogin2.php?e=" + Func.getUid(Profile.this));
        Log.v("this", getString(R.string.url) + "/getLogin2.php?e=" + Func.getUid(Profile.this));

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
        oldpass_l.setErrorEnabled(false);
        repass_lpass_l.setErrorEnabled(false);
        if (mobile.getText().length() != 11) {
            mobile_l.setErrorEnabled(true);
            mobile_l.setError(getString(R.string.wrong_mobile));
            mobile.requestFocus();
        } else if (user.getText().toString().trim().length() < 3) {
            user_l.setErrorEnabled(true);
            user_l.setError(getString(R.string.wrong_name));
            user.requestFocus();
        } else if (oldpass.getText().length() < 4 && oldpass.getText().length() > 0) {
            oldpass_l.setErrorEnabled(true);
            oldpass_l.setError("رمز عبور قبلی اشتباه است");
            oldpass_l.requestFocus();
        } else if (repass.getText().length() < 4 && pass.getText().length() > 0) {
            repass_lpass_l.setErrorEnabled(true);
            repass_lpass_l.setError(getString(R.string.wrong_pass));
            repass.requestFocus();
        } else if (pass.getText().length() > 0 && !pass.getText().toString().equals(repass.getText().toString())) {
            repass_lpass_l.setErrorEnabled(true);
            repass_lpass_l.setError(getString(R.string.noeqalpass));
            repass.requestFocus();
        } else {
            new submitForm().execute();
        }
    }

    private class submitForm extends AsyncTask<String, String, String> {

        Boolean goterr = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Profile.this);
            pDialog.setMessage(("در حال ارسال اطلاعات"));
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... params) {
            String responseBody = "";
            try {
                URL url = new URL(getString(R.string.url) + "/getEditProfile.php?v=khordad");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(20000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("uid", settings.getString("uid", "0"))
                        .appendQueryParameter("e", mobile.getText().toString().trim())
                        .appendQueryParameter("p", pass.getText().toString().trim())
//                        .appendQueryParameter("old_p",settings.getString("p",""))
                        .appendQueryParameter("old_p", oldpass.getText().toString().trim())
                        .appendQueryParameter("name", user.getText().toString())
                        .appendQueryParameter("em", email.getText().toString().trim())

                        .appendQueryParameter("adres", adress.getText().toString().trim())
                        .appendQueryParameter("codposti", codeposti.getText().toString().trim())
                        .appendQueryParameter("tel", tel.getText().toString().trim());
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

            }
            return responseBody;
        }

        @Override
        protected void onPostExecute(String body) {
            super.onPostExecute(body);

            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();

            if (goterr == false && !isCancelled()) {
                Log.v("this", "22 " + body);
                if (body.equals("errordade")) {
                    MyToast.makeText(Profile.this, "اشکالی پیش آمده است");
                } else if (body.contains("userExsist")) {
                    mobile_l.setError(getString(R.string.mobile_exist));
                    mobile.requestFocus();
                } else if (body.contains("wrongPass")) {
                    oldpass_l.setError("رمز عبور قبلی وارد شده اشتباه است");
                    oldpass.requestFocus();
                } else if (body.length() > 0 && body.contains("id")) {
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
                        pref.commit();

                        MyToast.makeText(Profile.this, "ویرایش پروفایل با موفقیت انجام شد");
                        startActivity(new Intent(Profile.this, Home.class));
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.v("this", e.getMessage());
                    }
                }
            } else if (goterr == true)
                MyToast.makeText(Profile.this, ("اشکالی به وجود آمده است . مجددا سعی کنید"));

        }
    }


    private void declare() {
        customProgressDialog = new CustomProgressDialog(this);
        customProgressDialog.show("");

        typeface2 = Typeface.createFromAsset(getAssets(), "IRAN Sans.ttf");
        mobile = (EditText) findViewById(R.id.mobile);
        mobile.setTypeface(typeface2);
		        if(getResources().getBoolean(R.bool.verify_user_by_sms))
            mobile.setEnabled(false);
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
        oldpass = (EditText) findViewById(R.id.old_password);
        oldpass.setTypeface(typeface2);
        oldpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                oldpass_l.setErrorEnabled(false);
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
        repass = (EditText) findViewById(R.id.repass);
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
                if (!pass.getText().toString().equals(repass.getText().toString())) {
                    repass_lpass_l.setErrorEnabled(true);
                    repass_lpass_l.setError(getString(R.string.noeqalpass));
                    repass.requestFocus();
                } else {
                    repass_lpass_l.setErrorEnabled(false);
                }
            }
        });


        user = (EditText) findViewById(R.id.user);
        user.setTypeface(typeface2);
        email = (EditText) findViewById(R.id.email);
        email.setTypeface(typeface2);
        moaref = (EditText) findViewById(R.id.moaref);
        moaref.setTypeface(typeface2);

        register = (TextView) findViewById(R.id.register);
        register.setTypeface(typeface2);

        mobile_l = (TextInputLayout) findViewById(R.id.input_layout_mobile);
        mobile_l.setTypeface(typeface2);
        pass_l = (TextInputLayout) findViewById(R.id.input_layout_pass);
        pass_l.setTypeface(typeface2);
        oldpass_l = (TextInputLayout) findViewById(R.id.input_layout_oldpass);
        oldpass_l.setTypeface(typeface2);

        repass_lpass_l = (TextInputLayout) findViewById(R.id.input_layout_repass);
        repass_lpass_l.setTypeface(typeface2);
        user_l = (TextInputLayout) findViewById(R.id.input_layout_user);
        user_l.setTypeface(typeface2);
        email_l = (TextInputLayout) findViewById(R.id.input_layout_email);
        email_l.setTypeface(typeface2);
        moaref_l = (TextInputLayout) findViewById(R.id.input_layout_moaref);
        moaref_l.setTypeface(typeface2);
  

        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        tel = (EditText) findViewById(R.id.tel);
        tel.setTypeface(typeface2);
        tel.setText(settings.getString("tel", ""));
        adress = (EditText) findViewById(R.id.adress);
        adress.setTypeface(typeface2);
        adress.setText(settings.getString("adres", ""));
        codeposti = (EditText) findViewById(R.id.codeposti);
        codeposti.setTypeface(typeface2);
        codeposti.setText(settings.getString("codeposti", ""));

        tel_l = (TextInputLayout) findViewById(R.id.input_layout_tel);
        tel_l.setTypeface(typeface2);
        mobile_l = (TextInputLayout) findViewById(R.id.input_layout_mobile);
        mobile_l.setTypeface(typeface2);
        adres_l = (TextInputLayout) findViewById(R.id.input_layout_adress);
        adres_l.setTypeface(typeface2);
        codeposti_l = (TextInputLayout) findViewById(R.id.input_layout_codeposti);
        codeposti_l.setTypeface(typeface2);

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


        register.setText("ذخیره");
    }

    private void actionbar() {
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        action.MakeActionBar(getString(R.string.editprofile));


        ImageView img_sabad = (ImageView) findViewById(R.id.img_sabad);
        img_sabad.setVisibility(View.GONE);
        TextView tvnumsabad = (TextView) findViewById(R.id.text_numkharid);
        tvnumsabad.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }
}
