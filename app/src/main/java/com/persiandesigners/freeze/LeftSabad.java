package com.persiandesigners.freeze;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.persiandesigners.freeze.Util.DatabaseHandler;
import com.persiandesigners.freeze.Util.MyToast;

/**
 * Created by Navid on 4/11/2018.
 */

public class LeftSabad {
    Context ctx;
    Activity act;
    Typeface typeface;
    DatabaseHandler db;
    ListView lv;
    String urlimg;
    TimeListAdapter adapter;
    TextView tv_lsabad_jamkol, tv_lsabad_takhfif, tv_lsabad_jam ;

    public LeftSabad(Activity act) {
        this.ctx = act;
        this.act = act;
        
        declare();
    }

    private void makeSabad() {
        Cursor cursor = db.getSabadkharid();
        adapter = new TimeListAdapter(act, cursor);
        lv.setAdapter(adapter);
    }

    public class TimeListAdapter extends CursorAdapter {
        public TimeListAdapter(Context context, Cursor c) {
            super(context, c);
        }

        public class ViewHolder {
            TextView tvTitle, gheymat, tvtedad, gheymat_balatar;
            ImageView img, plus, minus,del;

            public ViewHolder(View row) {
                tvTitle = (TextView) row.findViewById(R.id.title);
                tvTitle.setTypeface(typeface);
                tvtedad = (TextView) row.findViewById(R.id.num);
                tvtedad.setTypeface(typeface);
                del = (ImageView) row.findViewById(R.id.delete);
                gheymat = (TextView) row.findViewById(R.id.tv_price);
                gheymat.setTypeface(typeface);
                gheymat_balatar = (TextView) row.findViewById(R.id.tv_price2);
                gheymat_balatar.setTypeface(typeface);
                gheymat_balatar.setPaintFlags(gheymat_balatar.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                img = (ImageView) row.findViewById(R.id.img);
                plus = (ImageView) row.findViewById(R.id.plus);
                minus = (ImageView) row.findViewById(R.id.mines);
            }
        }

        @Override
        public View newView(Context context, Cursor arg1, ViewGroup arg2) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.sabad_row_left, arg2, false);
            TimeListAdapter.ViewHolder holder = new TimeListAdapter.ViewHolder(row);
            row.setTag(holder);
            return row;
        }

        @Override
        public void bindView(View v, Context context, Cursor c) {
            final TimeListAdapter.ViewHolder holder = (TimeListAdapter.ViewHolder) v.getTag();
            holder.tvtedad.setText(c.getInt(4) + "");
            holder.plus.setTag(c.getInt(0));
            holder.plus.setTag(-1,c.getInt(5));//maxcount
            holder.minus.setTag(c.getInt(0));
            holder.plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String Id = holder.plus.getTag().toString();
                    int n=db.getNumProd(Id) + 1;
                    int max=Integer.parseInt(holder.plus.getTag(-1).toString());
                    if(n<=max){
                        db.updateTedad(n, Id);
                        adapter.updateUI();
                        calTop();
                    }else
                        MyToast.makeText(act,"محصول بیشتر از این تعداد موجود نیست");
                }
            });
            holder.minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String Id = holder.minus.getTag().toString();
                    int n=db.getNumProd(Id) - 1;
                    if(n>0){
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
                public void onClick(View v) {
                    db.deleteSabad(holder.del.getTag().toString());
                    if (db.getcountsabad() == 0) {
                        declare();
                    } else {
                        adapter.updateUI();
                        calTop();
                    }
                }
            });

            int num=c.getInt(4);
            int numOmde=c.getInt(9);
            String prices=c.getString(3);
            if(numOmde>0 && num>=numOmde){
                prices=c.getString(10);
            }

            int price = c.getInt(4) * Integer.parseInt(prices);


            holder.gheymat.setText(Func.getCurrency(prices) + act.getString(R.string.toman));
            if(c.getString(6).length()>0)
                holder.gheymat_balatar.setText(Func.getCurrency(c.getString(9) + "") + act.getString(R.string.toman));
            else
                holder.gheymat_balatar.setText("");

            if(!c.getString(2).contains("Opitures"))
                Glide.with(act).load(urlimg+"Opitures/"+ c.getString(2)).into(holder.img);
            else
                Glide.with(act).load(urlimg+ c.getString(2)).into(holder.img);
        }

        private void updateUI() {
            swapCursor(db.getSabadkharid());
            //notifyDataSetChanged();
        }

    }

    private void calTop() {
        tv_lsabad_jamkol.setText(Func.getCurrency(db.getSabadMablaghKol())+ " تومان");
        tv_lsabad_takhfif.setText(Func.getCurrency(db.getJamTakhfifha()+"")+ " تومان");
        tv_lsabad_jam.setText(Func.getCurrency(db.getSabadMablaghKolWithTakhfif()+"")+ " تومان");
    }

    private void declare() {
        db = new DatabaseHandler(act);
        db.open();
        typeface = Func.getTypeface(act);

        urlimg = act.getString(R.string.url);
        lv = (ListView) act.findViewById(R.id.list_lsabad);

        tv_lsabad_jamkol=(TextView)act.findViewById(R.id.tv_lsabad_jamkol);
        if(tv_lsabad_jamkol!=null) {
            tv_lsabad_jamkol.setTypeface(typeface);
            tv_lsabad_takhfif = (TextView) act.findViewById(R.id.tv_lsabad_takhfif);
            tv_lsabad_takhfif.setTypeface(typeface);
            tv_lsabad_jam = (TextView) act.findViewById(R.id.tv_lsabad_jam);
            tv_lsabad_jam.setTypeface(typeface);

            TextView lsabad_empty = (TextView) act.findViewById(R.id.lsabad_empty);
            if (db.getcountsabad() == 0) {
                lsabad_empty.setTypeface(typeface);
                lsabad_empty.setVisibility(View.VISIBLE);
            } else {
                lsabad_empty.setVisibility(View.GONE);
                TextView tv_lsabad_tv = (TextView) act.findViewById(R.id.tv_lsabad_tv);
                tv_lsabad_tv.setTypeface(typeface);
                TextView tv_lsabad_jamkol_tv = (TextView) act.findViewById(R.id.tv_lsabad_jamkol_tv);
                tv_lsabad_jamkol_tv.setTypeface(typeface);
                TextView tv_lsabad_takhfif_tv = (TextView) act.findViewById(R.id.tv_lsabad_takhfif_tv);
                tv_lsabad_takhfif_tv.setTypeface(typeface);
                TextView tv_lsabad_jam_tv = (TextView) act.findViewById(R.id.tv_lsabad_jam_tv);
                tv_lsabad_jam_tv.setTypeface(typeface);

                TextView tv_lsabad_taied = (TextView) act.findViewById(R.id.tv_lsabad_taied);
                tv_lsabad_taied.setTypeface(typeface);
                tv_lsabad_taied.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (db.getcountsabad() == 0)
                            MyToast.makeText(act, "سبد خرید خالی است");
                        else {
                            Intent in = new Intent(act, Home.class);
                            in.putExtra("sabad", "true");
                            act.startActivity(in);
                        }
                    }
                });

                makeSabad();
                calTop();
            }
        }
    }
}
