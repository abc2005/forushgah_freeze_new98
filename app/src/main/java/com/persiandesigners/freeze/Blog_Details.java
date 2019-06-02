package com.persiandesigners.freeze;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.persiandesigners.freeze.Util.CustomProgressDialog;
import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;


/**
 * Created by navid on 12/29/2016.
 */
public class Blog_Details extends AppCompatActivity {
    ImageView img;
    WebView wb;
    CustomProgressDialog customProgressDialog;
    Toolbar toolbar;
    String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blogdetails);

        customProgressDialog = new CustomProgressDialog(this);
        customProgressDialog.show("");


        Bundle bl = getIntent().getExtras();
        title=bl.getString("onvan");
        img = (ImageView) findViewById(R.id.img);
        Glide.with(this).load(getString(R.string.url) + "NewsPictures/" + bl.getString("img")).into(img);
        new Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                Log.v("this", body);
                customProgressDialog.dismiss("");
                if (body.equals("errordade")) {
                    MyToast.makeText(getApplicationContext(), "اشکالی پیش آمده است");
                } else {
                    String stringToAdd = "width=\"100%\"";
                    StringBuilder sb = new StringBuilder(body);

                    int i = 0;
                    int cont = 0;
                    while (i != -1) {
                        i = body.indexOf("src", i + 1);
                        if (i != -1) sb.insert(i + (cont * stringToAdd.length()), stringToAdd);
                        ++cont;
                    }


                    WebView webview = (WebView) findViewById(R.id.wb);
                    WebSettings settings = webview.getSettings();
                    settings.setDefaultTextEncodingName("utf-8");
                    settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
                    settings.setSaveFormData(false);
                    settings.setSupportZoom(false);
                    settings.setBuiltInZoomControls(false);
                    webview.loadDataWithBaseURL(null, "<html><head><style>@font-face { font-family: myface;src: url(\"file:///android_asset/yekan.ttf\");" +
                                    "BODY, HTML {background: transparent;  }img{display: inline; height: auto;width: 100% !important;} body,div {  font-family: 'myface';} </style></head><body><div style='text-align: justify; line-height: 25px;width:99%' dir='rtl'>"
                                    + sb.toString() + "</div></body></html>",
                            "text/html", "utf-8", null);

                }
            }
        }, true, Blog_Details.this, "").execute(getString(R.string.url) + "/getBlogDetails.php?id=" + bl.getString("id"));


        actionbar();
    }


    private void actionbar() {
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        action.MakeActionBar(title);
        Func.checkSabad(Blog_Details.this);
    }
}
