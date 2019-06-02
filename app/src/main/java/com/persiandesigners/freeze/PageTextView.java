package com.persiandesigners.freeze;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;

/**
 * Created by navid on 10/15/2017.
 */
public class PageTextView extends AppCompatActivity {
    String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_textview);

        Bundle bl = getIntent().getExtras();
        title = bl.getString("title");
        actionbar();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "IRAN Sans.ttf");
        final TextView textf = (TextView) findViewById(R.id.text);
        textf.setTypeface(typeface);

        long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        new Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                Log.v("this", body);
                if (body.equals("errordade")) {
                    MyToast.makeText(getApplicationContext(), "اتصال اینترنت را بررسی کنید");
                }else{
                    textf.setText(android.text.Html.fromHtml(body));
                }
            }
        }, true, this, "").execute(bl.getString("url")+"&n="+number);
    }

    private void actionbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        action.MakeActionBar(title);
        Func.checkSabad(PageTextView.this);
    }
}
