package com.persiandesigners.freeze;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by navid on 3/1/2017.
 */
public class Vijegi extends AppCompatActivity {
    Toolbar toolbar;
    Typeface typeface;
    Bundle bl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vijegi);

        WebView tvvijegi = (WebView) findViewById(R.id.tvvijegi);
        typeface=Typeface.createFromAsset(this.getAssets(), "IRAN Sans Bold.ttf");
        bl=getIntent().getExtras();

        CardView cv = (CardView) findViewById(R.id.card_vijegi);
        cv.setVisibility(View.VISIBLE);
        String sHtmlTemplate = "<html><head>" +
                "<style>img{max-width:100%; margin:0 auto}, body{text-align:right; line-height:28px; direction:rtl}</style>" +
                "</head><body  dir='rtl' style='line-height:30px'>" +
                bl.getString("data") +
                "</body></html>";
//        tvvijegi.loadDataWithBaseURL(null, sHtmlTemplate, "text/html", "utf-8", null);
//        tvvijegi.loadUrl(getString(R.string.url)+"getJoziyat.php?id="+bl.getInt("id"));

        tvvijegi.loadUrl(getString(R.string.url) + "products_extFlds.php?pidd="+bl.getInt("id"));
        Log.v("this", getString(R.string.url) + "products_extFlds.php?pidd="+bl.getInt("id"));


        actionbar();
    }

    private void actionbar() {
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        action.MakeActionBar("");
        Func.checkSabad(Vijegi.this);
    }
}
