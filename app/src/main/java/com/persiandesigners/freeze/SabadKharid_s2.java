package com.persiandesigners.freeze;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.persiandesigners.freeze.Util.Alert;
import com.persiandesigners.freeze.Util.DatabaseHandler;
import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;

public class SabadKharid_s2 extends Fragment {
    Toolbar toolbar;
    Typeface typeface2;
    TimeListAdapter adapter;
    ListView lv;
    TextView jamKharid, takmil;
    DatabaseHandler db;
    String urlimg;
    View view;
    Boolean isBazaryab=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sabadkharids2, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        declare();
        actionbar();

        Cursor cursor = db.getSabadkharid();
        adapter = new TimeListAdapter(getActivity(), cursor);
        lv.setAdapter(adapter);

        takmil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int j = Integer.parseInt(db.getSabadMablaghKolWithTakhfif());

                if (getResources().getBoolean(R.bool.only_users_can_buy)
                        && Func.getUid(getActivity()).equals("0")) {

                    LinearLayout ln = (LinearLayout)v.findViewById(R.id.ln);
                    Snackbar.make(ln, "جهت تکمیل سفارش، ابتدا وارد شوید", Snackbar.LENGTH_LONG)
                            .setAction("ورود|عضویت", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(getActivity(), Login.class));
                                }
                            }).setActionTextColor(Color.WHITE).show();
                } else if (Func.isMultiSeller(getActivity())) {
                    new Html(new OnTaskFinished() {
                        @Override
                        public void onFeedRetrieved(String body) {
                            Log.v("this", body);
                            if (body.equals("errordade")) {
                                MyToast.makeText(getActivity(), "اشکالی پیش آمده است");
                            } else if (body.equals("ok")) {
//                                if(Func.getUid(getActivity()).equals("0")){
//                                    Intent in = new Intent(getActivity(), SabadKharid_s1.class);
//                                    startActivity(in);
//                                }else {
//                                    Intent in = new Intent(getActivity(), SabadAddress.class);
//                                    startActivity(in);
//                                }
                                Intent in = new Intent(getActivity(), SabadKharid_s1.class);
                                startActivity(in);
                            } else if (body.contains("#err")) {
                                AlertDialog.Builder a = new AlertDialog.Builder(getActivity());
                                a.setMessage(body.replace("#err", ""));
                                a.setPositiveButton(("باشه"), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog dialog = a.show();
                                TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                                messageText.setGravity(Gravity.RIGHT);
                                messageText.setTypeface(typeface2);

                                Button btn1 = (Button) dialog.findViewById(android.R.id.button1);
                                btn1.setTypeface(typeface2);

                                Button btn2 = (Button) dialog.findViewById(android.R.id.button2);
                                btn2.setTypeface(typeface2);
                            }
                        }
                    }, true, getActivity(), "در حال دریافت اطلاعات....").execute(getString(R.string.url) + "getJobs.php?job=minOrder&jam=" + j + "&shopId=" + Func.getShopId(getActivity()));
                } else {
                    if(Func.getUid(getActivity()).equals("0") || isBazaryab){
                        Intent in = new Intent(getActivity(), SabadKharid_s1.class);
                        startActivity(in);
                    }else {
                        Intent in = new Intent(getActivity(), SabadAddress.class);
                        startActivity(in);
                    }
                }

            }
        });

