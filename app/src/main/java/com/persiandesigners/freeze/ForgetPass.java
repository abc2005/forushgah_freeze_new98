package com.persiandesigners.freeze;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.persiandesigners.freeze.Util.HtmlPost;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;

/**
 * Created by navid on 12/2/2017.
 */
public class ForgetPass extends AppCompatActivity {
    Toolbar toolbar;
    Typeface typeface2;
    TextInputLayout pass_l, code_l, repass_l;
    EditText code, pass, re_pass;
    Button submit;
    Runnable r = null;
    Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgetpass);

        actionbar();
        declare();

        handler = new Handler();
        r = new Runnable() {
            public void run() {
                Log.v("this", "run");
                SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
                if (settings.getString("code", "0").length() > 1) {
                    code.setText(settings.getString("code", "0"));
                    try {
                        if (handler != null)
                            handler.removeCallbacks(r);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                handler.postDelayed(this, 500);
            }
        };
        handler.postDelayed(r, 500);

    }

    private void submit() {
        pass_l.setErrorEnabled(false);
        code_l.setErrorEnabled(false);
        repass_l.setErrorEnabled(false);
        if (code.getText().toString().length() < 4) {
            code_l.setErrorEnabled(true);
            code_l.setError("کد صحیح وارد کنید");
            code.requestFocus();
        } else if (pass.getText().length() < 4) {
            pass_l.setErrorEnabled(true);
            pass_l.setError(getString(R.string.wrong_pass));
            pass.requestFocus();
        } else if (re_pass.getText().length() < 4) {
            repass_l.setErrorEnabled(true);
            repass_l.setError(getString(R.string.wrong_repass));
            re_pass.requestFocus();
        } else if (!re_pass.getText().toString().equals(pass.getText().toString())) {
            repass_l.setErrorEnabled(true);
            repass_l.setError("رمز عبور و تکرار رمز عبور مشابه نیستند");
            re_pass.requestFocus();
        } else {
            long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("code", code.getText().toString())
                    .appendQueryParameter("pass", pass.getText().toString());
            String query = builder.build().getEncodedQuery();

            new HtmlPost(new OnTaskFinished() {
                @Override
                public void onFeedRetrieved(String body) {
                    Log.v("this", body + "Sd");
                    if (body.equals("errordade")) {
                        MyToast.makeText(getApplicationContext(), "اتصال اینترنت را بررسی کنید");
                    } else if (body.equals("ok")) {
                        startActivity(new Intent(ForgetPass.this, Login.class));
                        MyToast.makeText(ForgetPass.this, "رمز عبور با موفقیت ویرایش شد");
                        finish();
                    } else if (body.equals("wrong"))
                        MyToast.makeText(ForgetPass.this, "کد وارد شده اشتباه است");
                }
            }, true, ForgetPass.this, "", query).execute(getString(R.string.url) + "/updatePass.php?n=" + number);
        }
    }

    private void declare() {
        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor pref = settings.edit();
        pref.putString("code", "0");
        pref.commit();

        typeface2 = Typeface.createFromAsset(getAssets(), "IRAN Sans.ttf");
        pass_l = (TextInputLayout) findViewById(R.id.input_layout_pass);
        pass_l.setTypeface(typeface2);
        repass_l = (TextInputLayout) findViewById(R.id.re_input_layout_pass);
        repass_l.setTypeface(typeface2);
        code_l = (TextInputLayout) findViewById(R.id.ln_code);
        code_l.setTypeface(typeface2);

        pass = (EditText) findViewById(R.id.password);
        pass.setTypeface(typeface2);
        re_pass = (EditText) findViewById(R.id.repassword);
        re_pass.setTypeface(typeface2);
        code = (EditText) findViewById(R.id.code);
        code.setTypeface(typeface2);

        submit = (Button) findViewById(R.id.submit);
        submit.setTypeface(typeface2);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {

            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(android.Manifest.permission.RECEIVE_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.v("this", "granted");
                } else {
                    ActivityCompat.requestPermissions(ForgetPass.this, new String[]{android.Manifest.permission.RECEIVE_SMS}, 2);
                    Log.v("this", "need");
                }
            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(android.Manifest.permission.RECEIVE_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.v("this", "granted");
                } else {
                    ActivityCompat.requestPermissions(ForgetPass.this, new String[]{android.Manifest.permission.RECEIVE_SMS}, 2);
                    Log.v("this", "need");
                }
            }
        }
    }


    private void actionbar() {
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        action.MakeActionBar(getString(R.string.login));

        ImageView img_sabad = (ImageView) findViewById(R.id.img_sabad);
        img_sabad.setVisibility(View.GONE);
        TextView tvnumsabad = (TextView) findViewById(R.id.text_numkharid);
        tvnumsabad.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (handler != null)
                handler.removeCallbacks(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
