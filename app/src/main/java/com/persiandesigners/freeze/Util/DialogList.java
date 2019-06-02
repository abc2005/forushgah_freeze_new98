package com.persiandesigners.freeze.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.persiandesigners.freeze.Func;
import com.persiandesigners.freeze.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class DialogList {
    Context context;
    LinkedHashMap<String, Integer> Items;
    Dialog dialog;
    int intface;

    public DialogList(Context context, LinkedHashMap<String, Integer> items, int intface) {
        this.context = context;
        Items = items;
        this.intface = intface;
    }

    public void makeDialog() {
        dialog = new Dialog(context, R.style.DialogStyler);
        dialog.setContentView(R.layout.dialog_list);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ListView lvDialogList = (ListView) dialog.findViewById(R.id.lv_dialog_list);
        MyListViewAdapter adapter = new MyListViewAdapter(context, Items);
        lvDialogList.setAdapter(adapter);
        lvDialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (intface) {
//                    case Constant.Khadamat:
//                        ((MainInterface) context).KhadamatSelected(i);
//                        break;
                }

            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private class MyListViewAdapter extends BaseAdapter {
        private Typeface tf;
        private final ArrayList mData;

        public MyListViewAdapter(Context context, LinkedHashMap<String, Integer> items) {
            mData = new ArrayList();
            mData.addAll(items.entrySet());
            this.tf = Func.getTypeface((Activity)context);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Map.Entry<String, Integer> getItem(int position) {
            return (Map.Entry) mData.get(position);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View result;

            if (convertView == null) {
                result = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_list_row, parent,
                        false);
            } else {
                result = convertView;
            }

            Map.Entry<String, Integer> item = getItem(position);
            TextView tv = (TextView) result.findViewById(R.id.tv_dialog_list);
            tv.setTypeface(tf);
            tv.setText(item.getKey());

            ImageView img = (ImageView) result.findViewById(R.id.img_dialog_list);
            if (item.getValue() == 0) {
                img.setVisibility(View.GONE);
            } else {
                img.setVisibility(View.VISIBLE);
                img.setImageDrawable(ContextCompat.getDrawable(context, (item.getValue())));
            }

            return result;
        }
    }
}
