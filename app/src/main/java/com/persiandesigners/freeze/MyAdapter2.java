package com.persiandesigners.freeze;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.transition.TransitionManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.persiandesigners.freeze.Util.DatabaseHandler;
import com.persiandesigners.freeze.Util.MyAdapterCallBack;
import com.persiandesigners.freeze.Util.MyToast;

import java.util.List;

/**
 * Created by navid on 8/31/2015.
 */
public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder> implements View.OnClickListener {
    private LayoutInflater inflater;
    private List<FeedItem> list;
    Typeface typeface, typeface2;
    Context ctx;
    Boolean fullWidth = false;
    DatabaseHandler dbSabad;
    Boolean rowList = false, multiSeller=false;
    int posGreen = -1;
    Boolean HideSabadSection, show_details_instead_open;
    private ActionMode actionMode;

    public MyAdapter2(Context context, List<FeedItem> feedItemList) {
        if (context != null) {
            inflater = LayoutInflater.from(context);
            this.list = feedItemList;
            typeface = Typeface.createFromAsset(context.getAssets(), "IRAN Sans.ttf");
            typeface2 = Typeface.createFromAsset(context.getAssets(), "IRAN Sans.ttf");
            ctx = context;
            openDb();

            show_details_instead_open=context.getResources().getBoolean(R.bool.show_details_instead_open);
            if (Func.getIsNemayeshgahi(context).equals("1"))
                HideSabadSection = true;
            else
                HideSabadSection = false;

            if(context.getResources().getBoolean(R.bool.has_options))
                HideSabadSection=true;

            multiSeller=ctx.getResources().getBoolean(R.bool.multiseller);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (actionMode == null ? 0 : 1);
    }


    private void openDb() {
        dbSabad = new DatabaseHandler(ctx);
        if (!dbSabad.isOpen())
            dbSabad.open();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parrent, int viewType) {
        View view = null;
        view = inflater.inflate(R.layout.homepage_parts2, parrent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    public void setActionMode(ActionMode actionMode) {
        this.actionMode = actionMode;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int position) {
        final FeedItem item = list.get(position);
        viewHolder.name.setText(item.getname());

        String price=item.getprice() ;
        if(dbSabad.isAddedSabad(item.getid()) && item.getOmde_num()!=0){
            int numInSabad=dbSabad.getNumProd(item.getid());
            if(numInSabad>=item.getOmde_num()){
                price=item.getOmde_price()+"";
            }else{
                price=item.getprice()+"";
            }
        }
        try {
            if (item.getprice().length() > 1)
                viewHolder.price.setText(Func.getCurrency(price) + " تومان");
        } catch (Exception e) {
            if (item.getprice().length() > 1)
                viewHolder.price.setText((price) + "  تومان");
        }
		
		viewHolder.price_off.setText("");
        if (!item.getprice2().equals("0") && item.getprice2().length() > 0) {
            try {
                if(item.getprice2().length()>1)
                    viewHolder.price_off.setText(Func.getCurrency(item.getprice2()) + "  تومان");

                viewHolder.price_off.setVisibility(View.VISIBLE);
                double p1 = Double.parseDouble(price);
                double p2 = 0;
                try {
                    p2 = Double.parseDouble(item.getprice2());
                } catch (NumberFormatException e) {
                    p2 = 0;
                }
                int p = (int) ((p1 / p2) * 100) - 100;
                viewHolder.off.setText((Math.abs(p) + "") + "%");
                viewHolder.off.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            viewHolder.off.setVisibility(View.GONE);
            viewHolder.price_off.setVisibility(View.INVISIBLE);
        }

        viewHolder.plus.setTag(item.getid());
        viewHolder.min.setTag(item.getid());
//        viewHolder.num.setText(item.getTedad());

//        make product box green
//        if(dbSabad.isAddedSabad(item.getid())){
//            viewHolder.frame_bg.setBackgroundResource(R.drawable.card_edge);
//            viewHolder.tadad_ln.setBackgroundResource(R.drawable.card_edge);
//        }else{
//            viewHolder.frame_bg.setBackgroundResource(R.drawable.card_edge_white);
//            viewHolder.tadad_ln.setBackgroundResource(R.drawable.card_edge_white);
//        }

//        if (posGreen == position) {
//            viewHolder.frame_bg.setBackgroundResource(R.drawable.card_edge);
//            viewHolder.tadad_ln.setBackgroundResource(R.drawable.card_edge);
//        } else {
//            viewHolder.frame_bg.setBackgroundResource(R.drawable.card_edge_white);
//            viewHolder.tadad_ln.setBackgroundResource(R.drawable.card_edge_white);
//        }

        viewHolder.goin.setTag(item.getid());
        viewHolder.goin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id=viewHolder.goin.getTag().toString();
                for(int i=0; i<list.size();i++){
                    if(list.get(i).getid().equals(id)){
                        FeedItem item = list.get(i);
                        if(show_details_instead_open) {
                            Intent in = new Intent(ctx, Detailss.class);
                            in.putExtra("productid", item.getid());
                            in.putExtra("name", item.getname());
                            ctx.startActivity(in);
                        }else{
                            OpenDialog(item, i);
                        }

                        break;
                    }
                }
            }
        });

		viewHolder.num.setTag(item.getid());
        viewHolder.num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String prodId=viewHolder.num.getTag().toString();

                final EditText txtUrl = new EditText(ctx);
                txtUrl.setGravity(Gravity.LEFT);
                txtUrl.setTypeface(typeface);
                txtUrl.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                txtUrl.setInputType(InputType.TYPE_CLASS_NUMBER);
                txtUrl.setText(dbSabad.getNumProd(prodId)+"");
                AlertDialog dialog = new AlertDialog.Builder(ctx)
                        .setMessage("تعداد مورد نظر را وارد کنید").setView(txtUrl)
                        .setPositiveButton(("ثبت"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                int pos = 0;
                                String num = txtUrl.getText().toString();
                                for (int i = 0; i < list.size(); i++) {
                                    if (list.get(i).getid().equals(prodId)) {
                                        FeedItem pitem = list.get(i);
                                        int numMojjud = Integer.parseInt(pitem.getnum());
                                        if (numMojjud >= Integer.parseInt(num)) {
                                            list.get(i).setTedad(num);
                                            notifyItemChanged(i);
                                            dbSabad.updateNum(Integer.parseInt(prodId), Integer.parseInt(num));
                                        } else
                                            MyToast.makeText(ctx, "این کالا با تعداد درخواستی شما موجود نیست");
                                        break;
                                    }
                                }

                            }
                        }).setNegativeButton(("بستن"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                            }
                        }).show();

                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                textView.setTypeface(typeface);

                Button btn1 = (Button) dialog.findViewById(android.R.id.button1);
                btn1.setTypeface(typeface);

                Button btn2 = (Button) dialog.findViewById(android.R.id.button2);
                btn2.setTypeface(typeface);
            }
        });

        viewHolder.plus.setOnClickListener(this);
        viewHolder.min.setOnClickListener(this);

        if (!dbSabad.isOpen())
            dbSabad.open();

        viewHolder.imgsabad.setTag(item.getid());
        viewHolder.imgsabad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(viewHolder.lnGroup);

                if(item.getOpen()==false && multiSeller)
                    MyToast.makeText(ctx,"فروشگاه بسته است");
                else if(addSabad(v.getTag().toString())) {
                    v.setVisibility(View.GONE);
                    viewHolder.imgsabad.setVisibility(View.GONE);
                    viewHolder.tadad_ln.setVisibility(View.VISIBLE);
                    viewHolder.num.setText("1");
                }
            }
        });

        if (dbSabad == null)
            openDb();
        if (!dbSabad.isOpen())
            dbSabad.open();

        viewHolder.not.setVisibility(View.GONE);
        if (dbSabad.isAddedSabad(item.getid())) {
            viewHolder.tadad_ln.setVisibility(View.VISIBLE);
            viewHolder.imgsabad.setVisibility(View.GONE);
            viewHolder.num.setText(dbSabad.getNumProd(item.getid()) + "");
        } else {
            viewHolder.tadad_ln.setVisibility(View.GONE);
            viewHolder.imgsabad.setVisibility(View.VISIBLE);
        }


        if (item.getnum().length() == 0 || item.getnum().equals("0") || item.getprice().length()==0) {
            viewHolder.imgsabad.setVisibility(View.GONE);
            viewHolder.not.setVisibility(View.VISIBLE);
            viewHolder.tadad_ln.setVisibility(View.GONE);
            viewHolder.not.setText("محصول بیشتر از این تعداد موجود نیست");
        }

        if(viewHolder.num.getText().toString().equals("0")){
            dbSabad.removeFromSabad(item.getid());
            viewHolder.tadad_ln.setVisibility(View.GONE);
            viewHolder.imgsabad.setVisibility(View.VISIBLE);
        }

        String img = item.getimg();
        if (img.length() > 5)
            Glide.with(ctx).load(ctx.getString(R.string.url) + "Opitures/" + img).into(viewHolder.img);
        else
            viewHolder.img.setImageDrawable(ctx.getResources().getDrawable(R.mipmap.ic_launcher));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.plus:
                ChangeTedad(v.getTag().toString(), "plus");
                break;
            case R.id.min:
                ChangeTedad(v.getTag().toString(), "minus");
                break;
        }
    }

    private void ChangeTedad(String id, String doWhat) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getid().equals(id)) {
                int currentNum = dbSabad.getNumProd(id);
                int numMojjud = Integer.parseInt(list.get(i).getnum());
                if (doWhat.equals("plus")) {
                    currentNum++;
                    if (currentNum <= numMojjud) {
                        list.get(i).setTedad(currentNum + "");
                        dbSabad.updateNum(Integer.parseInt(id), currentNum);
                        try {
                            ((MyAdapterCallBack) ctx).checkSabad();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else
                        MyToast.makeText(ctx, ctx.getString(R.string.notmojud));
                } else {//minus
                    currentNum--;
                    if (currentNum > 0) {
                        list.get(i).setTedad(currentNum + "");
                        dbSabad.updateNum(Integer.parseInt(id), currentNum);
                    } else if (currentNum == 0) {
                        dbSabad.removeFromSabad(id);
                    }
                }
                posGreen = i;
//                notifyItemChanged(i);
                notifyDataSetChanged();
                try {
                    ((MyAdapterCallBack) ctx).checkSabad();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }


    private Boolean addSabad(String prodID) {
        Boolean b=true;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getid().equals(prodID)) {
                FeedItem item = list.get(i);
                int prdId = Integer.parseInt(item.getid());

                if (dbSabad.checkAddedBefore(prdId) <= 0) {
                    Boolean stat=dbSabad.sabadkharid(item.getname(), item.getimg()
                            , Integer.parseInt(item.getid()), item.getprice()
                            , item.getnum(), "1",item.getprice2(),"",item.getCatId(),item.getOmde_num(),item.getOmde_price(), item.getShopId());
                    if(Func.isMultiSeller(ctx) && stat==false){
                        Func.AlertMultipleStore(ctx);
                        return false;
                    }else {
                        posGreen = i;
                        notifyDataSetChanged();
                        try {
                            ((MyAdapterCallBack) ctx).checkSabad();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
//                else
//                    MyToast.makeText(ctx, ctx.getString(R.string.addedBeforeSabad));
            }
        }
        return b;
    }

    @Override
    public int getItemCount() {
        if (list == null)
            return 0;
        else
            return list.size();
    }

    public void addAll(List<FeedItem> catItems) {
		if (catItems != null) {
        if (this.list == null) {
            this.list = catItems;
        } else {
            this.list.addAll(catItems);
        }
        notifyDataSetChanged();
        notifyItemInserted(list.size());
		}
    }

    public void setFullWidth(boolean fullwidth) {
        fullWidth = fullwidth;
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, price, price_off, off, not, num, imgsabad;
        ImageView img, min, plus, barchasb, vije;
        LinearLayout cardView;
        LinearLayout tadad_ln, goin, ln_selling;
        FrameLayout frame_bg;
        ViewGroup lnGroup;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            if (HideSabadSection){
                ln_selling = (LinearLayout) itemView.findViewById(R.id.ln_selling);
                ln_selling.setVisibility(View.GONE);
            }
            lnGroup=(ViewGroup)itemView.findViewById(R.id.card_view);
            name = (TextView) itemView.findViewById(R.id.tvname);
            name.setTypeface(typeface);
            off = (TextView) itemView.findViewById(R.id.off);
            off.setTypeface(typeface2);
            price = (TextView) itemView.findViewById(R.id.tvprice);
            price.setTypeface(typeface2);
            not = (TextView) itemView.findViewById(R.id.tvnot);
            not.setTypeface(typeface2);
            num = (TextView) itemView.findViewById(R.id.tedad);
            num.setTypeface(typeface2);

            img = (ImageView) itemView.findViewById(R.id.img);
            vije = (ImageView) itemView.findViewById(R.id.vije);
            vije.bringToFront();

            barchasb = (ImageView) itemView.findViewById(R.id.barchasb);
            barchasb.bringToFront();
            plus = (ImageView) itemView.findViewById(R.id.plus);
            min = (ImageView) itemView.findViewById(R.id.min);
            imgsabad = (TextView) lnGroup.findViewById(R.id.imageView1);
            imgsabad.setTypeface(typeface2);
            if (fullWidth) {
                cardView = (LinearLayout) itemView.findViewById(R.id.card_view);
                cardView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            tadad_ln = (LinearLayout) lnGroup.findViewById(R.id.tedad_ln);
            goin = (LinearLayout) itemView.findViewById(R.id.goin);
            price_off = (TextView) itemView.findViewById(R.id.tvprice2);
            price_off.setTypeface(typeface2);
            price_off.setPaintFlags(price_off.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            frame_bg = (FrameLayout) itemView.findViewById(R.id.frame_bg);
        }

        @Override
        public void onClick(View v) {
            FeedItem item = list.get(getAdapterPosition());
            if(show_details_instead_open) {
                Intent in = new Intent(ctx, Detailss.class);
                in.putExtra("productid", item.getid());
                in.putExtra("name", item.getname());
                ctx.startActivity(in);
            }else{
                OpenDialog(item, getAdapterPosition());
            }
        }
    }

    private void OpenDialog(final FeedItem item, final int adapterPosition) {
        final String id = item.getid();
        final DatabaseHandler db=new DatabaseHandler(ctx);
        db.open();

        final Dialog dialog = new Dialog(ctx, R.style.DialogStyler);
        dialog.setContentView(R.layout.big_product);
        /////////
        TextView title = (TextView) dialog.findViewById(R.id.title);
        title.setTypeface(typeface2);
        title.setText(item.getname());

        final TextView price = (TextView) dialog.findViewById(R.id.price);
        price.setTypeface(typeface2);
        int num=db.getNumProd(item.getid());
        if(num>=item.getOmde_num() && item.getOmde_num()>0){
            price.setText(Func.getCurrency(item.getOmde_price()+"") + " تومان");
        }else if (item.getprice().length() > 1 && num<item.getOmde_num())
            price.setText(Func.getCurrency(item.getprice()) + " تومان");

        ImageView imgProd = (ImageView) dialog.findViewById(R.id.img);
        final String img = item.getimg();
        if (img.length() > 5)
            Glide.with(ctx).load(ctx.getString(R.string.url) + "Opitures/" + img.replaceAll("sm","")).into(imgProd);

        ProductSabad(item, dialog, adapterPosition);

        final ImageView fav=(ImageView)dialog.findViewById(R.id.fav);
        if (db.isLiked(Integer.parseInt(id))) {
            fav.setImageDrawable(ctx.getResources().getDrawable(R.drawable.fav_on));
        } else {
            fav.setImageDrawable(ctx.getResources().getDrawable(R.drawable.fav_off));
        }
        fav.bringToFront();
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (db.isLiked(Integer.parseInt(id))) {
                        fav.setImageDrawable(ctx.getResources().getDrawable(R.drawable.fav_off));
                        db.dolike(false,id, "",  "", img);
                    } else {
                        fav.setImageDrawable(ctx.getResources().getDrawable(R.drawable.fav_on));
                        db.dolike(true, id,  "",  "", img);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    private void ProductSabad(final FeedItem item, final Dialog dialog, final int adapterPosition) {
        final String id = item.getid();
        final DatabaseHandler db = new DatabaseHandler(ctx);
        db.open();

        LinearLayout added_ln = (LinearLayout) dialog.findViewById(R.id.added_ln);
        added_ln.setVisibility(View.GONE);

        TextView addsabad = (TextView) dialog.findViewById(R.id.addsabad);
        addsabad.setVisibility(View.GONE);

        if (item.getnum().length() == 0 || item.getnum().equals("0") || item.getprice().length()<=2) {
            addsabad.setVisibility(View.VISIBLE);
            addsabad.setText("ناموجود");
        } else {
            if (db.isAddedSabad(id)) {
                added_ln.setVisibility(View.VISIBLE);

                final TextView tv_num = (TextView) dialog.findViewById(R.id.numitems);
                tv_num.setTypeface(typeface2);
                tv_num.setText(db.getNumProd(id) + "");

                ImageView plus = (ImageView) dialog.findViewById(R.id.plus);
                plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int currentNum = db.getNumProd(id);
                        int numMojjud = Integer.parseInt(item.getnum());

                        currentNum++;
                        if (currentNum <= numMojjud) {
                            list.get(adapterPosition).setTedad(currentNum + "");
                            notifyItemChanged(adapterPosition);
                            db.updateNum(Integer.parseInt(id), currentNum);
                            tv_num.setText(currentNum + "");
                            try {
                                ((MyAdapterCallBack) ctx).checkSabad();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else
                            MyToast.makeText(ctx, ctx.getString(R.string.notmojud));

                        setPrice(dialog, db, item);
                    }
                });
                ImageView mines = (ImageView) dialog.findViewById(R.id.mines);
                mines.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int currentNum = db.getNumProd(id);
                        currentNum--;
                        if (currentNum > 0) {
                            list.get(adapterPosition).setTedad(currentNum + "");
                            notifyItemChanged(adapterPosition);
                            dbSabad.updateNum(Integer.parseInt(id), currentNum);
                        } else if (currentNum == 0) {
                            dbSabad.removeFromSabad(id);
                            notifyItemChanged(adapterPosition);
                            ProductSabad(item, dialog, adapterPosition);
                        }
                        tv_num.setText(currentNum + "");
                        try {
                            ((MyAdapterCallBack) ctx).checkSabad();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        setPrice(dialog,db,item);
                    }

                });
            } else {//not addded sabad
                addsabad.setVisibility(View.VISIBLE);
                addsabad.setTypeface(typeface2);
                addsabad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(item.getOpen()==false && multiSeller)
                            MyToast.makeText(ctx,"فروشگاه بسته است");
                        else if(addSabad(id) ){
                            notifyItemChanged(adapterPosition);
                            ProductSabad(item, dialog, adapterPosition);
                        }
                    }
                });
            }
        }
    }

    private void setPrice(Dialog dialog, DatabaseHandler db, FeedItem item) {
        TextView price = (TextView) dialog.findViewById(R.id.price);
        price.setTypeface(typeface2);

        int num=db.getNumProd(item.getid());
        if(num>=item.getOmde_num() && item.getOmde_num()>0){
            price.setText(Func.getCurrency(item.getOmde_price()+"") + " تومان");
        }else if (item.getprice().length() > 1 && num<item.getOmde_num())
            price.setText(Func.getCurrency(item.getprice()) + " تومان");
    }

}
