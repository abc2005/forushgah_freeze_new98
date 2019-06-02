package com.persiandesigners.freeze;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.OnTaskFinished;

/**
 * Created by navid on 12/29/2016.
 */
public class Blog extends AppCompatActivity {
    RecyclerView blog;
    private int previousTotal = 0;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    LinearLayoutManager mLayoutManager;
    Boolean  loadmore = true,taskrunning;
    int page = 0, in, to;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog);

        declrea();
		actionbar(); 
        new Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                if (body.equals("errordade")) {

                } else {
                    TahlilData(body);
                }
            }
        }, true, Blog.this, "").execute(getString(R.string.url) + "/getBlog.php");
    }

    private void TahlilData(String body) {
        Blog_Adapter adapter=new Blog_Adapter(Blog.this,Func.ParseBlog(body));
        blog.setAdapter(adapter);
    }

    private void declrea() {
        blog = (RecyclerView) findViewById(R.id.blog);
        blog.setLayoutManager(new LinearLayoutManager(this));
    }
	
	private void actionbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        action.MakeActionBar("مطالب");
        Func.checkSabad(Blog.this);

        ImageView img_sabad = (ImageView) findViewById(R.id.img_sabad);
        img_sabad.setVisibility(View.GONE);
        TextView tvnumsabad = (TextView) findViewById(R.id.text_numkharid);
        tvnumsabad.setVisibility(View.GONE);
    }
}