//        long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
//        new Html(new OnTaskFinished() {
//            @Override
//            public void onFeedRetrieved(String body) {
//                Log.v("this", "len " + body.length());
//                if (body.length() > 2 && !body.contains("errordade")) {
//                    AlertDialog.Builder a = new AlertDialog.Builder(getActivity());
//                    a.setMessage(body);
//                    a.setCancelable(false);
//                    a.setPositiveButton(("بستن"), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            startActivity(new Intent(getActivity(), FistActiivty.class));
//                            finish();
//                        }
//                    });
//                    AlertDialog dialog = a.show();
//                    TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
//                    messageText.setGravity(Gravity.RIGHT);
//                    messageText.setTypeface(typeface2);
//
//                }
//            }
//        }, false, getActivity(), "").execute(getString(R.string.url) + "/getIsShopClose.php?n=" + number);

    }

    public class TimeListAdapter extends CursorAdapter {
        public TimeListAdapter(Context context, Cursor c) {
            super(context, c);
        }

        public class ViewHolder {
            TextView tvTitle, gheymat, tvtedad, del, gheymatkol;
            ImageView img, plus, minus;

            public ViewHolder(View row) {
                tvTitle = (TextView) row.findViewById(R.id.title);
                tvTitle.setTypeface(typeface2);
                tvtedad = (TextView) row.findViewById(R.id.tvtedad);
                tvtedad.setTypeface(typeface2);
                del = (TextView) row.findViewById(R.id.del);
                del.setTypeface(typeface2);
                gheymat = (TextView) row.findViewById(R.id.gheymat);
                gheymat.setTypeface(typeface2);
                gheymatkol = (TextView) row.findViewById(R.id.gheymatkol);
                gheymatkol.setTypeface(typeface2);
                img = (ImageView) row.findViewById(R.id.img);
                plus = (ImageView) row.findViewById(R.id.plus);
                minus = (ImageView) row.findViewById(R.id.minus);
            }
        }

        @Override
        public View newView(Context context, Cursor arg1, ViewGroup arg2) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.sabad_row, arg2, false);
            ViewHolder holder = new ViewHolder(row);
            row.setTag(holder);
            return row;
        }

        @Override
        public void bindView(final View v, Context context, Cursor c) {
            final ViewHolder holder = (ViewHolder) v.getTag();
            holder.tvtedad.setText(c.getInt(4) + "");
            holder.plus.setTag(c.getInt(0));
            holder.plus.setTag(-1, c.getInt(5));//maxcount
            holder.minus.setTag(c.getInt(0));
            holder.plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String Id = holder.plus.getTag().toString();
                    int n = db.getNumProd(Id) + 1;
                    int max = Integer.parseInt(holder.plus.getTag(-1).toString());
                    if (n <= max) {
                        db.updateTedad(n, Id);
                        adapter.updateUI();
                        calTop();
                    } else
                        MyToast.makeText(getActivity(), "محصول بیشتر از این تعداد موجود نیست");
                }
            });
            holder.minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String Id = holder.minus.getTag().toString();
                    int n = db.getNumProd(Id) - 1;
                    if (n > 0) {
                        db.updateTedad(n, Id);
                        adapter.updateUI();
                        calTop();
                    }

                }
            });

            holder.tvTitle.setText(c.getString(1));
            holder.del.setTag(c.getInt(0));
            holder.del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vs) {
                    db.deleteSabad(holder.del.getTag().toString());
                    if (db.getcountsabad() == 0) {
                        LinearLayout noitem = (LinearLayout)view.findViewById(R.id.noitem);
                        noitem.setVisibility(View.VISIBLE);
                        TextView tvnoitem = (TextView)view.findViewById(R.id.tvnoitem);
                        tvnoitem.setTypeface(typeface2);
                    } else {
                        adapter.updateUI();
                        calTop();
                    }
                }
            });

            int num = c.getInt(4);
            int numOmde = c.getInt(9);
            String prices = c.getString(3);
            if (numOmde > 0 && num >= numOmde) {
                prices = c.getString(10);
            }

            int price = c.getInt(4) * Integer.parseInt(prices);


            holder.gheymat.setText(Func.getCurrency(prices) + getString(R.string.toman));
            if(isBazaryab){
                holder.gheymat.setTag(c.getInt(0));
                holder.gheymat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
                        final EditText txtUrl = new EditText(getActivity());
                        txtUrl.setGravity(Gravity.RIGHT);
                        txtUrl.setTypeface(typeface2);
                        new AlertDialog.Builder(getActivity())
                              .setMessage(("قیمت جدید را وارد کنید")).setView(txtUrl)
                              .setPositiveButton(("تایید"), new DialogInterface.OnClickListener() {
                                 public void onClick(DialogInterface dialog, int whichButton) {
                                    String mabda = txtUrl.getText().toString();
                                    if (mabda.length() > 0) {
                                        db.updatePrice(holder.gheymat.getTag().toString(),mabda);
                                        dialog.dismiss();
                                        updateUI();
                                        calTop();
                                    }
                                 }
                              }).setNegativeButton(("بیخیال"), new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int whichButton) {
                              dialog.dismiss();
                                      InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                      imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                           }
                        }).show();
                    }
                });
            }
            holder.gheymatkol.setText(Func.getCurrency(price + "") + getString(R.string.toman));

            if (!c.getString(2).contains("Opitures"))
                Glide.with(getActivity()).load(urlimg + "Opitures/" + c.getString(2)).into(holder.img);
            else
                Glide.with(getActivity()).load(urlimg + c.getString(2)).into(holder.img);
        }

        private void updateUI() {
            swapCursor(db.getSabadkharid());
            //notifyDataSetChanged();
        }

    }

    private void calTop() {
        jamKharid.setText(Func.getCurrency(db.getSabadMablaghKolWithTakhfif()) + getString(R.string.toman));
    }

    private void declare() {
        urlimg = getString(R.string.url);

        isBazaryab=Func.getIsBazaryab(getActivity());
        typeface2 = Func.getTypeface(getActivity());

        lv = (ListView)view.findViewById(R.id.list);
        jamKharid = (TextView)view.findViewById(R.id.jamkharid);
        jamKharid.setTypeface(typeface2);
        takmil = (TextView)view.findViewById(R.id.takmil);
        takmil.setTypeface(typeface2);

        TextView tvjamkharid = (TextView)view.findViewById(R.id.tvjamkharid);
        tvjamkharid.setTypeface(typeface2);

        db = new DatabaseHandler(getActivity());
        if (!db.isOpen())
            db.open();

        db.check();

        if (db.getcountsabad() == 0) {
            LinearLayout noitem = (LinearLayout)view.findViewById(R.id.noitem);
            noitem.setVisibility(View.VISIBLE);
            TextView tvnoitem = (TextView)view.findViewById(R.id.tvnoitem);
            tvnoitem.setTypeface(typeface2);
        }

        calTop();
//        ImageView deleteall=(ImageView)findViewById(R.id.deleteall);
//        deleteall.setVisibility(View.VISIBLE);
//        deleteall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder a = new AlertDialog.Builder(getActivity());
//                a.setMessage("آیا از حذف کالاهای خود از سبد خرید اطمینان دارید؟");
//                a.setPositiveButton(("بله"), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        db.clearSabadKharid();
//                        onBackPressed();
//                    }
//                });
//                a.setNegativeButton(("انصراف"),  new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//
//                    }
//                });
//
//                AlertDialog dialog = a.show();
//                TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
//                messageText.setGravity(Gravity.RIGHT);
//                messageText.setTypeface(typeface2);
//
//            }
//        });

        TextView removeall = (TextView)view.findViewById(R.id.removeall);
        removeall.setTypeface(typeface2);
        removeall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vs) {
                final Alert mAlert = new Alert(getActivity(), R.style.mydialog);
                mAlert.setIcon(android.R.drawable.ic_dialog_alert);
                mAlert.setMessage("ایا از حذف کلیه اجناس سبد خرید مطمئن هستید؟");
                mAlert.setPositveButton("بله", new View.OnClickListener() {
                    @Override
                    public void onClick(View views) {
                        mAlert.dismiss();
                        db.clearSabadKharid();
                        LinearLayout noitem = (LinearLayout)view.findViewById(R.id.noitem);
                        noitem.setVisibility(View.VISIBLE);
                        TextView tvnoitem = (TextView)view.findViewById(R.id.tvnoitem);
                        tvnoitem.setTypeface(typeface2);
                    }
                });

                mAlert.setNegativeButton("خیر", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAlert.dismiss();
                    }
                });

                mAlert.show();

//                AlertDialog.Builder a = new AlertDialog.Builder(getActivity());
//                a.setMessage("ایا از حذف کلیه اجناس سبد خرید مطمئن هستید؟");
//                a.setPositiveButton(("بله"), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        db.clearSabadKharid();
//                        startActivity(new Intent(getActivity(),FistActiivty.class));
//                        finish();
//                    }
//                });
//                a.setNegativeButton(("نه"),  new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//
//                AlertDialog dialog = a.show();
//                TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
//                messageText.setGravity(Gravity.RIGHT);
//                messageText.setTypeface(typeface2);
            }
        });
    }

    private void actionbar() {

        Func action = new Func(getActivity());
        action.MakeActionBar(getString(R.string.sabadkhrid));
        action.hideSabadKharidIcon();
        ;
//        action.hideSearch();

        ImageView imgSearch = (ImageView)view.findViewById(R.id.imgsearch);
        imgSearch.setVisibility(View.GONE);
        view.findViewById(R.id.back).setVisibility(View.INVISIBLE);
    }

}
