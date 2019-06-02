package com.persiandesigners.freeze;

import android.graphics.Typeface;
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
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;

import java.util.List;

/**
 * Created by Navid on 6/4/2018.
 */

public class MsgBox extends AppCompatActivity{//get notifications
    Typeface typeface;
    TextView loading,loadmoretv;
    RecyclerView rc_msgbox;
    Boolean loadmore = false,taskrunning;
    LinearLayoutManager lnmanager;
    MsgBoxAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msgbox);

        declare();
        getData();
        actionbar();
    }

    private void getData() {
        taskrunning=true;
        long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        new Html(new OnTaskFinished()
        {
            @Override
            public void onFeedRetrieved(String body)
            {
                loading.setVisibility(View.GONE);
                if(body.equals("errordade")){
                    MyToast.makeText(MsgBox.this,getString(R.string.problem));
                }else{
                    TahlilData(body);
                    taskrunning=false;
                }
            }
        },false,MsgBox.this,"").execute(getString(R.string.url)+"/getNotifications.php");

    }

    private void TahlilData(String body) {
        List items = Func.ParseMsgBox(body);
        if (items != null) {
            if (items.size() <20)
                loadmore = false;
            else
                loadmore=true;

            if(adapter==null){
                adapter=new MsgBoxAdapter(MsgBox.this,items);
                rc_msgbox.setAdapter(adapter);

                if(items.size()==0){
                    loading.setVisibility(View.VISIBLE);
                    loading.setText("موردی یافت نشد....");
                }
            }else{
                adapter.addAll(items);
            }
        }else{
            loading.setVisibility(View.VISIBLE);
            loading.setText("موردی یافت نشد....");
        }

        loading.setVisibility(View.GONE);
        loadmoretv.setVisibility(View.GONE);
    }



    private void declare() {
        typeface=Typeface.createFromAsset(this.getAssets(), "IRAN Sans Bold.ttf");
        loading=(TextView)findViewById(R.id.loading);
        loading.setTypeface(typeface);
        loadmoretv=(TextView)findViewById(R.id.loadmoretv);
        loadmoretv.setTypeface(typeface);

        rc_msgbox=(RecyclerView)findViewById(R.id.rc_msgbox);
        lnmanager=new LinearLayoutManager(this) ;
        rc_msgbox.setLayoutManager(lnmanager);

    }

    private void actionbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        action.MakeActionBar("پیامهای مدیر");
        Func.checkSabad(MsgBox.this);

        ImageView img_sabad = (ImageView) findViewById(R.id.img_sabad);
        img_sabad.setVisibility(View.GONE);
        TextView tvnumsabad = (TextView) findViewById(R.id.text_numkharid);
        tvnumsabad.setVisibility(View.GONE);
    }
}
