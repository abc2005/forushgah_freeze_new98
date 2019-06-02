package com.persiandesigners.freeze;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.OnTaskFinished;

import java.util.List;

/**
 * Created by Navid on 5/12/2018.
 */

public class SabadAddressAdapter extends RecyclerView.Adapter<SabadAddressAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private List<SabadAddress_Items> list;
    private Context ctx;
    private Typeface typeface;
	int posCheked=-1;

    public SabadAddressAdapter(Context context, List<SabadAddress_Items> feedItemList) {
        if (context != null) {
            inflater = LayoutInflater.from(context);
            this.list = feedItemList;
            this.ctx = context;
            this.typeface = Func.getTypefaceNormal((Activity) context);
        }
    }

    public void addAll(List<SabadAddress_Items> catItems) {
        if (this.list == null) {
            this.list = catItems;
        } else {
            this.list.addAll(catItems);
        }
        notifyDataSetChanged();
        notifyItemInserted(list.size());
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parrent, int i) {
        View view = inflater.inflate(R.layout.sabadadress_items, parrent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }
	
	 public int getSelectedAddress() {
        if(list.size()==1)
            return 0;
        else
            return posCheked;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int position) {
        SabadAddress_Items item = list.get(position);

        viewHolder.name.setText(item.getOnvan());
        String adres="<b>آدرس تحویل سفارش : </b>"+ item.getmahaleName()+ " "+ item.getaddress();
        if(item.getcodeposti().length()>0 && !item.getcodeposti().equals("null"))
            adres+="<Br /><b>کدپستی : </b>"+ item.getcodeposti();

        if(item.getVahed().length()>0 && !item.getVahed().equals("null"))
            adres+="<Br /><b>واحد : </b>"+ item.getVahed();
        if(item.getTabaghe().length()>0 && !item.getTabaghe().equals("null"))
            adres+="<Br /><b>طبقه : </b>"+ item.getTabaghe();
        if(item.getPelak().length()>0 && !item.getPelak().equals("null"))
            adres+="<Br /><b>پلاک : </b>"+ item.getPelak();

        viewHolder.adres.setText(android.text.Html.fromHtml(
                adres+"<br /><b>شماره تماس : </b>"+ item.gettel()
        ));

        if (item.getChecked())
            viewHolder.check.setChecked(true);
        else
            viewHolder.check.setChecked(false);

        viewHolder.check.setTag(item.getid());
        viewHolder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedId=viewHolder.delete.getTag().toString();
                for (int i=0;i<list.size();i++){
                    if(list.get(i).getid().equals(selectedId)){
                        list.get(i).setChecked(true);
						posCheked=i;
                    }else{
                        list.get(i).setChecked(false);
                    }
                }
                notifyDataSetChanged();
            }
        });

        viewHolder.delete.setTag(item.getid());
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder a = new AlertDialog.Builder(ctx);
                a.setMessage("آیا از حذف این آدرس مطمئن هستید؟");
                a.setPositiveButton(("بله"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
                        new Html(new OnTaskFinished() {
                            @Override
                            public void onFeedRetrieved(String body) {
                                Log.v("this", body);
                                String selectedId=viewHolder.delete.getTag().toString();
                                for (int i=0;i<list.size();i++){

                                    if(list.get(i).getid().equals(selectedId)){
                                        list.remove(i);
                                        notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }
                        }, false, (Activity)ctx, "").execute(ctx.getString(R.string.url) + "getDelAdres.php?n=" + number+"&id="+viewHolder.delete.getTag().toString()+"&uid="+Func.getUid((Activity)ctx));


                    }
                });
                a.setNegativeButton(("خیر"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = a.show();
                TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                messageText.setGravity(Gravity.RIGHT);
                messageText.setTypeface(typeface);
            }
        });
        viewHolder.edit.setTag(item.getid());
        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SabadAddress_Items item =null;
                String selectedId=viewHolder.delete.getTag().toString();
                for (int i=0;i<list.size();i++){
                    if(list.get(i).getid().equals(selectedId)){
                        item=list.get(i);
                        notifyDataSetChanged();
                        break;
                    }
                }
                if(item!=null){
                    Intent in=new Intent (ctx,SabadKharid_s1.class);
                    in.putExtra("name",item.getname());
                    in.putExtra("onvan",item.getOnvan());
                    in.putExtra("mahaleId",item.getmahale_id());
                    in.putExtra("adres",item.getaddress());
                    in.putExtra("codeposti",item.getcodeposti());
                    in.putExtra("lat",item.getlat());
                    in.putExtra("lon",item.getlon());
                    in.putExtra("id",item.getid());
                    in.putExtra("tel",item.gettel());
                    in.putExtra("vahed",item.getVahed().replace("null",""));
                    in.putExtra("tabaghe",item.getTabaghe().replace("null",""));
                    in.putExtra("pelak",item.getPelak().replace("null",""));
                    in.putExtra("for","edit");
                    ctx.startActivity(in);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list == null)
            return 0;
        else
            return list.size();
    }

     public int getSelectedAdress() {
        if(list.size()==1)
            return 0;
        else
            return posCheked;
    }

    public SabadAddress_Items getSelectedList(int listPos) {
        return list.get(listPos);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView adres, name;
        ImageView delete, edit;
        RadioButton check;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            adres = (TextView) itemView.findViewById(R.id.adres);
            adres.setTypeface(typeface);
            name = (TextView) itemView.findViewById(R.id.name);
            name.setTypeface(typeface);
            edit = (ImageView) itemView.findViewById(R.id.edit);
            delete = (ImageView) itemView.findViewById(R.id.delete);
            check = (RadioButton) itemView.findViewById(R.id.chk);
        }

        @Override
        public void onClick(View v) {
			posCheked=getAdapterPosition();
            SabadAddress_Items item = list.get(getAdapterPosition());
            String selectedId=item.getid();
            for (int i=0;i<list.size();i++){
                if(list.get(i).getid().equals(selectedId)){
                    list.get(i).setChecked(true);
                }else{
                    list.get(i).setChecked(false);
                }
            }
            notifyDataSetChanged();
        }
    }
}
