package com.persiandesigners.freeze;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class Page extends AppCompatActivity {
    WebView wb;
    String forWhat = "";
    Boolean goterror = false;
    ProgressDialog pDialog;
    Typeface typeface;
    Toolbar toolbar;
    Bundle bl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page);

        declare();

        bl = getIntent().getExtras();
        if (bl != null) {
            forWhat = bl.getString("w");
            if (Func.isDizbon(Page.this) == false) {
                if (forWhat.equals("aboutus") && !getString(R.string.url).contains("isfhyper")) {
                    TextView tvdesigner = (TextView) findViewById(R.id.tvdesigner);
                    tvdesigner.setVisibility(View.VISIBLE);
                    tvdesigner.setTypeface(typeface);
                    tvdesigner.setText(("طراحی اپلیکیشن اندروید Persian-Designers.ir"));
                    tvdesigner.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://persian-designers.ir"));
                            startActivity(browserIntent);
                        }
                    });
                }
            }
            wb.loadUrl(getString(R.string.url)+"getPage.php?page=" + forWhat);
//            long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
//            new Html(new OnTaskFinished() {
//                @Override
//                public void onFeedRetrieved(String result) {
//                    if (result.equals("errordade")) {
//                        MyToast.makeText(getApplicationContext(), "اشکالی پیش آمده است");
//                    }else{
//                        try {
//                            wb.loadData(
//                                    "<html><body><style>@font-face { font-family: 'myface';src: url('file:///android_asset/yekan.ttf');" +
//                                            "BODY, HTML {background: transparent;  } body {  font-family: 'myface', serif;} </style><div style='text-align: justify; line-height: 23px;float:right' dir='rtl'>"
//                                            + (result) + "</div></body></html>",
//                                    "text/html; charset=utf-8", "UTF-8");
//                        } catch (Exception e) {
//                            wb.loadData(
//                                    "<html><body><style>@font-face { font-family: 'myface';src: url('file:///android_asset/yekan.ttf');" +
//                                            "BODY, HTML {background: transparent;  } body {  font-family: 'myface', serif;} </style><div style='text-align: justify; line-height: 23px;float:right' dir='rtl'>"
//                                            + (result) + "</div></body></html>",
//                                    "text/html; charset=utf-8", "UTF-8");
//                        }
//                    }
//                }
//            }, true, Page.this, "").execute("http://fast-markets.com/demo//getPage.php?page=" + forWhat);
        }
        actionbar();
    }

    private void declare() {
        wb = (WebView) findViewById(R.id.webView1);
        WebSettings settings = wb.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setSaveFormData(false);
        settings.setSupportZoom(false);
        int fontsize = 14;
        if (fontsize > 20) fontsize = 13;
        settings.setDefaultFontSize(fontsize);
        settings.setBuiltInZoomControls(false);
        typeface = Typeface.createFromAsset(this.getAssets(), "yekan.ttf");

    }

    private void actionbar() {
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        if (bl.getString("onvan") != null)
            action.MakeActionBar(bl.getString("onvan"));
        else
            action.MakeActionBar("");
        Func.checkSabad(Page.this);

        ImageView img_sabad = (ImageView) findViewById(R.id.img_sabad);
        img_sabad.setVisibility(View.GONE);
        TextView tvnumsabad = (TextView) findViewById(R.id.text_numkharid);
        tvnumsabad.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        } catch (Exception e) {
        }
        finish();
    }
}
