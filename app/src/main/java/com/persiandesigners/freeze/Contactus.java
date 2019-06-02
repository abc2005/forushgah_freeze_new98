package com.persiandesigners.freeze;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.persiandesigners.freeze.Util.HtmlPost;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.NetworkAvailable;
import com.persiandesigners.freeze.Util.OnTaskFinished;

public class Contactus extends AppCompatActivity {
    EditText email, msg,tel;
    TextView tv;
    Button bt;
    Boolean goterror = false;
    ProgressDialog ringProgressDialog;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactus);

        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "yekan.ttf");

        Button btcall = (Button) findViewById(R.id.btcall);
        btcall.setTypeface(typeface);
        btcall.setText("تماس تلفنی");
        if (!getString(R.string.url).contains("babaarzooni"))
            btcall.setVisibility(View.GONE);
        btcall.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", getString(R.string.callNumber), null)));
            }
        });

        tel = (EditText) findViewById(R.id.tel);
        tel.setTypeface(typeface);
        tel.setHint(Html.fromHtml("<small>شماره تماس</small>"));

        email = (EditText) findViewById(R.id.editText1);
        email.setTypeface(typeface);
        email.setHint(Html.fromHtml("<small>" + (getString(R.string.email)) + "</small>"));
        msg = (EditText) findViewById(R.id.editText2);
        msg.setTypeface(typeface);
        msg.setHint(Html.fromHtml("<small>متن پیام</small>"));

        bt = (Button) findViewById(R.id.button1);
        bt.setTypeface(typeface);
        tv = (TextView) findViewById(R.id.textView1);
        tv.setText(("جهت تماس با ما میتوانید فرم زیر را تکمیل کنید"));
        tv.setTypeface(typeface);
        bt.setText(("ارسال پیام"));
        bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkAvailable.isNetworkAvailable(Contactus.this))
                    MyToast.makeText(Contactus.this, ("دسترسی به اینترنت برقرار نمیباشد"));
                else {
                    if (tel.length() !=11)
                        MyToast.makeText(Contactus.this, ("شماره تماس 11 رقمی وارد کنید"));
                    else if (msg.length() < 6)
                        MyToast.makeText(Contactus.this, ("متن پیام کوتاه است"));
                    else {
                        sendEmail();
                    }
                }
            }
        });
        actionbar();
        getContactBody();
    }

    private void getContactBody() {
        new com.persiandesigners.freeze.Util.Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                Log.v("this", body);
                if (body.equals("errordade")) {
                    MyToast.makeText(getApplicationContext(), "اشکالی پیش آمده است");
                }else{
                    try {
                        tv.setText(Html.fromHtml((
                                body+ "\n"+
                                        "جهت تماس با ما میتوانید فرم زیر را تکمیل کنید")));
                    } catch (Exception e) {
                        tv.setText(Html.fromHtml(
                                body+ "\n"+
                                        "جهت تماس با ما میتوانید فرم زیر را تکمیل کنید"));
                    }
                }
            }
        }, true, Contactus.this, "").execute(getString(R.string.url) + "/getConctactUsBody.php");
    }

    private void sendEmail() {
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("email", email.getText().toString())
                .appendQueryParameter("msg", msg.getText().toString())
                .appendQueryParameter("tel", tel.getText().toString())
                .appendQueryParameter("come", "app");
        String query = builder.build().getEncodedQuery();

        new HtmlPost(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                Log.v("this", body);
                if (body.equals("errordade")) {
                    MyToast.makeText(getApplicationContext(), "اشکالی پیش آمده است");
                } else if (body.equals("ok")) {
                    MyToast.makeText(Contactus.this, ("پیام با موفقیت ارسال شد"));
                    startActivity(new Intent(Contactus.this, Home.class));
                    finish();
                } else if (body.equals("err"))
                    MyToast.makeText(Contactus.this, getString(R.string.problem));
            }
        }, true, Contactus.this, "", query).execute(getString(R.string.url) + "/getSendMail.php");
    }

    private void actionbar() {
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        action.MakeActionBar(getString(R.string.contactus));

        ImageView img_sabad = (ImageView)findViewById(R.id.img_sabad);
        img_sabad.setVisibility(View.GONE);
        TextView tvnumsabad = (TextView) findViewById(R.id.text_numkharid);
        tvnumsabad.setVisibility(View.GONE);
    }
}
