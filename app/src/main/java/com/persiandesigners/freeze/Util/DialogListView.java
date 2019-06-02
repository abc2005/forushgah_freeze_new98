package com.persiandesigners.freeze.Util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.persiandesigners.freeze.Func;
import com.persiandesigners.freeze.R;

import java.util.ArrayList;


public class DialogListView {
    private Context ctx;
    private String title;
    private String []items;
    private Drawable icon;
    private Activity act;
    private Typeface typeface;
    private OnTaskFinished onOurTaskFinished;
    private EditText et_search;

    public DialogListView(OnTaskFinished onTaskFinished,Context ctx, String[] items, Drawable icon,String title) {
        this.onOurTaskFinished = onTaskFinished;
        this.ctx=ctx;
        this.items=items;
        this.icon=icon;
        this.act=(Activity)ctx;
        this.title=title;
        typeface= Func.getTypeface(act);
        makeIt();
    }
    public DialogListView(OnTaskFinished onTaskFinished, Context ctx, ArrayList<String> items, Drawable icon, String title) {
        this.onOurTaskFinished = onTaskFinished;
        this.ctx=ctx;
        String rows[]=new String[items.size()];
        for (int i=0;i<items.size();i++){
            rows[i]=items.get(i);
        }
        this.items=rows;
        this.icon=icon;
        this.act=(Activity)ctx;
        this.title=title;
        typeface=Func.getTypeface(act);
        makeIt();
    }

    private void makeIt() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(act);
        LayoutInflater inflater = act.getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.dialog_list_ln, null);

        TextView tv=(TextView) convertView.findViewById(R.id.ostan_shahrestan);
        tv.setTypeface(typeface);
        tv.setText(title);

        ImageView dialoglist_icon=(ImageView)convertView.findViewById(R.id.dialoglist_icon);
        if(icon==null)
            dialoglist_icon.setVisibility(View.GONE);
        else
            dialoglist_icon.setImageDrawable(icon);

        alertDialog.setView(convertView);
        final ListView lv = (ListView) convertView.findViewById(R.id.lv);

        final Lv_adapter adapter=new Lv_adapter(act,items);
        lv.setAdapter(adapter);

//        final EditText et_search=(convertView).findViewById(R.id.et_search);
//        if(items.length==0)
//            et_search.setVisibility(View.GONE);
//        et_search.setTypeface(typeface);
//        et_search.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                String s=et_search.getText().toString() ;
//                if(s.length()==0){
//                    lv.setAdapter(adapter);
//                }else{
//                    ArrayList<String>searchItems=new ArrayList<>();
//                    for (int i=0;i<items.length;i++){
//                        if(items[i].contains(s)){
//                            searchItems.add(items[i]);
//                        }
//                    }
//                    String Srows[]=new String[searchItems.size()];
//                    for (int i=0;i<searchItems.size();i++){
//                        Srows[i]=searchItems.get(i);
//                    }
//                    Lv_adapter SearchAdapter=new Lv_adapter(act,Srows);
//                    lv.setAdapter(SearchAdapter);
//                }
//            }
//        });

        final AlertDialog alert = alertDialog.create();
        alert.show();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onOurTaskFinished.onFeedRetrieved(String.valueOf(position));
                alert.dismiss();
            }
        });
    }
}
