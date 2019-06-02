package com.persiandesigners.freeze;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
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
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by navid on 6/4/2016.
 */
public class Login extends AppCompatActivity {
    Toolbar toolbar;
    Typeface typeface2;
    EditText mobile, pass, verify;
    TextView login, register, forgetPass;
    TextInputLayout mobile_l, pass_l, verify_l;
    Boolean log_reg_by_sms = false, verify_user_by_sms = false, registerOnFpage;
    LinearLayout mainln, verifyln;
    Boolean run = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        declare();
        actionbar();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Login.this, Register.class);
                if (getIntent() != null && getIntent().getExtras() != null)
                    in.putExtras(getIntent().getExtras());
                startActivity(in);
                if (registerOnFpage == false)
                    finish();
            }
        });
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText txtUrl = new EditText(getApplicationContext());
                txtUrl.setGravity(Gravity.RIGHT);
                txtUrl.setTextColor(Color.BLACK);
      
				 String forgettext="شماره همراه یا آدرس ایمیل را وارد کنید";
                if(getResources().getBoolean(R.bool.reg_email)==false)
                    forgettext="شماره همراه را وارد کنید";

                AlertDialog dialog = new AlertDialog.Builder(Login.this, R.style.MyAlertDialogStyle)
                        .setMessage(forgettext)
                        .setView(txtUrl)
                        .setPositiveButton(("ارسال"), new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int whichButton) {

                                long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
                                new Html(new OnTaskFinished() {
                                    @Override
                                    public void onFeedRetrieved(String body) {
                                        Log.v("this", "As" + body);
                                        if (body.equals("errordade")) {
                                            MyToast.makeText(getApplicationContext(), "اشکالی پیش آمده است");
                                        } else if (body.contains("ok")) {
                                            MyToast.makeText(getApplicationContext(), "کدبازیابی ارسال شد");
                                            startActivity(new Intent(Login.this, ForgetPass.class));
                                            dialog.dismiss();
                                        } else if (body.contains("err"))
                                            MyToast.makeText(Login.this, getString(R.string.nouser));
                                    }
                                }, true, Login.this, "").execute(getString(R.string.url) + "/getForgetPassword.php?us=" + txtUrl.getText().toString() + "&n=" + number);
                                Log.v("this", getString(R.string.url) + "/getForgetPassword.php?us=" + txtUrl.getText().toString() + "&n=" + number);

                            }
                        })
                        .setNegativeButton(("لغو"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        })
                        .show();

                Typeface typeface= Func.getTypeface(Login.this);
                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                textView.setTypeface(typeface);

                Button btn1 = (Button) dialog.findViewById(android.R.id.button1);
                btn1.setTypeface(typeface);

                Button btn2 = (Button) dialog.findViewById(android.R.id.button2);
                btn2.setTypeface(typeface);

            }
        });
    }

    private void submit() {
        mobile_l.setErrorEnabled(false);
        pass_l.setErrorEnabled(false);
        if (mobile.getText().toString().trim().length() != 11) {
            mobile_l.setErrorEnabled(true);
            mobile_l.setError(getString(R.string.wrong_mobile));
            mobile.requestFocus();
        } else if (pass.getText().length() < 4 && log_reg_by_sms == false) {
            pass_l.setErrorEnabled(true);
            pass_l.setError(getString(R.string.wrong_pass));
            pass.requestFocus();
        } else {
            String more = "";
            if (log_reg_by_sms)
                more = "&log_reg_by_sms=true";
            if (verify_user_by_sms)
                more = "&activeBySms=true";

            long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
            new Html(new OnTaskFinished() {
                @Override
                public void onFeedRetrieved(String body) {
                    Log.v("this", body);
                    if (body.equals("errordade")) {
                        MyToast.makeText(Login.this, "اشکالی پیش آمده است");
                    } else if (body.contains("notFoundss"))
                        MyToast.makeText(Login.this, "شماره همراه یا رمز عبور اشتباه است");
                    else if (body.length() > 0 && body.contains("id")) {
                        if (log_reg_by_sms) {
                            dialogCode(body);
                        } else {
                            getLogin(body);
                        }
                    }
                }
            }, true, Login.this, "").execute(getString(R.string.url) + "/getLogin.php?e="
                    + mobile.getText().toString().trim() + "&p=" + pass.getText().toString().trim() + "&n=" + number + more);
            Log.v("this", getString(R.string.url) + "/getLogin.php?e="
                    + mobile.getText().toString().trim() + "&p=" + pass.getText().toString().trim() + "&n=" + number + more);
        }
    }

    private void getLogin(String body) {
        try {
            JSONObject response = new JSONObject(body);
            JSONArray posts = response.optJSONArray("contacts");
            JSONObject post = posts.optJSONObject(0);
            SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
            SharedPreferences.Editor pref = settings.edit();
            pref.putString("mobile", mobile.getText().toString());
            pref.putString("p", post.optString("p"));
            pref.putString("uid", post.optString("id"));
            pref.putString("name", post.optString("name"));
            pref.putString("adres", post.optString("address"));
            if (post.optString("code_posti") != null) {
                Log.v("this", "ss");
                pref.putString("tel", post.optString("tel"));

                pref.putString("codeposti", post.optString("code_posti"));
            }
            pref.commit();

            startActivity(new Intent(Login.this, Home.class));

            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dialogCode(final String body) {
        Func func = new Func(Login.this);
        func.MakeCountDown();
		
		final TextView  countdown = (TextView) findViewById(R.id.countdown);
        TextView sendagain=(TextView)findViewById(R.id.sendagain);
        sendagain.setTypeface(typeface2);
        sendagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout ln = (LinearLayout) findViewById(R.id.lnmain);
                if(countdown.getText().toString().equals("0")){
                    submit();
                }else{
                    Snackbar.make(ln, "لطف تا 0 شدن زمان شکیبا باشید", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        verifyln.setVisibility(View.VISIBLE);
        mainln.setVisibility(View.GONE);

        verify.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (verify.getText().toString().length() == 5 && run == false) {
                    run = true;
                    CheckCode(body, verify.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void CheckCode(final String bodys, String mabda) {
        long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        new Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                Log.v("this", body);
                if (body.equals("errordade")) {
                    MyToast.makeText(getApplicationContext(), "اشکالی پیش آمده است");
                } else if (body.contains("ok")) {
                    getLogin(bodys);
                } else {
                    MyToast.makeText(Login.this, "کد وارد شده اشتباه است");
                    dialogCode(bodys);
                    run = false;
                }
            }
        }, true, Login.this, "").execute(getString(R.string.url) + "/getActiveUser.php?code=" + mabda + "&n=" + number + "&fromLogin=true");
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(android.Manifest.permission.RECEIVE_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.v("this", "granted");
                } else {
                    ActivityCompat.requestPermissions(Login.this, new String[]{android.Manifest.permission.RECEIVE_SMS}, 2);
                    Log.v("this", "need");
                }
            }
        }
    }

    private void declare() {
        registerOnFpage = getResources().getBoolean(R.bool.registerOnFpage);
        log_reg_by_sms = getResources().getBoolean(R.bool.login_register_by_sms_no_pass_needed);
        if (log_reg_by_sms) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(android.Manifest.permission.RECEIVE_SMS)
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.v("this", "granted");
                    } else {
                        ActivityCompat.requestPermissions(Login.this, new String[]{android.Manifest.permission.RECEIVE_SMS}, 2);
                        Log.v("this", "need");
                    }
                }

            }
        }
        verify_user_by_sms = getResources().getBoolean(R.bool.verify_user_by_sms);
        typeface2 = Typeface.createFromAsset(getAssets(), "IRAN Sans.ttf");

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
        pass = (EditText) findViewById(R.id.password);
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass_l.setErrorEnabled(false);
            }
        });

        verify = (EditText) findViewById(R.id.verifycode);
        verify.setTypeface(typeface2);

        login = (TextView) findViewById(R.id.login);
        login.setTypeface(typeface2);
        register = (TextView) findViewById(R.id.register);
        register.setTypeface(typeface2);
        forgetPass = (TextView) findViewById(R.id.forgetPass);
        forgetPass.setTypeface(typeface2);

        mobile_l = (TextInputLayout) findViewById(R.id.input_layout_mobile);
        mobile_l.setTypeface(typeface2);
        pass_l = (TextInputLayout) findViewById(R.id.input_layout_pass);
        pass_l.setTypeface(typeface2);
        pass.setTypeface(typeface2);

        if (log_reg_by_sms) {
            forgetPass.setVisibility(View.GONE);
            pass_l.setVisibility(View.GONE);
			 pass.setVisibility(View.GONE);
			TextInputLayout il_pass=(TextInputLayout)findViewById(R.id.il_pass);
            il_pass.setVisibility(View.GONE);

            register.setVisibility(View.GONE);
        }

        verify_l = (TextInputLayout) findViewById(R.id.input_layout_verify);
        verify_l.setTypeface(typeface2);
        mainln = (LinearLayout) findViewById(R.id.mainln);
        verifyln = (LinearLayout) findViewById(R.id.veriftyln);

        TextView tvverify = (TextView) findViewById(R.id.tvverify);
        tvverify.setTypeface(typeface2);
    }

    private String addPadding(String t, String s, int num) {
        StringBuilder retVal;

        if (null == s || 0 >= num) {
            throw new IllegalArgumentException("Don't be silly");
        }

        if (s.length() <= num) {
            //String to small, do nothing
            return s;
        }

        retVal = new StringBuilder(s);

        for (int i = retVal.length(); i > 0; i -= num) {
            retVal.insert(i, t);
        }
        return retVal.toString();
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
        if (registerOnFpage) {
            ImageView back = (ImageView) findViewById(R.id.back);
            back.setVisibility(View.INVISIBLE);
            ImageView imgsearch = (ImageView) findViewById(R.id.imgsearch);
            imgsearch.setVisibility(View.INVISIBLE);
        }
    }
}

