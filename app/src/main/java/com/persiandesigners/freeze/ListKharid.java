package com.persiandesigners.freeze;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.persiandesigners.freeze.Util.CustomProgressDialog;
import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.HtmlPost;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListKharid extends android.support.v4.app.Fragment {
    View v;
    CustomProgressDialog customProgressDialog;
    Typeface typeface;
    ListView lv;
    EditText et_listkharid;
    ArrayList<SingleRow> list;
    myAdapter adapter;
    TextView tv_search_listkharid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.list_kharid, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        declare();
        actionbar();
    }

    private void declare() {
        typeface = Func.getTypeface(getActivity());
        list = new ArrayList<SingleRow>();

        et_listkharid = (EditText) v.findViewById(R.id.et_listkharid);
        et_listkharid.setTypeface(typeface);

        lv = (ListView) v.findViewById(R.id.lv_listkharid);
        et_listkharid.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i== EditorInfo.IME_ACTION_DONE){
                    addIt();
                    return true;
                }
                return false;
            }
        });

        ImageView img_listkharid_plus = (ImageView) v.findViewById(R.id.img_listkharid_plus);
        img_listkharid_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addIt();
            }
        });


        TextView tv_listkharid = (TextView) v.findViewById(R.id.tv_listkharid);
        if (Func.getUid(getActivity()).equals("0")) {
            tv_listkharid.setTypeface(typeface);
            tv_listkharid.setText("تنها کاربران عضو میتوانند به این بخش دسترسی داشته باشند");
        } else {
            tv_listkharid.setVisibility(View.GONE);
            customProgressDialog = new CustomProgressDialog(getActivity());
            customProgressDialog.show("");

            long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
            new Html(new OnTaskFinished() {
                @Override
                public void onFeedRetrieved(String body) {
                    Log.v("this", body);
                    if (body.equals("errordade")) {
                        MyToast.makeText(getActivity(), "اتصال اینترنت را بررسی کنید");
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(body);
                            JSONArray jsonArray = jsonObject.optJSONArray("contacts");

                            if (jsonArray != null && jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject row = jsonArray.optJSONObject(i);
                                    String titles = row.optString("name");
                                    String ides = row.optString("id");
                                    list.add(new SingleRow(titles, ides));
                                }
                                adapter = new myAdapter(getActivity());
                                lv.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    customProgressDialog.dismiss("");
                }
            }, true, getActivity(), "").execute(getString(R.string.url) + "/getListKharid.php?n=" + number + "&w=getList&uid=" + Func.getUid(getActivity()));
        }

        tv_search_listkharid = (TextView) v.findViewById(R.id.tv_search_listkharid);
        tv_search_listkharid.setTypeface(typeface);
        tv_search_listkharid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list.size() == 0)
                    MyToast.makeText(getActivity(), "لیست خرید خالی است");
                else {
                    startActivity(new Intent(getActivity(), ListKharidItems.class));
                }
            }
        });
    }

    private void addIt() {
        if (et_listkharid.getText().toString().length() >= 2) {
            long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("name", et_listkharid.getText().toString());
            String query = builder.build().getEncodedQuery();
            new HtmlPost(new OnTaskFinished() {
                @Override
                public void onFeedRetrieved(String body) {
                    Log.v("this", body);
                    if (body.contains("err")) {
                        MyToast.makeText(getActivity(),body.replace("#err",""));
                    } else {
                        list.add(new SingleRow(et_listkharid.getText().toString(), body));
                        if (adapter == null) {
                            adapter = new myAdapter(getActivity());
                            lv.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                    et_listkharid.setText("");
                }
            }, true, getActivity(), "", query).execute(getString(R.string.url) + "/getListKharid.php?w=add&uid=" + Func.getUid(getActivity()));
        } else
            MyToast.makeText(getActivity(), "عنوان کالا را وارد کنید");
    }

    class SingleRow {
        String title;
        String id;

        SingleRow(String title, String id) {
            this.title = title;
            this.id = id;
        }
    }

    class myAdapter extends BaseAdapter {
        Context context;

        myAdapter(Context c) {
            context = c;
        }

        @Override
        public int getCount() {
            return list.size();  // baraye moshakhas kardan site BaseAdapter
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            // optimize nashode hast ,
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.listkhadamat_row, parent, false);

            SingleRow rowItem = list.get(position);

            TextView title = (TextView) row.findViewById(R.id.tv_listkharid_row);
            title.setTypeface(typeface);
            title.setText(rowItem.title);

            final ImageView img_listkharid_del = (ImageView) row.findViewById(R.id.img_listkharid_del);
            img_listkharid_del.setTag(rowItem.id);
            img_listkharid_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder a = new AlertDialog.Builder(getActivity());
                    a.setMessage(("آیا از حذف این لیست خرید مطمئن هستید؟"));
                    a.setPositiveButton(("بله"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
                            new Html(new OnTaskFinished() {
                                @Override
                                public void onFeedRetrieved(String body) {
                                    Log.v("this", body);
                                    list.remove(position);
                                    notifyDataSetChanged();
                                }
                            }, false, getActivity(), "").execute(getString(R.string.url) + "/getListKharid.php?n=" + number + "&uid=" + Func.getUid(getActivity()) + "&id=" + img_listkharid_del.getTag().toString() + "&w=del");
                        }
                    });
                    a.setNegativeButton(("نه"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
                    AlertDialog dialog = a.show();
                    TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                    messageText.setGravity(Gravity.RIGHT);
                    messageText.setTypeface(typeface);

                    Button btn1 = (Button) dialog.findViewById(android.R.id.button1);
                    btn1.setTypeface(typeface);

                    Button btn2 = (Button) dialog.findViewById(android.R.id.button2);
                    btn2.setTypeface(typeface);
                }
            });
            return row;
        }

    }

    private void actionbar() {
        Func action = new Func(getActivity());
        action.MakeActionBar("لیست خرید");

        Func.checkSabad(getActivity());
        ImageView img_sabad = (ImageView) v.findViewById(R.id.img_sabad);
        img_sabad.setVisibility(View.GONE);
        TextView tvnumsabad = (TextView) v.findViewById(R.id.text_numkharid);
        tvnumsabad.setVisibility(View.GONE);

        Func.checkSabad(getActivity());
        v.findViewById(R.id.back).setVisibility(View.INVISIBLE);
        v.findViewById(R.id.imgsearch).setVisibility(View.INVISIBLE);
    }
}
