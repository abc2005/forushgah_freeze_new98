package com.persiandesigners.freeze;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.zxing.Result;
import com.persiandesigners.freeze.Util.DatabaseHandler;
import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.HtmlPost;
import com.persiandesigners.freeze.Util.MyAdapterCallBack;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.NetworkAvailable;
import com.persiandesigners.freeze.Util.OnTaskFinished;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by navid on 6/23/2016.
 */
public class Search extends Fragment implements MyAdapterCallBack, ZXingScannerView.ResultHandler {
    Toolbar toolbar;
    Typeface typeface;
    RecyclerView rc_products;
    AsyncTask<String, Void, String> AsyncProduct;
    Boolean loadmore = true, taskrunning;
    List<FeedItem> items_products;
    MyAdapter2 productAdaper;
    ImageView progressBar;
    String urls;
    ZXingScannerView mScannerView;
    private static final String ACTION_VOICE_SEARCH = "com.google.android.gms.actions.SEARCH_ACTION";
    private static final int REQUEST_CODE = 1234;
    EditText search;
    View v;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.search, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        declare();
        actionbar();
        urls = getString(R.string.url)+"/getProductsTezol.php";
    }
    
    private void declare() {
        progressBar = (ImageView)v.findViewById(R.id.progressBar);
        Typeface typeface2 = Func.getTypeface(getActivity());

        typeface = typeface2;

        rc_products = (RecyclerView)v.findViewById(R.id.products);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;
        int columns = Math.round(dpWidth / 140);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), columns);
        rc_products.setLayoutManager(mLayoutManager);

        TextView tv1 = (TextView)v.findViewById(R.id.tv1);
        tv1.setTypeface(typeface2);
    }

    private void actionbar() {
        toolbar = (Toolbar)v.findViewById(R.id.appbar);

        Func action = new Func(getActivity());
        try {
            action.MakeActionBar(getString(R.string.search));
        } catch (Exception e) {
            e.printStackTrace();
        }
        action.hideDrawer();

        ImageView back = (ImageView)v.findViewById(R.id.back);
        back.setVisibility(View.GONE);

        ImageView imglogo = (ImageView)v.findViewById(R.id.imglogo);
        imglogo.setVisibility(View.GONE);
        ImageView imgSearch = (ImageView)v.findViewById(R.id.imgsearch);
        imgSearch.setVisibility(View.GONE);

        TextView searchtv = (TextView)v.findViewById(R.id.search_et);
        searchtv.setVisibility(View.GONE);

        LinearLayout lnsearch = (LinearLayout)v.findViewById(R.id.lnsearchs);
        lnsearch.setVisibility(View.VISIBLE);

        ImageView barcode = (ImageView)v.findViewById(R.id.barcode);
        barcode.setVisibility(View.GONE);

        search = (EditText)v.findViewById(R.id.search_et2);
        search.setTypeface(typeface);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                performSearch(s.toString());
            }
        });
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(search.getText().toString());

                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });

        ImageView img_sabad = (ImageView)v.findViewById(R.id.img_sabad);
        img_sabad.setVisibility(View.GONE);
        TextView tvnumsabad = (TextView)v.findViewById(R.id.text_numkharid);
        tvnumsabad.setVisibility(View.GONE);

        LinearLayout lnsabadbottom = (LinearLayout)v.findViewById(R.id.lnsabadbottom);
        lnsabadbottom.setVisibility(View.GONE);

        if(getResources().getBoolean(R.bool.enable_voice_search)){
            ImageView voicesearch=(ImageView)v.findViewById(R.id.voicesearch);
            voicesearch.setVisibility(View.VISIBLE);
            voicesearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     try {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa");
                    startActivityForResult(intent, REQUEST_CODE);
                } catch (Exception e) {
                    MyToast.makeText(getActivity() ,"قابلیت سرچ صوتی روی دستگاه شما وجود ندارد");
                }
                }
            });
        }
    }

