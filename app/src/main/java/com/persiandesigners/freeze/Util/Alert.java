package com.persiandesigners.freeze.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.persiandesigners.freeze.Func;
import com.persiandesigners.freeze.R;

/**
 * Created by navid on 12/6/2017.
 */
public class Alert extends Dialog {
    private String message;
    private String title;
    private String btYesText;
    private String btNoText;
    private int icon=0;
    private View.OnClickListener btYesListener=null;
    private View.OnClickListener btNoListener=null;
    Typeface typeface;
    Context ctx;
    public String positiveBgColor="#3A5475";
    public String negativeBgColor="#FF4045";
    public String positiveTextColor="#ffffff";
    public String negativeTextColor="#ffffff";
    public String MessageTextColor="#3F5B69";


    public Alert(Context context) {
        super(context);
        ctx=context;
    }

    public Alert(Context context, int themeResId) {
        super(context, themeResId);
        ctx=context;
    }

    protected Alert(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        ctx=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        setContentView(R.layout.customdialog);

        typeface= Func.getTypeface((Activity) ctx);

        TextView tvmessage = (TextView) findViewById(R.id.onvan);
        tvmessage.setTypeface(typeface);
        tvmessage.setTextColor(Color.parseColor(MessageTextColor));
        tvmessage.setText(getMessage());

        Button btYes = (Button) findViewById(R.id.positve);
        btYes.setTypeface(typeface);
        btYes.setTextColor(Color.parseColor(positiveTextColor));
        btYes.setBackgroundColor(Color.parseColor(positiveBgColor));

        Button btNo = (Button) findViewById(R.id.negative);
        btNo.setTypeface(typeface);
        btNo.setTextColor(Color.parseColor(negativeTextColor));
        btNo.setBackgroundColor(Color.parseColor(negativeBgColor));

        if(btYesText==null || btYesText.length()==0)
            btYes.setVisibility(View.GONE);
        btYes.setText(btYesText);

        if(btNoText==null || btNoText.length()==0)
            btNo.setVisibility(View.GONE);
        btNo.setText(btNoText);

        btYes.setOnClickListener(btYesListener);
        btNo.setOnClickListener(btNoListener);

    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public void setPositveButton(String yes, View.OnClickListener onClickListener) {
        dismiss();
        this.btYesText = yes;
        this.btYesListener = onClickListener;


    }

    public void setNegativeButton(String no, View.OnClickListener onClickListener) {
        dismiss();
        this.btNoText = no;
        this.btNoListener = onClickListener;


    }
}