//    @Override
////    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
////            ArrayList<String> datas = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
////			if(datas.size()>0) {
////            Log.v("this",datas.get(0).toString());
////			search.setText(datas.get(0));
////            performSearch(datas.get(0).toString());
////			}
////        }
////        super.onActivityResult(requestCode, resultCode, data);
////    }

    private void performSearch(String s) {
        LinearLayout beforesearch = (LinearLayout)v.findViewById(R.id.beforesearch);
        beforesearch.setVisibility(View.GONE);
        if (s.length() == 0) {
            productAdaper = null;
            if (AsyncProduct != null)
                AsyncProduct.cancel(true);
            rc_products.setAdapter(null);
            beforesearch.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else if (s.length() >= 2) {
            progressBar.setVisibility(View.VISIBLE);
            if (AsyncProduct != null)
                AsyncProduct.cancel(true);

            taskrunning = true;

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("search", s);
            String query = builder.build().getEncodedQuery();
            AsyncProduct = new HtmlPost(new OnTaskFinished() {
                @Override
                public void onFeedRetrieved(String body) {
                    taskrunning = false;
                    productAdaper = null;
                    if (body.equals("errordade")) {
                        MyToast.makeText(getActivity(), "اتصال اینترنت خود را بررسی کنید");
                    } else {
                        items_products = Func.parseResult(body);
                        productAdaper = new MyAdapter2(getActivity(), items_products);
                        rc_products.setAdapter(productAdaper);
                        productAdaper.setFullWidth(true);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }, false, getActivity(), "", query).execute(urls + "?for=search");
        }

        ImageView imglogo = (ImageView)v.findViewById(R.id.imglogo);
        imglogo.setVisibility(View.GONE);
    }


    @Override
    public void checkSabad() {
        actionbar();

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void getScan() {

    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {

        if (permsRequestCode == 2) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getScan();
            } else {
                // permission denied
            }
            return;
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("this", rawResult.getText()); // Prints scan results
        Log.v("this", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        Toast.makeText(getActivity(), "کد محصول : " + rawResult.getText().toString(), Toast.LENGTH_LONG).show();
        if (rawResult.getText() != null && rawResult.getText().toString().length() > 0) {
            if (mScannerView != null) {
                mScannerView.stopCamera();           // Stop camera on pause
            }
            if (NetworkAvailable.isNetworkAvailable(getActivity())) {
//                setContentView(R.layout.search);
				actionbar();
                long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
                new Html(new OnTaskFinished() {
                    @Override
                    public void onFeedRetrieved(String body) {
                        Log.v("this", body);
                        if (body.equals("errordade")) {

                        } else if (body.contains("#not")) {
                            MyToast.makeText(getActivity(), "محصول با باکد اسکن شده موجود نیست");
                        } else {
                            List<FeedItem> productItem = Func.parseResult(body);
                            try {
                                OpenDialog(productItem.get(0));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, true, getActivity(), "").execute(getString(R.string.url)
                        + "/getProductsTezol.php?n=" + number + "&pid=" + rawResult.getText().toString());
                Log.v("this",getString(R.string.url)
                        + "/getProductsTezol.php?n=" + number + "&pid=" + rawResult.getText().toString());
            } else {
                Toast.makeText(getActivity(), "اتصال اینترنت جهت انجام عملیات وجود ندارد", Toast.LENGTH_LONG).show();
            }

        }
        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }

    private void OpenDialog(final FeedItem item) {
        final String id = item.getid();
        final DatabaseHandler db = new DatabaseHandler(getActivity());
        db.open();

        final Dialog dialog = new Dialog(getActivity(), R.style.DialogStyler);
        dialog.setContentView(R.layout.big_product);
        /////////
        TextView title = (TextView) dialog.findViewById(R.id.title);
        title.setTypeface(typeface);
        title.setText(item.getname());

        final TextView price = (TextView) dialog.findViewById(R.id.price);
        price.setTypeface(typeface);
        if (item.getprice().length() > 2)
            price.setText(Func.getCurrency(item.getprice()) + " تومان");

        ImageView imgProd = (ImageView) dialog.findViewById(R.id.img);
        final String img = item.getimg();
        if (img.length() > 5)
            Glide.with(getActivity()).load(getActivity().getString(R.string.url) + "Opitures/" + img).into(imgProd);

        ProductSabad(item, dialog);

        final ImageView fav = (ImageView) dialog.findViewById(R.id.fav);
        if (db.isLiked(Integer.parseInt(id))) {
            fav.setImageDrawable(getResources().getDrawable(R.drawable.fav_on));
        } else {
            fav.setImageDrawable(getResources().getDrawable(R.drawable.fav_off));
        }
        fav.bringToFront();
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (db.isLiked(Integer.parseInt(id))) {
                        fav.setImageDrawable(getResources().getDrawable(R.drawable.fav_off));
                        db.dolike(false, id, "", "", img);
                    } else {
                        fav.setImageDrawable(getResources().getDrawable(R.drawable.fav_on));
                        db.dolike(true, id, "", "", img);
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

    private void ProductSabad(final FeedItem item, final Dialog dialog) {
        final String id = item.getid();
        final DatabaseHandler db = new DatabaseHandler(getActivity());
        db.open();

        LinearLayout added_ln = (LinearLayout) dialog.findViewById(R.id.added_ln);
        added_ln.setVisibility(View.GONE);

        TextView addsabad = (TextView) dialog.findViewById(R.id.addsabad);
        addsabad.setVisibility(View.GONE);

        if (item.getnum().length() == 0 || item.getnum().equals("0") || item.getprice().length() <= 2) {
            addsabad.setVisibility(View.VISIBLE);
            addsabad.setText("ناموجود");
        } else {
            if (db.isAddedSabad(id)) {
                added_ln.setVisibility(View.VISIBLE);

                final TextView tv_num = (TextView) dialog.findViewById(R.id.numitems);
                tv_num.setTypeface(typeface);
                tv_num.setText(db.getNumProd(id) + "");

                ImageView plus = (ImageView) dialog.findViewById(R.id.plus);
                plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int currentNum = db.getNumProd(id);
                        int numMojjud = Integer.parseInt(item.getnum());

                        currentNum++;
                        if (currentNum <= numMojjud) {
                            item.setTedad(currentNum + "");
                            db.updateNum(Integer.parseInt(id), currentNum);
                            tv_num.setText(currentNum + "");
                            checkSabad();
                        } else
                            MyToast.makeText(getActivity(), getString(R.string.notmojud));
                    }
                });
                ImageView mines = (ImageView) dialog.findViewById(R.id.mines);
                mines.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int currentNum = db.getNumProd(id);
                        currentNum--;
                        if (currentNum > 0) {
                            item.setTedad(currentNum + "");
                            db.updateNum(Integer.parseInt(id), currentNum);
                        } else if (currentNum == 0) {
                            db.removeFromSabad(id);
                            ProductSabad(item, dialog);
                        }
                        tv_num.setText(currentNum + "");
                        checkSabad();
                    }
                });
            } else {//not addded sabad
                addsabad.setVisibility(View.VISIBLE);
                addsabad.setTypeface(typeface);
                addsabad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addSabad(id, item);
                        ProductSabad(item, dialog);
                    }
                });
            }
        }
    }

    private void addSabad(String prodID, FeedItem item) {
        final DatabaseHandler db = new DatabaseHandler(getActivity());
        db.open();

        int prdId = Integer.parseInt(item.getid());
        if (db.checkAddedBefore(prdId) <= 0) {
            Boolean stat=db.sabadkharid(item.getname(), item.getimg()
                    , Integer.parseInt(item.getid()), item.getprice()
                    , item.getnum(), item.getTedad(),item.getprice2(),"",item.getCatId(),item.getOmde_num(),item.getOmde_price(), item.getShopId());
            if(Func.isMultiSeller(getActivity()) && stat==false){
                Func.AlertMultipleStore(getActivity());
            }else {
                checkSabad();
            }

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mScannerView != null) {
            mScannerView.stopCamera();           // Stop camera on pause
        }
    }
}
